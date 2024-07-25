package com.developers.sprintsync.tracking.session.ui

import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.developers.sprintsync.R
import com.developers.sprintsync.databinding.FragmentTrackingBinding
import com.developers.sprintsync.global.util.extension.findTopNavController
import com.developers.sprintsync.tracking.analytics.dataManager.formatter.CaloriesFormatter
import com.developers.sprintsync.tracking.analytics.dataManager.formatter.DurationFormatter
import com.developers.sprintsync.tracking.analytics.dataManager.mapper.indicator.DistanceMapper
import com.developers.sprintsync.tracking.analytics.dataManager.mapper.indicator.PaceMapper
import com.developers.sprintsync.tracking.analytics.ui.map.util.MapManager
import com.developers.sprintsync.tracking.session.model.session.TrackStatus
import com.developers.sprintsync.tracking.session.model.session.TrackerState
import com.developers.sprintsync.tracking.session.model.track.Segment
import com.developers.sprintsync.tracking.session.model.track.Track
import com.developers.sprintsync.tracking.session.model.track.toLatLng
import com.developers.sprintsync.tracking.session.service.manager.TrackingServiceController
import com.developers.sprintsync.tracking.session.ui.useCase.ManageMapCameraUseCase
import com.developers.sprintsync.tracking.session.viewModel.TrackingSessionViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TrackingFragment : Fragment() {
    private var _binding: FragmentTrackingBinding? = null
    private val binding get() = checkNotNull(_binding) { getString(R.string.binding_init_error) }

    private val sessionViewModel by activityViewModels<TrackingSessionViewModel>()

    private val service by lazy { TrackingServiceController(requireContext()) }

    private val mapManager: MapManager by lazy { MapManager(requireContext()) }

    @Inject
    lateinit var manageMapCameraUseCase: ManageMapCameraUseCase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentTrackingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated")
        initLoadingViews()
        initMap(savedInstanceState) {
            mapManager.setMapStyle(sessionViewModel.minimalMapStyle)
            setTrackDataObservers()
            getActiveSegments()?.let {
                mapManager.addPolylines(it)
                mapManager.moveCameraToLocation(it.last().endLocation.toLatLng())
            }
        }
        setTrackerStateObservers()
        setDurationObserver()
        setTrackStatusObserver()
        setFinishButtonListener()
        setBackButtonListener()
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart")
        sessionViewModel.startUpdatingLocation()
        binding.mapView.onStart()
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause")
        binding.mapView.onPause()
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")
        binding.mapView.onResume()
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop")
        sessionViewModel.stopUpdatingLocation()
        binding.mapView.onStop()
    }

    private fun initMap(
        savedInstanceState: Bundle?,
        onMapReady: () -> Unit,
    ) {
        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync {
            mapManager.initialize(it)
            onMapReady()
        }
    }

    private fun initLoadingViews() {
        updateGeneralLoadingVisibility(false)
        updateMapLoadingVisibility(true)
    }

    private fun setTrackerStateObservers() {
        sessionViewModel.trackerState.observe(viewLifecycleOwner) { state ->
            updateTrackingControllerPanel(state)
        }
    }

    private fun setTrackDataObservers() {
        setCurrentLocationObserver()
        setTrackObserver()
    }

    private fun setDurationObserver() {
        sessionViewModel.duration.observe(viewLifecycleOwner) { duration ->
            updateDuration(duration)
        }
    }

    private fun setCurrentLocationObserver() {
        sessionViewModel.currentLocation.observe(viewLifecycleOwner) { location ->
            val latLng = location?.toLatLng()
            latLng?.let {
                if (mapLoadingIsVisible()) {
                    updateMapLoadingVisibility(false)
                }
                mapManager.updateUserMarker(it)
                manageMapCameraUseCase(mapManager, it)
            }
        }
    }

    private fun setTrackObserver() {
        sessionViewModel.track.observe(viewLifecycleOwner) { track ->
            updateTrackingData(track)
            track.segments.lastOrNull()?.let { mapManager.addPolyline(it) }
        }
    }

    private fun setTrackStatusObserver() {
        sessionViewModel.trackStatus.observe(viewLifecycleOwner) { status ->
            Log.d(TAG, "Track status : $status")
            when (status) {
                is TrackStatus.Valid -> {
                    updateGeneralLoadingVisibility(true)
                    sessionViewModel.track.value?.let { track ->
                        val targetWidth = resources.getDimensionPixelSize(R.dimen.mapPreview_width)
                        val targetHeight =
                            resources.getDimensionPixelSize(R.dimen.mapPreview_height)
                        mapManager.captureTrackSnapshot(
                            track.segments,
                            binding.mapView.width,
                            binding.mapView.height,
                            targetWidth,
                            targetHeight,
                            sessionViewModel.unlabeledMapStyle,
                        ) { bitmap ->
                            sessionViewModel.saveTrack(track.copy(mapPreview = bitmap))
                            Log.d(TAG, "setTrackStatusObserver: Track saved")
                            navigateToSessionSummary()
                        }
                    }
                }

                is TrackStatus.Invalid -> {
                    navigateUp()
                }

                is TrackStatus.Incomplete -> {
                    // NO - OP
                }
            }
        }
    }

    private fun updateDuration(durationMillis: Long) {
        binding.tvDuration.text = DurationFormatter.formatToHhMmSs(durationMillis)
    }

    private fun updateTrackingData(track: Track) {
        binding.apply {
            tvDistanceValue.text =
                DistanceMapper.metersToPresentableKilometers(track.distanceMeters)
            tvCaloriesValue.text = CaloriesFormatter.formatCalories(track.calories)
            val currentPace = getPace(track)
            tvPaceValue.text = PaceMapper.formatPaceWithTwoDecimals(currentPace)
        }
    }

    private fun getPace(track: Track): Float =
        when {
            track.segments.isNotEmpty() && track.segments.last() is Segment.ActiveSegment -> {
                (track.segments.last() as Segment.ActiveSegment).pace
            }

            else -> 0f
        }

    private fun updateTrackingControllerPanel(state: TrackerState) {
        Log.d(TAG, "updateTrackingControllerPanel: $state")
        when (state) {
            TrackerState.Initialised -> {
                initTrackingControllerButton()
                setPauseCardVisibility(false)
                setFinishButtonVisibility(false)
            }

            TrackerState.Tracking -> {
                updateTrackingControllerButton(true)
                setPauseCardVisibility(false)
                setFinishButtonVisibility(true)
            }

            TrackerState.Paused -> {
                updateTrackingControllerButton(false)
                setPauseCardVisibility(true)
                setFinishButtonVisibility(true)
            }

            TrackerState.Finished -> {
                // NO - OP
            }
        }
    }

    private fun setPauseCardVisibility(isVisible: Boolean) {
        when (isVisible) {
            true -> binding.pauseCard.visibility = View.VISIBLE
            false -> binding.pauseCard.visibility = View.GONE
        }
    }

    private fun setFinishButtonVisibility(isVisible: Boolean) {
        when (isVisible) {
            true -> binding.btFinish.visibility = View.VISIBLE
            false -> binding.btFinish.visibility = View.GONE
        }
    }

    private fun setFinishButtonListener() {
        binding.btFinish.setOnClickListener {
            service.finish()
        }
    }

    private fun initTrackingControllerButton() {
        updateTextTrackingControllerVisibility(true)
        updateImageTrackingControllerVisibility(false)
        updateTrackingControllerListener(false)
        initTextTrackingController()
        updateTrackingControllerButtonBackground(false)
    }

    private fun updateTrackingControllerButton(isTracking: Boolean) {
        when (isTracking) {
            true ->
                binding.apply {
                    updateTextTrackingControllerVisibility(false)
                    updateTrackingControllerListener(true)
                    updateImageTrackingControllerVisibility(true)
                    updateTrackingControllerButtonBackground(true)
                    updateImageTrackingController(true)
                }

            false ->
                binding.apply {
                    updateTextTrackingControllerVisibility(false)
                    updateTrackingControllerListener(false)
                    updateImageTrackingControllerVisibility(true)
                    updateTrackingControllerButtonBackground(false)
                    updateImageTrackingController(false)
                }
        }
    }

    private fun updateTrackingControllerListener(isTracking: Boolean) {
        when (isTracking) {
            true ->
                binding.btTrackingController.setOnClickListener {
                    service.pauseService()
                }

            false ->
                binding.btTrackingController.setOnClickListener {
                    service.startService()
                }
        }
    }

    private fun updateTextTrackingControllerVisibility(isVisible: Boolean) {
        when (isVisible) {
            true -> binding.tvTrackingController.visibility = View.VISIBLE
            false -> binding.tvTrackingController.visibility = View.GONE
        }
    }

    private fun initTextTrackingController() {
        binding.tvTrackingController.text = getString(R.string.start)
    }

    private fun updateImageTrackingControllerVisibility(isVisible: Boolean) {
        when (isVisible) {
            true -> binding.imTrackingController.visibility = View.VISIBLE
            false -> binding.imTrackingController.visibility = View.GONE
        }
    }

    private fun updateImageTrackingController(isTracking: Boolean) {
        when (isTracking) {
            true -> binding.imTrackingController.setImageResource(R.drawable.ic_pause_48dp)
            false -> binding.imTrackingController.setImageResource(R.drawable.ic_start_48dp)
        }
    }

    private fun updateTrackingControllerButtonBackground(isTracking: Boolean) {
        when (isTracking) {
            true -> binding.btTrackingController.setBackgroundResource(R.drawable.bt_circle_thirdhly)
            false -> binding.btTrackingController.setBackgroundResource(R.drawable.bt_circle_secondary)
        }
    }

    private fun setBackButtonListener() {
        binding.btBack.setOnClickListener {
            findTopNavController().navigateUp()
        }
    }

    private fun updateMapLoadingVisibility(isVisible: Boolean) {
        val loadingBar = (binding.mapLoadingBar.drawable as? AnimatedVectorDrawable)
        when (isVisible) {
            true -> {
                loadingBar?.start()
                binding.mapLoadingView.visibility = View.VISIBLE
            }

            false -> {
                loadingBar?.stop()
                binding.mapLoadingView.visibility = View.GONE
            }
        }
    }

    private fun mapLoadingIsVisible(): Boolean = binding.mapLoadingView.visibility == View.VISIBLE

    private fun updateGeneralLoadingVisibility(isVisible: Boolean) {
        val loadingBar = (binding.generalLoadingBar.drawable as? AnimatedVectorDrawable)
        when (isVisible) {
            true -> {
                loadingBar?.start()
                binding.generalLoadingView.visibility = View.VISIBLE
            }

            false -> {
                loadingBar?.stop()
                binding.generalLoadingView.visibility = View.GONE
            }
        }
    }

    private fun getActiveSegments(): List<Segment.ActiveSegment>? =
        sessionViewModel.track.value
            ?.segments
            ?.takeIf { it.isNotEmpty() }
            ?.filterIsInstance<Segment.ActiveSegment>()

    private fun navigateToSessionSummary() {
        findTopNavController().navigate(R.id.action_trackingFragment_to_sessionSummaryFragment)
    }

    private fun navigateUp() = findTopNavController().navigateUp()

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView")
        binding.mapView.onDestroy()
        sessionViewModel.onDestroy()
        mapManager.clear()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
    }

    companion object {
        private const val TAG = "My Stack: TrackingFragment"
    }
}
