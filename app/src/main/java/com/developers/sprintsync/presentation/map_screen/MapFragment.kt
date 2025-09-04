package com.developers.sprintsync.presentation.map_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.developers.sprintsync.R
import com.developers.sprintsync.core.util.extension.adjustCameraToBounds
import com.developers.sprintsync.core.util.extension.findTopNavController
import com.developers.sprintsync.core.util.extension.observe
import com.developers.sprintsync.core.util.extension.setMapStyle
import com.developers.sprintsync.core.util.extension.showToast
import com.developers.sprintsync.core.util.log.AppLogger
import com.developers.sprintsync.data.map.GoogleMapStyle
import com.developers.sprintsync.databinding.FragmentMapBinding
import com.google.android.gms.maps.GoogleMap
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MapFragment : Fragment() {
    private var _binding: FragmentMapBinding? = null
    private val binding get() = checkNotNull(_binding) { getString(R.string.error_binding_not_initialized) }

    private val args: MapFragmentArgs by navArgs()

    private val viewModel by viewModels<MapViewModel>()

    private var _map: GoogleMap? = null
    private val map get() = checkNotNull(_map) { getString(R.string.error_map_not_initialized) }

    @Inject
    lateinit var log: AppLogger

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        binding.loadingOverlay.bindToLifecycle(lifecycle)
        binding.mapView.onCreate(savedInstanceState)
        viewModel.fetchTrack(args.trackId)
        setupMap { observeState() }
        setBackButton()
    }

    private fun setupMap(
        onMapReady: () -> Unit
    ) {
        binding.mapView.getMapAsync { map ->
            try {
                map.setMapStyle(requireContext(), GoogleMapStyle.DETAILED)
                _map = map
                log.i("Map initialized")
                onMapReady()
            } catch (e: Exception) {
                log.e("Failed to initialize map: ${e.message}", e)
                showToast(requireContext().getString(R.string.error_map_load_failed))
            }
        }
    }

    private fun observeState() {
        observe(viewModel.state) { state ->
            when (state) {
                MapViewModel.UiState.Loading -> {
                    showLoading(true)
                }

                is MapViewModel.UiState.Success -> {
                    showLoading(false)
                    if (state.polylines.isNotEmpty()) {
                        state.polylines.forEach {
                            map.addPolyline(it)
                        }
                        map.adjustCameraToBounds(state.bounds, state.padding)
                    }
                    log.d("Map loaded successfully")
                }
            }
        }
    }

    private fun setBackButton() {
        binding.btnBack.setOnClickListener {
            findTopNavController().navigateUp()
        }
    }

    private fun showLoading(isVisible: Boolean) {
        when (isVisible) {
            true -> binding.loadingOverlay.show()
            false -> binding.loadingOverlay.hide()
        }
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.mapView.onDestroy()
        _binding = null
    }
}
