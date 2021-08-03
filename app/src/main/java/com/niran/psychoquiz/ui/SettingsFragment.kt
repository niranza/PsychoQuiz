package com.niran.psychoquiz.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.niran.psychoquiz.PsychoQuizApplication
import com.niran.psychoquiz.database.models.Word
import com.niran.psychoquiz.databinding.FragmentSettingsBinding
import com.niran.psychoquiz.utils.adapters.BooleanSettingAdapter
import com.niran.psychoquiz.viewmodels.QuizSettingsViewModel
import com.niran.psychoquiz.viewmodels.QuizSettingsViewModelFactory


class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: QuizSettingsViewModel by viewModels {
        QuizSettingsViewModelFactory((activity?.application as PsychoQuizApplication).settingRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSettingsBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {

            val wordFirstLetterAdapter = BooleanSettingAdapter(Word.FirstLetter.A.getNames())
            val wordTypeAdapter = BooleanSettingAdapter(Word.Types.UNKNOWN.getNames())

            wordFirstLetterRv.apply {
                adapter = wordFirstLetterAdapter
                setHasFixedSize(true)
            }

            wordTypeRv.apply {
                adapter = wordTypeAdapter
                setHasFixedSize(true)
            }

            viewModel.getAllWordFirstLettersSettings().observe(viewLifecycleOwner) { list ->
                list?.let { wordFirstLetterAdapter.submitList(it) }
            }

            viewModel.getAllWordTypeSettings().observe(viewLifecycleOwner) { list ->
                list?.let { wordTypeAdapter.submitList(it) }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}