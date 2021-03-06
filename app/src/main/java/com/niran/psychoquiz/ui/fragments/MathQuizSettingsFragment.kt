package com.niran.psychoquiz.ui.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.niran.psychoquiz.PsychoQuizApplication
import com.niran.psychoquiz.R
import com.niran.psychoquiz.database.models.settings.superclasses.BooleanSetting
import com.niran.psychoquiz.databinding.FragmentMathQuizSettingsBinding
import com.niran.psychoquiz.adapters.BooleanSettingAdapter
import com.niran.psychoquiz.utils.enums.MathType
import com.niran.psychoquiz.viewmodels.MathQuizSettingsViewModel
import com.niran.psychoquiz.viewmodels.MathQuizSettingsViewModelFactory

class MathQuizSettingsFragment : Fragment() {

    private var _binding: FragmentMathQuizSettingsBinding? = null
    val binding get() = _binding!!

    private val viewModel: MathQuizSettingsViewModel by viewModels {
        MathQuizSettingsViewModelFactory(
            (activity?.application as PsychoQuizApplication).mathQuizSettingRepository
        )
    }

    private val navArgs: MathQuizSettingsFragmentArgs by navArgs()

    private val currentMathType get() = MathType.values()[navArgs.mathType]

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMathQuizSettingsBinding.inflate(inflater)

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {

            if (currentMathType == MathType.POWER) layoutSecondNumber.visibility = View.GONE

            val outputOneAdapter = BooleanSettingAdapter(
                object : BooleanSettingAdapter.BooleanSettingClickHandler {
                    override fun onCheckBoxClick(booleanSetting: BooleanSetting) {
                        (booleanSetting).apply {
                            settingValue = !settingValue
                            viewModel.insertBooleanSetting(this)
                        }
                    }
                })

            cbSelectAllFirstNumber.setOnCheckedChangeListener { _, isChecked ->
                viewModel.selectAllOutputOneSettings(currentMathType, isChecked)
            }

            val outputTwoAdapter = BooleanSettingAdapter(
                object : BooleanSettingAdapter.BooleanSettingClickHandler {
                    override fun onCheckBoxClick(booleanSetting: BooleanSetting) {
                        (booleanSetting).apply {
                            settingValue = !settingValue
                            viewModel.insertBooleanSetting(this)
                        }
                    }
                })

            cbSelectAllSecondNumber.setOnCheckedChangeListener { _, isChecked ->
                viewModel.selectAllOutputTwoSettings(currentMathType, isChecked)
            }

            rvOutputOne.apply {
                setHasFixedSize(true)
                adapter = outputOneAdapter
            }

            rvOutputTwo.apply {
                setHasFixedSize(true)
                adapter = outputTwoAdapter
            }

            viewModel.getAllOutputOneSettingsAsLiveData(currentMathType)
                .observe(viewLifecycleOwner) { settingsList ->
                    settingsList?.let { outputOneAdapter.submitList(it) }
                }

            viewModel.getAllOutputTwoSettingsAsLiveData(currentMathType)
                .observe(viewLifecycleOwner) { settingList ->
                    settingList?.let { outputTwoAdapter.submitList(it) }
                }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) =
        inflater.inflate(R.menu.fragment_math_quiz_settings_menu, menu)

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.item_home -> {
                navigateUp()
                true
            }
            android.R.id.home -> {
                navigateToMathQuizFragment()
                true
            }
            else -> true
        }

    private fun navigateUp() = view?.findNavController()?.navigateUp()

    private fun navigateToMathQuizFragment() = view?.findNavController()?.navigate(
        MathQuizSettingsFragmentDirections.actionMathQuizSettingsFragmentToMathQuizFragment(
            when (currentMathType) {
                MathType.MULTIPLICATION -> MathType.MULTIPLICATION.ordinal
                MathType.DIVISION -> MathType.DIVISION.ordinal
                MathType.POWER -> MathType.POWER.ordinal
            }
        )
    )

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}