package com.developers.sprintsync.ui.fragment

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.asLiveData
import com.developers.sprintsync.R
import com.developers.sprintsync.databinding.FragmentRunDashBinding
import com.developers.sprintsync.service.TrackingService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RunDashFragment : Fragment() {

    private var _binding: FragmentRunDashBinding? = null
    private val binding get() = checkNotNull(_binding) { getString(R.string.binding_init_error) }

    private var _trackingService: TrackingService? = null

    private val trackingService get() = checkNotNull(_trackingService) { getString(R.string.service_isn_t_initialised) }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName?, iBinder: IBinder?) {
            val binder = iBinder as TrackingService.ServiceBinder
            _trackingService = binder.getService()
            initTrackingCollector()
            setServiceObservers()
        }

        override fun onServiceDisconnected(className: ComponentName?) {
            _trackingService = null
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRunDashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()


    }

    override fun onStart() {
        super.onStart()
        bindService()
    }

    private fun sendCommandToService() =
        Intent(requireContext(), TrackingService::class.java).also {
            requireContext().startService(it)
        }

    private fun initTrackingCollector() {
        CoroutineScope(Dispatchers.IO).launch {
            trackingService.getTrackFlow().collect {
                if (trackingService.isActive) {
                    Log.i("My stack", "Ui thread: " + Thread.currentThread().name)
                    Log.i("My stack", it.toString())
                }
            }
        }
    }

    private fun setListeners() {
        setStartListener()
    }

    private fun setStartListener() {
        binding.btStart.setOnClickListener {
            if (trackingService.isActive)
                trackingService.pause() else {
                Intent(requireContext(), TrackingService::class.java).also {
                    requireContext().startService(it)
                }
            }
        }
    }

    private fun bindService() {
        val serviceIntent = Intent(requireContext(), TrackingService::class.java)
        requireActivity().bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    private fun unbindService() {
            requireActivity().unbindService(serviceConnection)
    }

    private fun setServiceObservers() {
        trackingService.getTimeInMillisFlow().asLiveData().observe(viewLifecycleOwner) {
            binding.tvStopwatch.text = it.toString()
        }

        trackingService.distanceInMeters.asLiveData().observe(viewLifecycleOwner) {
            binding.tvTotalKmValue.text = it.toInt().toString()
        }

        trackingService.paceMinutesPerKm.asLiveData().observe(viewLifecycleOwner) {
            binding.tvPaceValue.text = it.toInt().toString()
        }
    }


    override fun onStop() {
        super.onStop()
        unbindService()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

