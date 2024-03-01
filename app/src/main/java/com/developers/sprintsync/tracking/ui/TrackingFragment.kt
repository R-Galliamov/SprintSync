package com.developers.sprintsync.tracking.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.developers.sprintsync.R
import com.developers.sprintsync.databinding.FragmentTrackingBinding
import com.developers.sprintsync.tracking.service.TrackingServiceController
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
    }

    private fun setObservers() {
        setActiveStateObservers()
    }

    private fun setActiveStateObservers() {
        viewModel.isTracking.observe(viewLifecycleOwner) { isActive ->
            if (!isActive) {
                binding.btStart.setOnClickListener {
                    service.startService()
                }
            } else {
                binding.btStart.setOnClickListener {
                    service.pauseService()
                }
            }
        }
    }

    /*
    private fun initMaps() {
        binding.mapView.getMapAsync { googleMap ->
            // The GoogleMap object is now ready to use
        }
    }

    private fun setServiceObservers() {
        serviceManager.service.tracker.trackDataFLow().asLiveData()
            .observe(viewLifecycleOwner) { track ->
                binding.tvPaceValue.text = PaceMapper.paceToPresentablePace(track.avgPace)
                binding.tvTotalKmValue.text =
                    DistanceMapper.metersToPresentableDistance(track.distanceMeters)
                binding.tvTotalKcalValue.text = track.calories.toString()
            }

        serviceManager.service.tracker.timeInMillisFlow().asLiveData()
            .observe(viewLifecycleOwner) { time ->
                binding.tvStopwatch.text = TimeMapper.millisToPresentableTime(time)
            }
    }

    private fun finishWorkOut() {
        TrackBuilder.buildTrack(service.segmentList)
        navigate to StatisticsFragment
    }
     */

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
