package com.developers.sprintsync.user.ui.userProfile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.developers.sprintsync.R
import com.developers.sprintsync.databinding.FragmentUserProfileBinding
import com.developers.sprintsync.user.ui.userProfile.chart.interaction.navigation.ChartNavigator
import com.developers.sprintsync.user.viewModel.UserProfileViewModel
import kotlinx.coroutines.launch

class UserProfileFragment : Fragment() {
    private var _binding: FragmentUserProfileBinding? = null
    private val binding get() = checkNotNull(_binding) { getString(R.string.binding_init_error) }

    private val viewModel by activityViewModels<UserProfileViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.initChartManager(binding.progressChart)
        viewModel.setWeeklyConfiguration()
        viewModel.setScroller(binding)

        initDataRangeListener()

        setRangeNavigatingButtons()
    }

    private fun initDataRangeListener() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.dateRange.collect { dateRange ->
                    binding.tvDayMonthRange.text = dateRange.dayMonthRange
                    binding.tvYearRange.text = dateRange.yearsRange
                }
            }
        }
    }

    private fun setRangeNavigatingButtons() {
        binding.btPreviousRange.setOnClickListener {
            viewModel.navigateRange(ChartNavigator.NavigationDirection.PREVIOUS)
        }
        binding.btNextRange.setOnClickListener {
            viewModel.navigateRange(ChartNavigator.NavigationDirection.NEXT)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
