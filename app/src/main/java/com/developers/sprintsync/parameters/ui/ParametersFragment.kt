package com.developers.sprintsync.parameters.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.developers.sprintsync.R
import com.developers.sprintsync.databinding.FragmentParametersBinding
import com.developers.sprintsync.global.util.spinner.manager.SpinnerManager
import com.developers.sprintsync.global.util.spinner.mapper.GenderToSpinnerMapper
import com.developers.sprintsync.parameters.dataStorage.repository.UserPreferencesRepositoryImpl
import com.developers.sprintsync.parameters.model.Gender
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ParametersFragment : Fragment() {
    private var _binding: FragmentParametersBinding? = null
    private val binding get() = checkNotNull(_binding) { getString(R.string.binding_init_error) }

    private var _spinnerManager: SpinnerManager<Gender>? = null
    private val spinnerManager get() = checkNotNull(_spinnerManager) { getString(R.string.spinner_manager_init_error) }

    @Inject
    lateinit var repo: UserPreferencesRepositoryImpl

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
        initSpinnerManager()
    }

    private fun initSpinnerManager() {
        _spinnerManager = SpinnerManager(requireContext(), GenderToSpinnerMapper())
        spinnerManager.initSpinner(binding.userParameters.spinnerGender, Gender.entries.toList())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _spinnerManager = null
    }
}
