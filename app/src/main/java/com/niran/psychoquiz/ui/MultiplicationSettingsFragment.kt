package com.niran.psychoquiz.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.niran.psychoquiz.R
import com.niran.psychoquiz.database.models.settings.superclasses.BooleanSetting
import com.niran.psychoquiz.databinding.FragmentMultiplicationSettingsBinding
import com.niran.psychoquiz.utils.adapters.BooleanSettingAdapter

class MultiplicationSettingsFragment : Fragment() {

    private var _binding: FragmentMultiplicationSettingsBinding? = null
    val binding get() = _binding!!

    private val outputOneList = getNumberList().toMutableList()
    private val outputTwoList = getNumberList().toMutableList()
    private val numberList = getNumberList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentMultiplicationSettingsBinding.inflate(inflater)

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {

            val outputOneAdapter = BooleanSettingAdapter(
                getNumberStringList(),
                object : BooleanSettingAdapter.BooleanSettingClickHandler {
                    override fun onCheckBoxClick(booleanSetting: BooleanSetting) {
                        if (!booleanSetting.settingValue) {
                            outputOneList.add(numberList[booleanSetting.settingId])
                            booleanSetting.settingValue = true
                        } else {
                            outputOneList.remove(numberList[booleanSetting.settingId])
                            booleanSetting.settingValue = false
                        }
                    }
                })/*.apply { submitList(getBooleanSettings()) }*/

            val outputTwoAdapter = BooleanSettingAdapter(
                getNumberStringList(),
                object : BooleanSettingAdapter.BooleanSettingClickHandler {
                    override fun onCheckBoxClick(booleanSetting: BooleanSetting) {
                        if (!booleanSetting.settingValue) {
                            outputTwoList.add(numberList[booleanSetting.settingId])
                            booleanSetting.settingValue = true
                        } else {
                            outputTwoList.remove(numberList[booleanSetting.settingId])
                            booleanSetting.settingValue = false
                        }
                    }
                })/*.apply { submitList(getBooleanSettings()) }*/

            outputOneRv.apply {
                setHasFixedSize(true)
                adapter = outputOneAdapter
            }

            outputTwoRv.apply {
                setHasFixedSize(true)
                adapter = outputTwoAdapter
            }
        }
    }

    private fun getNumberStringList(): List<String> =
        mutableListOf<String>().also { stringList ->
            getNumberList().also { intList ->
                for (num in intList) stringList.add(num.toString())
            }
        }

//    private fun getBooleanSettings() = mutableListOf<BooleanSetting>().also { booleanSettingList ->
//        getNumberList().forEachIndexed { i, _ ->
//            booleanSettingList.add(BooleanSetting(i, true))
//        }
//    }

    private fun getNumberList() = (MIN_NUMBER_PER_SETTING..MAX_NUMBER_PER_SETTING).toList()

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) =
        inflater.inflate(R.menu.empty_menu, menu)

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            android.R.id.home -> {
                navigateToMultiplicationFragment()
                true
            }
            else -> true
        }

    private fun navigateToMultiplicationFragment() = view?.findNavController()?.navigate(
        MultiplicationSettingsFragmentDirections
            .actionMultiplicationSettingsFragmentToMultiplicationFragment(
                outputOneList.toIntArray(),
                outputTwoList.toIntArray()
            )
    )

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val MIN_NUMBER_PER_SETTING = 1
        private const val MAX_NUMBER_PER_SETTING = 12
    }
}