package com.developers.sprintsync.tracking.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.developers.sprintsync.R
import com.developers.sprintsync.databinding.FragmentTrackingBinding
import com.developers.sprintsync.tracking.mapper.indicator.DistanceMapper
import com.developers.sprintsync.tracking.mapper.indicator.PaceMapper
import com.developers.sprintsync.tracking.mapper.indicator.TimeMapper
import com.developers.sprintsync.tracking.model.Segment
import com.developers.sprintsync.tracking.model.Track
import com.developers.sprintsync.tracking.model.TrackerState
import com.developers.sprintsync.tracking.model.toLatLng
import com.developers.sprintsync.tracking.service.TrackingServiceController
import com.developers.sprintsync.tracking.util.manager.MapManager
import com.developers.sprintsync.tracking.viewModel.TrackingViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrackingFragment : Fragment() {
    private var _binding: FragmentTrackingBinding? = null
    private val binding get() = checkNotNull(_binding) { getString(R.string.binding_init_error) }

    private val viewModel by activityViewModels<TrackingViewModel>()

    private val service: TrackingServiceController by lazy {
        TrackingServiceController(
            requireContext(),
        )
    }

    private var mapManager: MapManager? = null

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
        setObservers()
        initMapManager()
        initMap()
        binding.mapView.onCreate(savedInstanceState)
    }

    private fun initMapManager() {
        mapManager = MapManager(requireContext())
    }

    private fun initMap() {
        binding.mapView.getMapAsync {
            mapManager?.initialize(it)
        }
    }

    private fun setObservers() {
        setTrackerStateObservers()
        setDataObservers()
    }

    private fun setTrackerStateObservers() {
        viewModel.trackerState.observe(viewLifecycleOwner) { state ->
            updateTrackingControllerPanel(state)
        }
    }

    private fun setDataObservers() {
        setDurationObserver()
        setCurrentLocationObserver()
        setTrackObserver()
    }

    private fun setDurationObserver() {
        viewModel.duration.observe(viewLifecycleOwner) { duration ->
            updateDuration(duration)
        }
    }

    private fun setCurrentLocationObserver() {
        viewModel.currentLocation.observe(viewLifecycleOwner) { location ->
            val latLng = location?.toLatLng()
            latLng?.let {
                mapManager?.updateUserMarker(it)
                mapManager?.moveCameraToLocation(it)
            }
        }
    }

    private fun setTrackObserver() {
        viewModel.track.observe(viewLifecycleOwner) { track ->
            track.segments.lastOrNull { it is Segment.ActiveSegment }
            updateTrackingData(track)
            track.segments.lastOrNull()?.let { mapManager?.addPolyline(it) }
        }
    }

    private fun updateDuration(durationMillis: Long) {
        binding.tvDuration.text = TimeMapper.millisToPresentableTime(durationMillis)
    }

    private fun updateTrackingData(track: Track) {
        binding.apply {
            tvDistanceValue.text = DistanceMapper.metersToPresentableDistance(track.distanceMeters)
            tvCaloriesValue.text = track.calories.toString()
            val currentPace = getPace(track)
            tvPaceValue.text = PaceMapper.paceToPresentablePace(currentPace)
        }
    }

    private fun getPace(track: Track): Float {
        return when {
            track.segments.isNotEmpty() && track.segments.last() is Segment.ActiveSegment -> {
                (track.segments.last() as Segment.ActiveSegment).pace
            }

            else -> 0f
        }
    }

    private fun updateTrackingControllerPanel(state: TrackerState) {
        when (state) {
            TrackerState.Initialised -> {
                initTrackingControllerButton()
                initFinishButtonListener()
                setFinishButtonVisibility(false)
            }

            TrackerState.Tracking -> {
                updateTrackingControllerButton(true)
                setFinishButtonVisibility(true)
            }

            TrackerState.Paused -> {
                updateTrackingControllerButton(false)
                setFinishButtonVisibility(true)
            }

            TrackerState.Finished -> {
                // NO - OP
            }
        }
    }

    private fun setFinishButtonVisibility(isVisible: Boolean) {
        when (isVisible) {
            true -> binding.btFinish.visibility = View.VISIBLE
            false -> binding.btFinish.visibility = View.GONE
        }
    }

    private fun initFinishButtonListener() {
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

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mapManager?.cleanup()
        mapManager = null
    }
}
