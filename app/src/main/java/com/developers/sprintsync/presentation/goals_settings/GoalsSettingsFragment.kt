package com.developers.sprintsync.presentation.goals_settings

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
import com.developers.sprintsync.core.util.view.InputCardHandler
import com.developers.sprintsync.core.util.view.InputCardView
import com.developers.sprintsync.databinding.FragmentUpdateGoalsBinding
import com.developers.sprintsync.domain.workouts_plan.model.Metric
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class GoalsSettingsFragment : Fragment() {
    private var _binding: FragmentUpdateGoalsBinding? = null
    private val binding get() = checkNotNull(_binding) { getString(R.string.binding_init_error) }

    private val inputCardViews: Map<Metric, InputCardView> by lazy { createMetricInputViewMap(binding) }

    private val viewModel by activityViewModels<GoalsSettingsViewModel>()

    @Inject
    lateinit var inputCardHandler: InputCardHandler

    @Inject
    lateinit var metricInputConverter: MetricInputConverter

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
        configureInputCards()
        setDailyGoalsListener()
    }

    private fun configureInputCards() {
        inputCardHandler.configureInputCards(binding.container, inputCardViews.values.toList())
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
                viewModel.dailyGoals.collect { mapMetricGoal ->
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
            saveMetricValues()
            findNavController().navigateUp()
        }
    }

    private fun saveMetricValues() {
        val metricValueMap = createMetricValueMap()
        viewModel.updateValues(metricValueMap)
    }

    private fun createMetricValueMap(): Map<Metric, Float> {
        val map = mutableMapOf<Metric, Float>()
        inputCardViews.forEach { (metric, metricView) ->
            val uiValue = metricView.editText.text.toString()
            if (uiValue.isBlank()) return@forEach
            val value = metricInputConverter.convertInputToMetricValue(metric, uiValue)
            if (value.toInt() == 0) return@forEach
            map[metric] = value
        }
        return map
    }

    private fun setBackButton() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
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
