package com.developers.sprintsync.presentation.user_parameters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.developers.sprintsync.R
import com.developers.sprintsync.databinding.FragmentParametersBinding
import com.developers.sprintsync.core.presentation.view.InputCardHandler
import com.developers.sprintsync.core.presentation.view.spinner.manager.SpinnerManager
import com.developers.sprintsync.presentation.user_parameters.util.GenderToSpinnerMapper
import com.developers.sprintsync.presentation.user_parameters.util.WellnessGoalToSpinnerMapper
import com.developers.sprintsync.core.util.extension.setState
import com.developers.sprintsync.domain.user_parameters.model.Gender
import com.developers.sprintsync.domain.user_parameters.model.UserParameters
import com.developers.sprintsync.presentation.user_parameters.util.DatePickerCreator
import com.developers.sprintsync.domain.user_parameters.model.WellnessGoal
import com.developers.sprintsync.core.presentation.view.InputCardView
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class UserParametersFragment : Fragment() {
    private var _binding: FragmentParametersBinding? = null
    private val binding get() = checkNotNull(_binding) { getString(R.string.binding_init_error) }

    private var _genderSpinnerManager: SpinnerManager<Gender>? = null
    private val genderSpinnerManager get() = checkNotNull(_genderSpinnerManager) { getString(R.string.spinner_manager_init_error) }

    private var _goalSpinnerManager: SpinnerManager<WellnessGoal>? = null
    private val wellnessGoalToSpinnerMapper get() = checkNotNull(_goalSpinnerManager) { getString(R.string.spinner_manager_init_error) }

    private var _datePicker: MaterialDatePicker<Long>? = null
    private val datePicker get() = checkNotNull(_datePicker) { getString(R.string.date_picker_init_error) } // TODO get rid of such kind of checks

    private val viewModel: UserParametersViewModel by viewModels()

    @Inject
    lateinit var inputCardHandler: InputCardHandler

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentParametersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        configureInputCards()
        setCardSwitchListener()
        initGenderSpinnerManager()
        initWellnessGoalSpinnerManager()
        initParametersFlowListeners()
        setBirthDateCardListener()
    }

    private fun configureInputCards() {
        inputCardHandler.configureInputCards(binding.root, createTextInputViews())
    }

    private fun initGenderSpinnerManager() {
        val spinner = binding.userParameters.spinnerGender
        val items = Gender.entries
        val mapper = GenderToSpinnerMapper()
        _genderSpinnerManager = SpinnerManager(spinner, items, mapper)
    }

    private fun initWellnessGoalSpinnerManager() {
        val spinner = binding.generalGoal.spinner
        val items = WellnessGoal.entries
        val mapper = WellnessGoalToSpinnerMapper()
        _goalSpinnerManager = SpinnerManager(spinner, items, mapper)
    }

    private fun initParametersFlowListeners() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.parametersFlow.collect { parameters ->
                    _datePicker = createDatePickerIfNull(parameters.birthDateTimestamp)
                    updateGenderUI(parameters.gender)
                    updateBirthDateUI(parameters.birthDate)
                    updateWeightUI(parameters.weight)
                    updateWellnessGoalUI(parameters.wellnessGoal)
                    updateUseStatsPermissionUI(parameters.useStatsPermission)
                }
            }
        }
    }

    private fun createDatePickerIfNull(selectedDateTimestamp: Long): MaterialDatePicker<Long> =
        _datePicker ?: DatePickerCreator().create(selectedDateTimestamp) { selectedDateFormatted ->
            updateBirthDateUI(selectedDateFormatted)
        }

    private fun updateGenderUI(gender: Gender) {
        genderSpinnerManager.setSelectedItem(gender)
    }

    private fun updateWeightUI(weight: String) {
        binding.userParameters.etWeightValue.setText(weight)
    }

    private fun updateBirthDateUI(date: String) {
        binding.userParameters.tvBirthDate.text = date
    }

    private fun updateWellnessGoalUI(wellnessGoal: WellnessGoal) {
        wellnessGoalToSpinnerMapper.setSelectedItem(wellnessGoal)
    }

    private fun updateUseStatsPermissionUI(isChecked: Boolean) {
        binding.cardSwitch.btSwitch.setState(isChecked, false)
    }

    private fun setBirthDateCardListener() {
        binding.userParameters.cardBirthDate.setOnClickListener {
            datePicker.show(parentFragmentManager, TAG)
        }
    }

    private fun saveParameters() = createUserParameters()?.let { viewModel.saveParameters(it) }

    // TODO replace with UserParameters creator. Move to view model.
    private fun createUserParameters(): UserParameters? {
        val gender = genderSpinnerManager.getSelectedItem()
        val wellnessGoal = wellnessGoalToSpinnerMapper.getSelectedItem()
        val birthDateTimestamp = datePicker.selection ?: return null
        val weight =
            binding.userParameters.etWeightValue.text
                .toString()
                .toFloat()
        val useStatsPermission = binding.cardSwitch.btSwitch.isChecked
        return UserParameters(gender, birthDateTimestamp, weight, wellnessGoal, useStatsPermission)
    }

    private fun setCardSwitchListener() {
        binding.cardSwitch.root.setOnClickListener {
            binding.cardSwitch.btSwitch.isChecked = !binding.cardSwitch.btSwitch.isChecked
        }
    }

    private fun createTextInputViews(): List<InputCardView> {
        val card = binding.userParameters.cardWeight
        val editText = binding.userParameters.etWeightValue
        return listOf(InputCardView(card, editText))
    }

    private fun clearResources() {
        _genderSpinnerManager = null
        _goalSpinnerManager = null
        _datePicker = null
        _binding = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        saveParameters()
        clearResources()
    }

    companion object {
        private const val TAG = "My stack: ParametersFragment"
    }
}
