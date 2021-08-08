package com.niran.psychoquiz.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.niran.psychoquiz.PsychoQuizApplication
import com.niran.psychoquiz.R
import com.niran.psychoquiz.database.models.Word
import com.niran.psychoquiz.database.models.settings.WordFirstLetterSetting
import com.niran.psychoquiz.database.models.settings.WordTypeSetting
import com.niran.psychoquiz.database.models.settings.superclasses.BooleanSetting
import com.niran.psychoquiz.databinding.FragmentQuizSettingsBinding
import com.niran.psychoquiz.utils.adapters.BooleanSettingAdapter
import com.niran.psychoquiz.utils.safeObserveWithInit
import com.niran.psychoquiz.viewmodels.QuizSettingsViewModel
import com.niran.psychoquiz.viewmodels.QuizSettingsViewModelFactory


class QuizSettingsFragment : Fragment() {

    private var _binding: FragmentQuizSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: QuizSettingsViewModel by viewModels {
        QuizSettingsViewModelFactory((activity?.application as PsychoQuizApplication).settingRepository)
    }

    private var originalStateWordFirstLetterList = listOf<WordFirstLetterSetting>()
    private var originalStateWordTypeList = listOf<WordTypeSetting>()
    private var currentStateWordFirstLetterList = listOf<WordFirstLetterSetting>()
    private var currentStateWordTypeList = listOf<WordTypeSetting>()
    private val reloadQuiz
        get() = originalStateWordFirstLetterList != currentStateWordFirstLetterList
                || originalStateWordTypeList != currentStateWordTypeList

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentQuizSettingsBinding.inflate(inflater)

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {

            val wordFirstLetterAdapter = BooleanSettingAdapter(Word.FirstLetter.getNames(),
                object : BooleanSettingAdapter.BooleanSettingClickHandler {
                    override fun onCheckBoxClick(
                        booleanSetting: BooleanSetting
                    ) {
                        val newBooleanSetting = (booleanSetting as WordFirstLetterSetting)
                            .copy(settingValue = !booleanSetting.settingValue)
                        viewModel.insertBooleanSetting(newBooleanSetting)
                    }
                })

            selectAllFirstLetterCb.apply {
                isChecked = allSettingsSelected(currentStateWordFirstLetterList)

                setOnCheckedChangeListener { _, isChecked ->
                    viewModel.selectAllSettings(WordFirstLetterSetting::class, isChecked)
                }
            }

            val wordTypeAdapter = BooleanSettingAdapter(Word.Types.UNKNOWN.getNames(),
                object : BooleanSettingAdapter.BooleanSettingClickHandler {
                    override fun onCheckBoxClick(
                        booleanSetting: BooleanSetting
                    ) {
                        val newBooleanSetting = (booleanSetting as WordTypeSetting)
                            .copy(settingValue = !booleanSetting.settingValue)
                        viewModel.insertBooleanSetting(newBooleanSetting)
                    }
                })

            restartBtn.setOnClickListener { navigateToQuizFragment(true) }

            wordFirstLetterRv.apply {
                adapter = wordFirstLetterAdapter
                setHasFixedSize(true)
            }

            wordTypeRv.apply {
                adapter = wordTypeAdapter
                setHasFixedSize(true)
            }

            viewModel.getAllWordFirstLettersSettingsAsLiveData()
                .safeObserveWithInit(viewLifecycleOwner,
                    observe = { list ->
                        wordFirstLetterAdapter.submitList(list)
                        currentStateWordFirstLetterList = list
                    }, init = { list -> originalStateWordFirstLetterList = list })

            viewModel.getAllWordTypeSettingsAsLiveData()
                .safeObserveWithInit(viewLifecycleOwner,
                    observe = { list ->
                        wordTypeAdapter.submitList(list)
                        currentStateWordTypeList = list
                    }, init = { list -> originalStateWordTypeList = list })

        }
    }

    private fun allSettingsSelected(settingList: List<BooleanSetting>): Boolean {
        for (setting in settingList) if (!setting.settingValue) return false
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) =
        inflater.inflate(R.menu.empty_menu, menu)


    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            android.R.id.home -> {
                navigateToQuizFragment(reloadQuiz)
                true
            }
            else -> true
        }

    private fun navigateToQuizFragment(reloadQuiz: Boolean) = view?.findNavController()
        ?.navigate(QuizSettingsFragmentDirections.actionSettingsFragmentToQuizFragment(reloadQuiz))

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
