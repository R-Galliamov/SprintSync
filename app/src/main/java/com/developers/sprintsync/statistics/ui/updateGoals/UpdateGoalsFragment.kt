package com.developers.sprintsync.statistics.ui.updateGoals

import android.os.Bundle
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
import com.developers.sprintsync.global.manager.view.input.InputCardManager
import com.developers.sprintsync.global.manager.view.spinner.manager.SpinnerManager
import com.developers.sprintsync.global.manager.view.spinner.mapper.WellnessGoalToSpinnerMapper
import com.developers.sprintsync.statistics.model.chart.chartData.Metric
import com.developers.sprintsync.statistics.model.goal.WellnessGoal
import com.developers.sprintsync.statistics.model.ui.InputCardView
import com.developers.sprintsync.statistics.util.converter.MetricInputConverter
import com.developers.sprintsync.statistics.viewModel.UpdateGoalsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class UpdateGoalsFragment : Fragment() {
    private var _binding: FragmentUpdateGoalsBinding? = null
    private val binding get() = checkNotNull(_binding) { getString(R.string.binding_init_error) }

    private val inputCardViews: Map<Metric, InputCardView> by lazy { createMetricInputViewMap(binding) }

    private val viewModel by activityViewModels<UpdateGoalsViewModel>()

    private var _spinnerManager: SpinnerManager<WellnessGoal>? = null
    private val spinnerManager get() = checkNotNull(_spinnerManager) { getString(R.string.spinner_manager_init_error) }

    @Inject
    lateinit var inputCardManager: InputCardManager

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
        initSpinnerManager()
        configureInputCards()
        setDailyGoalsListener()
        setWellnessGoalListener()
        setAdjustButtonListener()
    }

    private fun configureInputCards() {
        inputCardManager.configureInputCards(binding.container, inputCardViews.values.toList())
    }

    private fun initSpinnerManager() {
        val spinner = binding.generalGoal.spinner
        val items = WellnessGoal.entries
        val mapper = WellnessGoalToSpinnerMapper()
        _spinnerManager = SpinnerManager(spinner, items, mapper)
    }

    private fun setWellnessGoalListener() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.wellnessGoal.collect { goal ->
                    spinnerManager.setSelectedItem(goal)
                }
            }
        }
    }

    private fun createMetricInputViewMap(binding: FragmentUpdateGoalsBinding): Map<Metric, InputCardView> {
        val map = mutableMapOf<Metric, InputCardView>()
        Metric.entries.forEach { metric ->
            val inputCardView =
                when (metric) {
                    Metric.DISTANCE ->
                        InputCardView(
                            card = binding.dailyGoals.cardDistance,
                            editText = binding.dailyGoals.etDistanceValue,
                        )

                    Metric.DURATION ->
                        InputCardView(
                            card = binding.dailyGoals.cardDuration,
                            editText = binding.dailyGoals.etDurationValue,
                        )

                    Metric.CALORIES ->
                        InputCardView(
                            card = binding.dailyGoals.cardCalories,
                            editText = binding.dailyGoals.etCaloriesValue,
                        )
                }
            map[metric] = inputCardView
        }
        return map
    }

    private fun setDailyGoalsListener() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.formattedDailyGoals.collect { mapMetricGoal ->
                    mapMetricGoal.entries.forEach { entry ->
                        val goal = entry.value.value
                        inputCardViews[entry.key]?.editText?.setText(goal)
                    }
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
        val goal = spinnerManager.getSelectedItem()
        viewModel.saveWellnessGoal(goal)
    }

    private fun createMetricValueMap(): Map<Metric, Float> {
        val map = mutableMapOf<Metric, Float>()
        inputCardViews.forEach { (metric, metricView) ->
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
            // TODO: update
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _spinnerManager = null
    }

    companion object {
        private const val TAG = "My stack: UpdateGoalsFragment"
    }
}
