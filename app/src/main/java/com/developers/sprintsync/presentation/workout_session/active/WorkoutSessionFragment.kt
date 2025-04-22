package com.developers.sprintsync.presentation.workout_session.active

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
import com.developers.sprintsync.presentation.components.MapStyle
import com.developers.sprintsync.presentation.workout_session.active.util.map.MapCameraManager
import com.developers.sprintsync.presentation.workout_session.active.util.map.MapSnapshotCreator
import com.developers.sprintsync.presentation.workout_session.active.util.map.MapSnapshotPreparer
import com.developers.sprintsync.presentation.workout_session.active.util.map.MarkerManager
import com.developers.sprintsync.presentation.workout_session.active.util.metrics_formatter.UiMetrics
import com.developers.sprintsync.presentation.workout_session.active.util.service.TrackingServiceController
import com.developers.sprintsync.presentation.workout_session.active.util.state_handler.event.UIEvent
import com.developers.sprintsync.presentation.workout_session.active.util.state_handler.map.MapUiState
import com.developers.sprintsync.presentation.workout_session.active.util.state_handler.ui.UIState
import com.developers.sprintsync.presentation.workout_session.active.util.tracking_panel.TrackingPanelController
import com.developers.sprintsync.presentation.workout_session.active.util.tracking_panel.TrackingPanelState
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLngBounds
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WorkoutSessionFragment : Fragment() {
    private var _binding: FragmentTrackingBinding? = null
    private val binding get() = checkNotNull(_binding) { getString(R.string.binding_init_error) }

    private val viewModel by viewModels<WorkoutSessionViewModel>()

    private var _map: GoogleMap? = null
    private val map get() = checkNotNull(_map) { getString(R.string.map_init_error) }

    private val mapCamera = MapCameraManager()
    private val mapMarker: MarkerManager by lazy { MarkerManager(requireContext().getBitmapDescriptor(R.drawable.ic_user_location)) }

    private val serviceController by lazy { TrackingServiceController(requireContext()) }

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
        bindGeneralLoadingOverlay()
        showInitialLoading()
        setMapLoadingOverlay()
        binding.mapView.onCreate(savedInstanceState)
        initializeTrackingPanel()
        initializeMap()
        observeUIStaticState()
        observeTrackingDuration()
        setBackButtonListener()
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart")
        serviceController.bind(requireActivity()) { connectionResult ->
            viewModel.bindTo(connectionResult)
        }
        serviceController.launchLocationUpdates()
        binding.mapView.onStart()
    }

    private fun initializeTrackingPanel() {
        _trackingPanel =
            TrackingPanelController(
                binding.btTrackingController,
                onStart = { serviceController.startService() },
                onPause = { serviceController.pauseService() },
                onFinish = { serviceController.finishService() },
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
        collectFlow(viewModel.uiStateFlow) { state ->
            updatePauseCardVisibility(state)
            when (state) {
                UIState.Initialized -> {
                    binding.generalLoadingOverlay.hide()
                    trackingPanel.updateState(TrackingPanelState.Initialized)
                }

                UIState.Active -> {
                    trackingPanel.updateState(TrackingPanelState.Active)
                }

                UIState.Paused -> {
                    trackingPanel.updateState(TrackingPanelState.Paused)
                }

                UIState.Completing -> {
                    showCompletingLoading()
                }
            }
        }
    }

    private fun observeUIEvents() {
        collectFlow(viewModel.uiEventFlow) { event ->
            when (event) {
                is UIEvent.UpdateTrackingUi -> {
                    updateTrackMetrics(event.metrics)
                    event.polylines.forEach { map.addPolyline(it) }
                }

                is UIEvent.RequestSnapshot -> {
                    prepareMapForSnapshot(event.bounds)
                    MapSnapshotCreator.createSnapshot(map) { bitmap ->
                        viewModel.onSnapshotReady(bitmap)
                    }
                }

                is UIEvent.NavigateToSummary -> navigateToSessionSummary(event.trackId)
                is UIEvent.ErrorAndClose -> {
                    Toast
                        .makeText(requireContext(), event.message, Toast.LENGTH_LONG)
                        .show() // TODO handle error event
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
        collectFlow(viewModel.durationFlow) { duration ->
            updateDuration(duration)
        }

    private fun observeMapState() {
        val loadingView = binding.mapLoadingOverlay
        collectFlow(viewModel.mapStateFlow) { state ->
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

    private fun bindGeneralLoadingOverlay() = binding.generalLoadingOverlay.bindToLifecycle(lifecycle)

    private fun showInitialLoading() {
        binding.generalLoadingOverlay.apply {
            setLoadingMessage()
            show()
        }
    }

    private fun showCompletingLoading() {
        binding.generalLoadingOverlay.apply {
            setLoadingMessage(context.getString(R.string.completing_track_message))
            show()
        }
    }

    private fun setMapLoadingOverlay() =
        binding.mapLoadingOverlay.apply {
            bindToLifecycle(lifecycle)
            setLoadingMessage(getString(R.string.tracking_map_loading_message))
        }

    private fun navigateToSessionSummary(trackId: Int) {
        val action = WorkoutSessionFragmentDirections.actionTrackingFragmentToSessionSummaryFragment(trackId)
        findTopNavController().navigate(action)
    }

    private fun popBackStack() = findTopNavController().popBackStack()

    companion object {
        private const val TAG = "My Stack: TrackingFragment"
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
        serviceController.unbind(requireActivity())
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
