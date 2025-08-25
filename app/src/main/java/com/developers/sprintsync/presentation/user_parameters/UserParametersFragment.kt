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
import com.developers.sprintsync.core.util.view.InputCardHandler
import com.developers.sprintsync.core.util.view.InputCardView
import com.developers.sprintsync.core.util.view.spinner.manager.SpinnerManager
import com.developers.sprintsync.databinding.FragmentUserParametersBinding
import com.developers.sprintsync.domain.user_profile.model.Sex
import com.developers.sprintsync.domain.user_profile.model.UserParameters
import com.developers.sprintsync.presentation.user_parameters.util.DatePickerCreator
import com.developers.sprintsync.presentation.user_parameters.util.GenderToSpinnerMapper
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class UserParametersFragment : Fragment() {
    private var _binding: FragmentUserParametersBinding? = null
    private val binding get() = checkNotNull(_binding) { getString(R.string.binding_init_error) }

    private var _sexSpinnerManager: SpinnerManager<Sex>? = null
    private val genderSpinnerManager get() = checkNotNull(_sexSpinnerManager) { getString(R.string.spinner_manager_init_error) }

    private var _datePicker: MaterialDatePicker<Long>? = null
    private val datePicker get() = checkNotNull(_datePicker) { getString(R.string.date_picker_init_error) } // TODO get rid of such kind of checks or wrap in try block

    private val viewModel: UserParametersViewModel by viewModels()

    @Inject
    lateinit var inputCardHandler: InputCardHandler

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentUserParametersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        configureInputCards()
        initGenderSpinnerManager()
        initParametersFlowListeners()
        setBirthDateCardListener()
    }

    private fun configureInputCards() {
        inputCardHandler.configureInputCards(binding.root, createTextInputViews())
    }

    private fun initGenderSpinnerManager() {
        val spinner = binding.userParameters.spinnerGender
        val items = Sex.entries
        val mapper = GenderToSpinnerMapper()
        _sexSpinnerManager = SpinnerManager(spinner, items, mapper)
    }

    private fun initParametersFlowListeners() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.parametersFlow.collect { parameters ->
                    _datePicker = createDatePickerIfNull(parameters.birthDateTimestamp)
                    updateGenderUI(parameters.sex)
                    updateBirthDateUI(parameters.birthDate)
                    updateWeightUI(parameters.weight)
                }
            }
        }
    }

    private fun createDatePickerIfNull(selectedDateTimestamp: Long): MaterialDatePicker<Long> =
        _datePicker ?: DatePickerCreator().create(selectedDateTimestamp) { selectedDateFormatted ->
            updateBirthDateUI(selectedDateFormatted)
        }

    private fun updateGenderUI(sex: Sex) {
        genderSpinnerManager.setSelectedItem(sex)
    }

    private fun updateWeightUI(weight: String) {
        binding.userParameters.etWeightValue.setText(weight)
    }

    private fun updateBirthDateUI(date: String) {
        binding.userParameters.tvBirthDate.text = date
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
        val birthDateTimestamp = datePicker.selection ?: return null
        val weight =
            binding.userParameters.etWeightValue.text
                .toString()
                .toFloat()
        return UserParameters(gender, birthDateTimestamp, weight)
    }

    private fun createTextInputViews(): List<InputCardView> {
        val card = binding.userParameters.cardWeight
        val editText = binding.userParameters.etWeightValue
        return listOf(InputCardView(card, editText))
    }

    private fun clearResources() {
        _sexSpinnerManager = null
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
