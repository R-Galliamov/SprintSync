package com.developers.sprintsync.presentation.user_parameters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.developers.sprintsync.R
import com.developers.sprintsync.core.util.extension.observe
import com.developers.sprintsync.core.util.extension.showError
import com.developers.sprintsync.core.util.log.AppLogger
import com.developers.sprintsync.core.util.view.DatePickerFactory
import com.developers.sprintsync.core.util.view.InputCardHandler
import com.developers.sprintsync.core.util.view.InputCardView
import com.developers.sprintsync.core.util.view.SpinnerBinder
import com.developers.sprintsync.databinding.FragmentUserParametersBinding
import com.developers.sprintsync.domain.user_profile.model.Sex
import com.developers.sprintsync.presentation.user_parameters.model.UserParametersUiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class UserParametersFragment : Fragment() {
    private var _binding: FragmentUserParametersBinding? = null
    private val binding get() = checkNotNull(_binding) { getString(R.string.error_binding_not_initialized) }

    private val viewModel: UserParametersViewModel by activityViewModels()

    @Inject
    lateinit var inputCardHandler: InputCardHandler

    @Inject
    lateinit var datePickerFactory: DatePickerFactory

    @Inject
    lateinit var log: AppLogger

    private var sexBinder: SpinnerBinder<Sex>? = null

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
        setSexBinder()
        configureInputCards()
        setCardBirthDateListener()
        setOnWeightChangedListener()
        observeUiState()
        observeErrors()
    }

    private fun setSexBinder() {
        val label = viewModel.sexLabel
        sexBinder = SpinnerBinder<Sex>({ label(it) }).also {
            it.attach(binding.userParameters.spinnerGender, Sex.entries)
            it.setOnSelected { selected -> viewModel.onSexChanged(selected) }
            log.d("setGenderBinder")
        }
    }

    private fun showDatePicker(selected: Long?) {
        val picker = datePickerFactory.create(selected)
        picker.addOnPositiveButtonClickListener { sel -> viewModel.onDateChanged(sel) }
        picker.show(childFragmentManager, "birthDate")
        log.d("showDatePicker")
    }

    private fun configureInputCards() {
        inputCardHandler.configureInputCards(binding.root, createTextInputViews())
        log.d("configureInputCards")
    }

    private fun setOnWeightChangedListener() {
        val weightView = binding.userParameters.etWeightValue
        weightView.setOnEditorActionListener { _, actionId, _ ->
            val input = weightView.text
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.onWeightChanged(input); true
            } else false
        }

        weightView.setOnFocusChangeListener { _, hasFocus ->
            val input = weightView.text
            if (!hasFocus) viewModel.onWeightChanged(input)
        }

    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { s ->
                    when (s) {
                        UserParametersUiState.Empty -> {/* NO-OP */
                        }

                        UserParametersUiState.Loading -> {/* NO-OP */
                        }

                        is UserParametersUiState.Success -> {
                            val p = s.draft
                            p.sex?.let { updateGenderUI(it) }
                            updateBirthDateUI(p.birthDateText)
                            updateWeightUI(p.weightText)
                        }
                    }

                }
            }
        }
    }

    private fun observeErrors() {
        observe(viewModel.errors) { errors ->
            val message = getString(errors.first().titleRes)
            showError(log, message)
        }
    }

    private fun updateGenderUI(sex: Sex) {
        sexBinder?.setSelection(sex)
    }

    private fun updateWeightUI(weight: String) {
        binding.userParameters.etWeightValue.setText(weight)
    }

    private fun updateBirthDateUI(date: String) {
        binding.userParameters.tvBirthDate.text = date
    }

    private fun setCardBirthDateListener() {
        binding.userParameters.cardBirthDate.setOnClickListener {
            val bdEpoch = viewModel.upDraft?.birthDateEpochMillis
            showDatePicker(bdEpoch)
        }
    }

    private fun createTextInputViews(): List<InputCardView> {
        val card = binding.userParameters.cardWeight
        val editText = binding.userParameters.etWeightValue
        return listOf(InputCardView(card, editText))
    }

    private fun clearResources() {
        sexBinder?.detach()
        sexBinder = null
        _binding = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        clearResources()
    }
}
