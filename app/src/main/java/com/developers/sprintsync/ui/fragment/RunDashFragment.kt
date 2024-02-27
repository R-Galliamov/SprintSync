package com.developers.sprintsync.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.asLiveData
import com.developers.sprintsync.R
import com.developers.sprintsync.databinding.FragmentRunDashBinding
import com.developers.sprintsync.manager.service.TrackingServiceManager
import com.developers.sprintsync.util.mapper.indicator.DistanceMapper
import com.developers.sprintsync.util.mapper.indicator.TimeMapper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RunDashFragment : Fragment() {
    private var _binding: FragmentRunDashBinding? = null
    private val binding get() = checkNotNull(_binding) { getString(R.string.binding_init_error) }

    private var _serviceManager: TrackingServiceManager? = null
    private val serviceManager get() = checkNotNull(_serviceManager) { "Service isn't initialised" }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentRunDashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initServiceManager()
        setListeners()
    }

    override fun onStart() {
        super.onStart()
        serviceManager.bindService { setServiceObservers() }
    }

    private fun setListeners() {
        setStartListener()
    }

    private fun setStartListener() {
        binding.btStart.setOnClickListener {
            if (serviceManager.service.isActive.value) {
                serviceManager.pauseService()
            } else {
                serviceManager.startService()
            }
        }
    }

    /*
    private fun initMaps() {
        binding.mapView.getMapAsync { googleMap ->
            // The GoogleMap object is now ready to use
        }
    }
     */

    private fun setServiceObservers() {
        serviceManager.service.timeInMillisFlow().asLiveData()
            .observe(viewLifecycleOwner) { timeInMillis ->
                binding.tvStopwatch.text = TimeMapper.millisToPresentableTime(timeInMillis)
            }

        serviceManager.service.distanceInMeters.asLiveData()
            .observe(viewLifecycleOwner) { distanceInMeters ->
                binding.tvTotalKmValue.text =
                    DistanceMapper.metersToPresentableDistance(distanceInMeters)
            }
    }

    /*
    private fun finishWorkOut() {
        TrackBuilder.buildTrack(service.segmentList)
        navigate to StatisticsFragment
    }
     */

    private fun initServiceManager() {
        _serviceManager = TrackingServiceManager(this)
    }

    override fun onStop() {
        super.onStop()
        serviceManager.unbindService()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
