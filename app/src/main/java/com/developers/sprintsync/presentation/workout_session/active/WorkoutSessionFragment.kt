package com.developers.sprintsync.presentation.workout_session.active

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.developers.sprintsync.R
import com.developers.sprintsync.core.util.extension.findTopNavController
import com.developers.sprintsync.core.util.extension.getBitmapDescriptor
import com.developers.sprintsync.core.util.extension.navigateBack
import com.developers.sprintsync.core.util.extension.observe
import com.developers.sprintsync.core.util.extension.setMapStyle
import com.developers.sprintsync.core.util.extension.showErrorAndBack
import com.developers.sprintsync.core.util.extension.showToast
import com.developers.sprintsync.core.util.log.AppLogger
import com.developers.sprintsync.data.map.GoogleMapStyle
import com.developers.sprintsync.data.map.TrackPreviewStyle
import com.developers.sprintsync.databinding.FragmentWorkoutSessionBinding
import com.developers.sprintsync.presentation.workout_session.active.util.map.MapCameraManager
import com.developers.sprintsync.presentation.workout_session.active.util.map.MapSnapshotCreator
import com.developers.sprintsync.presentation.workout_session.active.util.map.MapSnapshotPreparer
import com.developers.sprintsync.presentation.workout_session.active.util.map.MarkerManager
import com.developers.sprintsync.presentation.workout_session.active.util.metrics_formatter.UiMetrics
import com.developers.sprintsync.presentation.workout_session.active.util.service.TrackingServiceController
import com.developers.sprintsync.presentation.workout_session.active.util.state_handler.event.UIEvent
import com.developers.sprintsync.presentation.workout_session.active.util.state_handler.map.MapUiState
import com.developers.sprintsync.presentation.workout_session.active.util.state_handler.ui.UIState
import com.developers.sprintsync.presentation.workout_session.active.util.view.TrackingController
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLngBounds
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Fragment for managing the active workout session UI, including map and tracking controls.
 */
@AndroidEntryPoint
class WorkoutSessionFragment : Fragment() {

    private var _binding: FragmentWorkoutSessionBinding? = null
    private val binding get() = checkNotNull(_binding) { getString(R.string.binding_init_error) }

    private val viewModel by activityViewModels<WorkoutSessionViewModel>()

    private var _map: GoogleMap? = null
    private val map get() = checkNotNull(_map) { getString(R.string.map_init_error) }

    private val serviceController by lazy {
        log.i("TrackingServiceController - Initializing new instance for Fragment: ${this.hashCode()}")
        TrackingServiceController(requireContext(), log)
    }

    private val trackingControllerView: TrackingController by lazy { binding.trackingController }

    @Inject
    lateinit var mapCamera: MapCameraManager

    @Inject
    lateinit var mapMarker: MarkerManager

    @Inject
    lateinit var log: AppLogger

    @Inject
    lateinit var mapSnapshotCreator: MapSnapshotCreator

    @Inject
    lateinit var mapSnapshotPreparer: MapSnapshotPreparer

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentWorkoutSessionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        log.d("onCreate - HashCode: ${this.hashCode()}")
        try {
            bindGeneralLoadingOverlay()
            setMapLoadingOverlay()
            setupTrackingPanel()
            binding.mapView.onCreate(savedInstanceState)
            setupMap {
                setUserLocationIcon()
                observeUIEvents()
                observeMapState()
            }
            observeUIState()
            observeStateFlows()
            setBackButtonListener()
        } catch (e: Exception) {
            log.e("Error setting up fragment", e)
            showErrorAndBack(log)
        }
    }

    override fun onStart() {
        super.onStart()
        try {
            log.d("Starting fragment, binding service")
            serviceController.bind(requireActivity()) { connectionResult ->
                viewModel.bindTo(connectionResult)
            }
            serviceController.launchLocationUpdates()
            binding.mapView.onStart()
        } catch (e: Exception) {
            log.e("Failed to start fragment: ${e.message}", e)
            showErrorAndBack(log)
        }
    }

    // Sets up the tracking panel with start/pause/finish actions
    private fun setupTrackingPanel() {
        val listener = object : TrackingController.OnInteractionListener {
            override fun onStart() = serviceController.startService()

            override fun onPause() = serviceController.pauseService()

            override fun onFinish() = serviceController.finishService()

        }
        trackingControllerView.setOnInteractionListener(listener)
    }

    // Initializes the Google Map and sets up map-ready callback
    private fun setupMap(
        onMapReady: () -> Unit
    ) {
        binding.mapView.getMapAsync { map ->
            try {
                mapCamera.attachToMap(map)
                map.setMapStyle(requireContext(), GoogleMapStyle.MINIMAL)
                _map = map
                log.i("Map initialized")
                onMapReady()
            } catch (e: Exception) {
                log.e("Failed to initialize map: ${e.message}", e)
                showToast(requireContext().getString(R.string.err_map_init))
            }
        }
    }

    // Sets icon for user location marker
    private fun setUserLocationIcon() {
        try {
            val icon = requireContext().getBitmapDescriptor(R.drawable.ic_user_location)
            requireNotNull(icon) { " Icon is null" }
            mapMarker.setIcon(icon)
        } catch (e: Exception) {
            log.e("Failed to set user location icon", e)
            showToast(requireContext().getString(R.string.err_marker_init))
        }
    }

    // Observes UI state to update visibility and loading states
    private fun observeUIState() {
        observe(viewModel.uiStateFlow) { state ->
            try {
                setInitialLoadingVisibility(state.showInitialLoading)
                setCompletingLoadingVisibility(state.showCompletingLoading)
                updatePauseCardVisibility(state)
                updateTrackingPanel(state)
            } catch (e: Exception) {
                log.e("Error handling UI state: ${e.message}", e)
                showErrorAndBack(log)
            }
        }
    }

    // Updates tracking panel UI
    private fun updateTrackingPanel(state: UIState) {
        val panelState = state.trackingControllerState ?: return
        trackingControllerView.updateState(panelState)
    }

    // Observes UI events for snapshot requests and navigation
    private fun observeUIEvents() {
        observe(viewModel.uiEventFlow) { event ->
            try {
                when (event) {
                    is UIEvent.RequestSnapshot -> {
                        prepareMapForSnapshot(event.bounds, event.style)
                        mapSnapshotCreator.createSnapshot(map) { bitmap ->
                            viewModel.onSnapshotReady(bitmap)
                        }
                    }

                    is UIEvent.NavigateToSummary -> navigateToSessionSummary(event.trackId)
                    is UIEvent.ErrorAndClose -> {
                        showErrorAndBack(log, event.message) // TODO we don't need error here
                    }
                }
                log.d("Processed UI event: ${event.javaClass.simpleName}")
            } catch (e: Exception) {
                log.e("Error handling UI event: ${e.message}", e)
                showErrorAndBack(log)
            }
        }
    }

    // Observes duration and metrics flows
    private fun observeStateFlows() {
        observe(viewModel.durationFlow) { duration ->
            updateDuration(duration)
        }
        observe(viewModel.metricsFlow) { metrics ->
            updateMetrics(metrics)
        }
    }

    // Observes map state to update location and polylines
    private fun observeMapState() {
        val loadingView = binding.mapLoadingOverlay
        observe(viewModel.mapStateFlow) { state ->
            try {
                when (state) {
                    MapUiState.Loading -> loadingView.show()
                    is MapUiState.LocationUpdated -> {
                        if (binding.mapLoadingOverlay.isVisible) loadingView.hide()
                        mapMarker.updateMarker(map, state.location)
                        mapCamera.moveCamera(state.location)
                    }

                    is MapUiState.PolylinesUpdated -> state.polylines.forEach { map.addPolyline(it) }
                }
                log.d("Map state updated: ${state.javaClass.simpleName}")
            } catch (e: Exception) {
                log.e("Error handling map state: ${e.message}", e)
                showErrorAndBack(log)
            }

        }
    }

    private fun updateDuration(duration: String) {
        binding.tvDuration.text = duration
    }

    private fun updateMetrics(metrics: UiMetrics) {
        binding.apply {
            tvDistanceValue.text = metrics.distance
            tvCaloriesValue.text = metrics.calories
            tvPaceValue.text = metrics.pace
        }
        log.d("Updated metrics: distance=${metrics.distance}")
    }

    private fun setBackButtonListener() {
        binding.btnBack.setOnClickListener {
            navigateBack(log)
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

    private fun setInitialLoadingVisibility(isVisible: Boolean) {
        if (isVisible) {
            binding.generalLoadingOverlay.apply {
                setLoadingMessage()
                show()
            }
        } else binding.generalLoadingOverlay.hide()

    }

    private fun setCompletingLoadingVisibility(isVisible: Boolean) {
        if (isVisible) {
            binding.generalLoadingOverlay.apply {
                setLoadingMessage(context.getString(R.string.message_completing_track))
                show()
            }
        }
    }

    private fun setMapLoadingOverlay() =
        binding.mapLoadingOverlay.apply {
            bindToLifecycle(lifecycle)
            setLoadingMessage(getString(R.string.tracking_map_loading_message))
        }

    // Prepares the map for taking a snapshot
    private fun prepareMapForSnapshot(bounds: LatLngBounds, style: TrackPreviewStyle) {
        try {
            mapSnapshotPreparer.prepareMap(
                context = requireContext(),
                map = map,
                bounds = bounds,
                padding = style.padding,
                marker = mapMarker.marker,
                mapStyle = style.mapStyle,
            )
            log.i("Map prepared for snapshot: bounds=$bounds")
        } catch (e: Exception) {
            log.e("Failed to prepare map for snapshot: ${e.message}", e)
        }
    }

    private fun stopLocationUpdatesIfShould() {
        val shouldStop = viewModel.uiStateFlow.value.shouldStopLocationUpdatesWhenClosed
        if (shouldStop) {
            try {
                serviceController.stopLocationUpdates()
                log.d("Stopped location updates on close")
            } catch (e: Exception) {
                log.e("Error stopping location updates: ${e.message}", e)
            }
        }
    }

    private fun navigateToSessionSummary(trackId: Int) {
        val action = WorkoutSessionFragmentDirections.actionTrackingFragmentToSessionSummaryFragment(trackId)
        findTopNavController().navigate(action)
        log.i("Navigated to session summary: trackId=$trackId")
    }

    override fun onPause() {
        super.onPause()
        log.d("onPause")
        binding.mapView.onPause()
    }

    override fun onResume() {
        super.onResume()
        log.d("onResume")
        binding.mapView.onResume()
    }

    override fun onStop() {
        super.onStop()
        try {
            log.d("Stopping fragment, unbinding service")
            serviceController.unbindService(requireActivity())
            stopLocationUpdatesIfShould()
            binding.mapView.onStop()
        } catch (e: Exception) {
            log.e("Error stopping fragment: ${e.message}", e)
            showErrorAndBack(log)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        log.d("onDestroyView")
        binding.mapView.onDestroy()
        mapCamera.detachMap()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        log.d("onDestroy")
    }
}
