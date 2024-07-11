package com.developers.sprintsync.tracking.ui

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
import com.developers.sprintsync.tracking.mapper.indicator.DistanceMapper
import com.developers.sprintsync.tracking.mapper.indicator.PaceMapper
import com.developers.sprintsync.tracking.mapper.indicator.TimeMapper
import com.developers.sprintsync.tracking.model.Segment
import com.developers.sprintsync.tracking.model.Segments
import com.developers.sprintsync.tracking.model.Track
import com.developers.sprintsync.tracking.model.TrackerState
import com.developers.sprintsync.tracking.model.toLatLng
import com.developers.sprintsync.tracking.service.manager.TrackingServiceController
import com.developers.sprintsync.tracking.util.map.MapManager
import com.developers.sprintsync.tracking.viewModel.TrackingSessionViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrackingFragment : Fragment() {
    private var _binding: FragmentTrackingBinding? = null
    private val binding get() = checkNotNull(_binding) { getString(R.string.binding_init_error) }

    private val sessionViewModel by activityViewModels<TrackingSessionViewModel>()

    private val service by lazy { TrackingServiceController(requireContext()) }

    private val mapManager: MapManager by lazy { MapManager(requireContext()) }

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
        updateProgressBarVisibility(true)
        initMap(savedInstanceState) {
            updateProgressBarVisibility(false)
            setTrackDataObservers()
            getNonEmptySegments()?.let {
                mapManager.addPolylines(it)
                mapManager.moveCameraToSegments(it)
            }
        }
        setTrackerStateObservers()
        setDurationObserver()
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
                mapManager.updateUserMarker(it)
                mapManager.moveCameraToLocation(it)
            }
        }
    }

    private fun setTrackObserver() {
        sessionViewModel.track.observe(viewLifecycleOwner) { track ->
            updateTrackingData(track)
            track.segments.lastOrNull()?.let { mapManager.addPolyline(it) }
        }
    }

    private fun updateDuration(durationMillis: Long) {
        binding.tvDuration.text = TimeMapper.millisToPresentableTime(durationMillis)
    }

    private fun updateTrackingData(track: Track) {
        binding.apply {
            tvDistanceValue.text =
                DistanceMapper.metersToPresentableKilometers(track.distanceMeters)
            tvCaloriesValue.text = track.calories.toString()
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
                when (sessionViewModel.isTrackValid()) {
                    true -> findTopNavController().navigate(R.id.action_trackingFragment_to_sessionSummaryFragment)
                    false -> findTopNavController().navigateUp()
                }
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

    private fun updateProgressBarVisibility(isVisible: Boolean) {
        when (isVisible) {
            true -> binding.progressBar.visibility = View.VISIBLE
            false -> binding.progressBar.visibility = View.GONE
        }
    }

    private fun getNonEmptySegments(): Segments? =
        sessionViewModel.track.value
            ?.segments
            ?.takeIf { it.isNotEmpty() }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView")
        binding.mapView.onDestroy()
        _binding = null
    }

    companion object {
        private const val TAG = "Tracking Fragment"
    }
}
