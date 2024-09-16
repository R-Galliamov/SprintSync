package com.developers.sprintsync.user.ui.updateGoals

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.developers.sprintsync.R
import com.developers.sprintsync.databinding.FragmentUpdateGoalsBinding
import com.developers.sprintsync.global.util.AndroidUtils
import com.developers.sprintsync.user.model.chart.chartData.Metric
import com.developers.sprintsync.user.model.goal.WellnessGoal
import com.developers.sprintsync.user.model.ui.MetricInputView
import com.developers.sprintsync.user.util.adapter.SpinnerAdapter
import com.developers.sprintsync.user.util.converter.MetricInputConverter
import com.developers.sprintsync.user.viewModel.UpdateGoalsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class UpdateGoalsFragment : Fragment() {
    private var _binding: FragmentUpdateGoalsBinding? = null
    private val binding get() = checkNotNull(_binding) { getString(R.string.binding_init_error) }

    private val metricInputViews: Map<Metric, MetricInputView> by lazy { createMetricMetricViewMap(binding) }

    private val viewModel by activityViewModels<UpdateGoalsViewModel>()

    private var _spinnerAdapter: SpinnerAdapter? = null
    private val spinnerAdapter get() = checkNotNull(_spinnerAdapter) { getString(R.string.spinner_adapter_init_error) }

    @Inject
    lateinit var androidUtils: AndroidUtils

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentUpdateGoalsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        setBackButton()
        setSaveButton()
        initSpinner()
        configureKeyboardBehavior()
        setCardsOnClickListener()
        setDailyGoalsListener()
        setWellnessGoalListener()
        // TODO: update when finish testing
        setAdjustButtonListener()
    }

    private fun initSpinnerAdapter() {
        _spinnerAdapter = SpinnerAdapter(requireContext(), WellnessGoal.entries)
    }

    private fun initSpinner() {
        initSpinnerAdapter()
        binding.generalGoal.spinner.adapter = spinnerAdapter
    }

    private fun setWellnessGoalListener() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.wellnessGoal.collect { goal ->
                    val position = spinnerAdapter.getPosition(goal)
                    binding.generalGoal.spinner.setSelection(position)
                }
            }
        }
    }

    private fun createMetricMetricViewMap(binding: FragmentUpdateGoalsBinding): Map<Metric, MetricInputView> {
        val map = mutableMapOf<Metric, MetricInputView>()
        Metric.entries.forEach { metric ->
            val metricInputView =
                when (metric) {
                    Metric.DISTANCE ->
                        MetricInputView(
                            card = binding.dailyGoals.cardDistance,
                            editText = binding.dailyGoals.etDistanceValue,
                        )

                    Metric.DURATION ->
                        MetricInputView(
                            card = binding.dailyGoals.cardDuration,
                            editText = binding.dailyGoals.etDurationValue,
                        )

                    Metric.CALORIES ->
                        MetricInputView(
                            card = binding.dailyGoals.cardCalories,
                            editText = binding.dailyGoals.etCaloriesValue,
                        )
                }
            map[metric] = metricInputView
        }
        return map
    }

    private fun setDailyGoalsListener() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.formattedDailyGoals.collect { mapMetricGoal ->
                    mapMetricGoal.entries.forEach { entry ->
                        val goal = entry.value.value
                        metricInputViews[entry.key]?.editText?.setText(goal)
                    }
                }
            }
        }
    }

    private fun setCardsOnClickListener() {
        metricInputViews.forEach { (metric, metricView) ->
            metricView.card.setOnClickListener {
                onCardClick(metric)
            }
        }
    }

    private fun onCardClick(metric: Metric) {
        metricInputViews[metric]?.editText?.requestFocus()
    }

    private fun configureKeyboardBehavior() {
        clearFocusOnRootClick()
        setOnFocusListeners()
    }

    private fun clearFocusOnRootClick() {
        binding.container.setOnClickListener {
            Log.d(TAG, "Clear focus")
            metricInputViews.values.forEach { it.editText.clearFocus() }
        }
    }

    private fun setOnFocusListeners() {
        metricInputViews.values.forEach { metricInputView ->
            metricInputView.editText.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    androidUtils.showKeyboard(metricInputView.editText)
                } else {
                    androidUtils.hideKeyboard(requireView())
                }
            }
        }
    }

    private fun setSaveButton() {
        binding.btSave.setOnClickListener {
            saveWellnessGoal()
            saveMetricValues()
            findNavController().navigateUp()
        }
    }

    private fun saveMetricValues() {
        val metricValueMap = createMetricValueMap()
        viewModel.updateValues(metricValueMap)
    }

    private fun saveWellnessGoal() {
        val selectedPosition = binding.generalGoal.spinner.selectedItemPosition
        val wellnessGoal = spinnerAdapter.getItem(selectedPosition)
        if (wellnessGoal != null) {
            viewModel.saveWellnessGoal(wellnessGoal)
        }
    }

    private fun createMetricValueMap(): Map<Metric, Float> {
        val map = mutableMapOf<Metric, Float>()
        metricInputViews.forEach { (metric, metricView) ->
            val uiValue = metricView.editText.text.toString()
            if (uiValue.isBlank()) return@forEach
            val value = MetricInputConverter.convertInputToMetricValue(metric, uiValue)
            if (value.toInt() == 0) return@forEach
            map[metric] = value
        }
        return map
    }

    private fun setBackButton() {
        binding.btBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setAdjustButtonListener() {
        binding.btAdjustToParameters.setOnClickListener {
            findNavController().navigate(R.id.action_updateGoalsFragment_to_userPreferencesFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "My stack: UpdateGoalsFragment"
    }
}