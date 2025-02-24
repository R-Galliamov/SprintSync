package com.developers.sprintsync.tracking_session.presentation.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.developers.sprintsync.R
import com.developers.sprintsync.core.util.extension.collectFlow
import com.developers.sprintsync.core.util.extension.findTopNavController
import com.developers.sprintsync.core.util.extension.getBitmapDescriptor
import com.developers.sprintsync.core.util.extension.setMapStyle
import com.developers.sprintsync.databinding.FragmentTrackingBinding
import com.developers.sprintsync.map.data.model.MapStyle
import com.developers.sprintsync.tracking.service.controller.TrackingServiceController
import com.developers.sprintsync.tracking_session.presentation.util.state_handler.event.UIEvent
import com.developers.sprintsync.tracking_session.presentation.util.state_handler.ui.UIState
import com.developers.sprintsync.tracking_session.presentation.util.metrics_formatter.UiMetrics
import com.developers.sprintsync.tracking_session.presentation.util.map.MapCameraManager
import com.developers.sprintsync.tracking_session.presentation.util.map.MapSnapshotCreator
import com.developers.sprintsync.tracking_session.presentation.util.map.MapSnapshotPreparer
import com.developers.sprintsync.tracking_session.presentation.util.map.MarkerManager
import com.developers.sprintsync.tracking_session.presentation.view.TrackingPanelController
import com.developers.sprintsync.tracking_session.presentation.view.TrackingPanelState
import com.developers.sprintsync.tracking_session.presentation.util.state_handler.map.MapUiState
import com.developers.sprintsync.tracking_session.presentation.view_model.TrackingViewModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLngBounds
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrackingFragment : Fragment() {
    private var _binding: FragmentTrackingBinding? = null
    private val binding get() = checkNotNull(_binding) { getString(R.string.binding_init_error) }

    private val sessionViewModel by viewModels<TrackingViewModel>()

    private var _map: GoogleMap? = null
    private val map get() = checkNotNull(_map) { getString(R.string.map_init_error) }

    private val mapCamera = MapCameraManager()
    private val mapMarker: MarkerManager by lazy { MarkerManager(requireContext().getBitmapDescriptor(R.drawable.ic_user_location)) }

    private val service by lazy { TrackingServiceController(requireContext()) }

    private var _trackingPanel: TrackingPanelController? = null
    private val trackingPanel get() = checkNotNull(_trackingPanel) { "Tracking panel isn't initialized" }

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
        binding.loadingOverlay.bindToLifecycle(lifecycle)
        binding.mapLoadingOverlay.bindToLifecycle(lifecycle)
        binding.mapView.onCreate(savedInstanceState)
        initializeTrackingPanel()
        initializeMap()
        observeUIStaticState()
        observeTrackingDuration()
        setBackButtonListener()
    }

    private fun initializeTrackingPanel() {
        _trackingPanel =
            TrackingPanelController(
                binding.btTrackingController,
                onStart = { service.startService() },
                onPause = { service.pauseService() },
                onFinish = { service.finish() },
            )
    }

    private fun initializeMap() {
        binding.mapView.getMapAsync { map ->
            mapCamera.attachMap(map)
            map.setMapStyle(requireContext(), MapStyle.MINIMAL, TAG)
            _map = map
            observeUIEvents()
            observeMapState()
        }
    }

    private fun observeUIStaticState() {
        collectFlow(sessionViewModel.uiStateFlow) { state ->
            updatePauseCardVisibility(state)
            when (state) {
                UIState.Initialized -> {
                    trackingPanel.updateState(TrackingPanelState.Initialized)
                }

                UIState.Active -> {
                    trackingPanel.updateState(TrackingPanelState.Active)
                }

                UIState.Paused -> {
                    trackingPanel.updateState(TrackingPanelState.Paused)
                }

                UIState.Completing -> {
                    binding.loadingOverlay.show()
                }
            }
        }
    }

    private fun observeUIEvents() {
        collectFlow(sessionViewModel.uiEventFlow) { event ->
            when (event) {
                is UIEvent.UpdateTrackingUi -> {
                    updateTrackMetrics(event.metrics)
                    map.addPolyline(event.polylines)
                }

                is UIEvent.RequestSnapshot -> {
                    prepareMapForSnapshot(event.bounds)
                    MapSnapshotCreator.createSnapshot(map) { bitmap ->
                        sessionViewModel.onSnapshotReady(bitmap)
                    }
                }

                is UIEvent.NavigateToSummary -> navigateToSessionSummary(event.trackId)
                UIEvent.ErrorAndClose -> {
                    Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show() // TODO handle error event
                    popBackStack()
                }
            }
        }
    }

    private fun prepareMapForSnapshot(bounds: LatLngBounds) {
        // val padding = MapCalculations.calculateTrackPadding(binding.mapView.width, binding.mapView.height)
        MapSnapshotPreparer.prepareMap(
            context = requireContext(),
            map = map,
            bounds = bounds,
            padding = 100, // TODO test different paddings
            marker = mapMarker.marker,
            mapStyle = MapStyle.UNLABELED,
        )
    }

    private fun observeTrackingDuration() =
        collectFlow(sessionViewModel.duration) { duration ->
            updateDuration(duration)
        }

    private fun observeMapState() {
        val loadingView = binding.mapLoadingOverlay
        collectFlow(sessionViewModel.mapStateFlow) { state ->
            when (state) {
                MapUiState.Loading -> loadingView.show()
                is MapUiState.Active -> {
                    if (binding.mapLoadingOverlay.isVisible) loadingView.hide()
                    mapMarker.setMarker(map, state.location)
                    mapCamera.moveCamera(state.location)
                }
            }
        }
    }

    private fun updateDuration(duration: String) {
        binding.tvDuration.text = duration
    }

    private fun updateTrackMetrics(track: UiMetrics) {
        binding.apply {
            tvDistanceValue.text = track.distance
            tvCaloriesValue.text = track.caloriesBurned
            tvPaceValue.text = track.pace
        }
    }

    private fun setBackButtonListener() {
        binding.btBack.setOnClickListener {
            popBackStack()
        }
    }

    private fun updatePauseCardVisibility(state: UIState) {
        binding.pauseCard.visibility =
            when (state) {
                UIState.Paused -> View.VISIBLE
                else -> View.GONE
            }
    }

    private fun navigateToSessionSummary(trackId: Int) {
        val action = TrackingFragmentDirections.actionTrackingFragmentToSessionSummaryFragment(trackId)
        findTopNavController().navigate(action)
    }

    private fun popBackStack() = findTopNavController().popBackStack()

    companion object {
        private const val TAG = "My Stack: TrackingFragment"
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart")
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
        binding.mapView.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView")
        binding.mapView.onDestroy()
        mapCamera.detachMap()
        _trackingPanel = null
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
    }
}
