package com.developers.sprintsync.presentation.map_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.developers.sprintsync.R
import com.developers.sprintsync.domain.track.model.Segments
import com.developers.sprintsync.domain.track.model.Track
import com.developers.sprintsync.core.util.extension.findTopNavController
import com.developers.sprintsync.core.util.extension.setMapStyle
import com.developers.sprintsync.databinding.FragmentMapBinding
import com.developers.sprintsync.presentation.components.MapStyle
import com.google.android.gms.maps.GoogleMap

class MapFragment : Fragment() {
    private var _binding: FragmentMapBinding? = null
    private val binding get() = checkNotNull(_binding) { getString(R.string.binding_init_error) }

    private val args: MapFragmentArgs by navArgs()

    private val viewModel by activityViewModels<MapViewModel>()

    private var _map: GoogleMap? = null
    private val map get() = checkNotNull(_map) { getString(R.string.map_init_error) }

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
            map.setMapStyle(requireContext(), MapStyle.DETAILED)
            setDataObserver()
        }
        setBackButton()
    }

    private fun initMap(
        savedInstanceState: Bundle?,
        onMapReady: () -> Unit,
    ) {
        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync { map ->
            _map = map
            onMapReady()
        }
    }

    private fun setDataObserver() {
        viewModel.getTrackById(args.trackId).observe(viewLifecycleOwner) { track ->
            track ?: return@observe
            getNonEmptySegments(track)?.let { segments ->
                updateProgressBarVisibility(false)
                /*
                mapManager.addPolylines(segments)
                map.adjustCamera(
                    MapCalculations.calculateBounds(
                        segments.flatten(),
                        MapCalculations.calculateTrackPadding(
                            binding.mapView.width,
                            binding.mapView.height,
                        ),
                    ),
                )

                 */
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
