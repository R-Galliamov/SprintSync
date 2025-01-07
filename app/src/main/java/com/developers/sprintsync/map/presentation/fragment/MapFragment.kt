package com.developers.sprintsync.map.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.developers.sprintsync.R
import com.developers.sprintsync.databinding.FragmentMapBinding
import com.developers.sprintsync.core.util.extension.findTopNavController
import com.developers.sprintsync.map.components.presentation.MapManager
import com.developers.sprintsync.core.components.track.data.model.Segments
import com.developers.sprintsync.core.components.track.data.model.Track
import com.developers.sprintsync.map.presentation.view_model.MapViewModel

class MapFragment : Fragment() {
    private var _binding: FragmentMapBinding? = null
    private val binding get() = checkNotNull(_binding) { getString(R.string.binding_init_error) }

    private val mapManager by lazy { MapManager(requireContext()) }

    private val args: MapFragmentArgs by navArgs()

    private val viewModel by activityViewModels<MapViewModel>()

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
        updateProgressBarVisibility(true)
        initMap(savedInstanceState) {
            mapManager.setMapStyle(viewModel.detailedMapStyle)
            setDataObserver()
        }
        setBackButton()
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

    private fun setDataObserver() {
        viewModel.getTrackById(args.trackId).observe(viewLifecycleOwner) { track ->
            track ?: return@observe
            getNonEmptySegments(track)?.let { segments ->
                updateProgressBarVisibility(false)
                mapManager.addPolylines(segments)
                mapManager.adjustCameraToSegments(
                    segments,
                    binding.mapView.width,
                    binding.mapView.height,
                )
            }
        }
    }

    private fun setBackButton() {
        binding.btBack.setOnClickListener {
            findTopNavController().navigateUp()
        }
    }

    private fun getNonEmptySegments(track: Track): Segments? = track.segments.takeIf { it.isNotEmpty() }

    private fun updateProgressBarVisibility(isVisible: Boolean) {
        when (isVisible) {
            true -> binding.progressBar.visibility = View.VISIBLE
            false -> binding.progressBar.visibility = View.GONE
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
