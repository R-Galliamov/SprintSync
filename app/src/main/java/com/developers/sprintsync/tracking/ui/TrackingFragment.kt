package com.developers.sprintsync.tracking.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.developers.sprintsync.R
import com.developers.sprintsync.databinding.FragmentTrackingBinding
import com.developers.sprintsync.tracking.mapper.indicator.PaceMapper
import com.developers.sprintsync.tracking.mapper.indicator.TimeMapper
import com.developers.sprintsync.tracking.model.Segment
import com.developers.sprintsync.tracking.model.Track
import com.developers.sprintsync.tracking.model.TrackerState
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
        setTrackerStateObservers()
        setDataObserver()
    }

    private fun setTrackerStateObservers() {
        viewModel.trackerState.observe(viewLifecycleOwner) { state ->
            updateButtonController(state)
        }
    }

    private fun setDataObserver() {
        viewModel.data.observe(viewLifecycleOwner) { data ->
            updateDuration(data.durationMillis)
            updateTrackingData(data.track)
        }
    }

    private fun updateDuration(durationMillis: Long) {
        binding.tvDuration.text = TimeMapper.millisToPresentableTime(durationMillis)
    }

    private fun updateTrackingData(track: Track) {
        binding.apply {
            tvDistanceValue.text = track.distanceMeters.toString()
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

    private fun updateButtonController(state: TrackerState) {
        when (state) {
            TrackerState.Initialised -> {
                showStartButton()
            }

            TrackerState.Tracking -> {
                showPauseButton()
                showFinishButton()
            }

            TrackerState.Paused -> {
                showResumeButton()
                showFinishButton()
            }

            TrackerState.Finished -> {
                // NO - OP
            }
        }
    }

    private fun showStartButton() {
        binding.tvController.visibility = View.VISIBLE
        binding.btTrackingController.setBackgroundResource(R.drawable.bt_circle_secondary)
        binding.btTrackingController.setOnClickListener {
            service.startService()
        }
        binding.btFinish.visibility = View.GONE
        binding.imController.visibility = View.GONE
    }

    private fun showPauseButton() {
        binding.tvController.visibility = View.GONE
        binding.btTrackingController.setBackgroundResource(R.drawable.bt_circle_thirdhly)
        binding.imController.setImageResource(R.drawable.ic_pause_48dp)
        binding.imController.visibility = View.VISIBLE
        binding.btTrackingController.setOnClickListener {
            service.pauseService()
        }
    }

    private fun showResumeButton() {
        binding.tvController.visibility = View.GONE
        binding.btTrackingController.setBackgroundResource(R.drawable.bt_circle_secondary)
        binding.imController.setImageResource(R.drawable.ic_start_48dp)
        binding.imController.visibility = View.VISIBLE
        binding.btTrackingController.setOnClickListener {
            service.startService()
        }
    }

    private fun showFinishButton() {
        binding.btFinish.apply {
            visibility = View.VISIBLE
            setOnClickListener {
                service.finish()
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
