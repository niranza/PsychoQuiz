package com.niran.psychoquiz.ui.fragments

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.niran.psychoquiz.ui.MainActivity
import com.niran.psychoquiz.R
import com.niran.psychoquiz.databinding.FragmentHomeBinding
import com.niran.psychoquiz.utils.saveLanguageAndRefresh


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater)

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            btnEnglish.setOnClickListener { navigateToEnglishFragment() }
            btnMath.setOnClickListener { navigateToMathFragment() }
            btnTimer.setOnClickListener { startTimerActivity() }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) =
        inflater.inflate(R.menu.fragment_home_menu, menu)

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.item_change_language -> {
            languageDialog().show()
            true
        }
        else -> true
    }

    private fun languageDialog() = AlertDialog.Builder(requireContext()).apply {
        val langArray = resources.getStringArray(R.array.languages)
        setTitle(R.string.choose_language)
        setItems(resources.getStringArray(R.array.languages)) { _, which ->
            when (langArray[which]) {
                getString(R.string.hebrew) -> activity?.saveLanguageAndRefresh("iw")
                getString(R.string.english) -> activity?.saveLanguageAndRefresh("en")
            }
        }
        create()
    }

    private fun navigateToEnglishFragment() = view?.findNavController()
        ?.navigate(HomeFragmentDirections.actionHomeFragmentToChooseLetterFragment())

    private fun navigateToMathFragment() = view?.findNavController()
        ?.navigate(HomeFragmentDirections.actionHomeFragmentToMathNavGraph())

    private fun startTimerActivity() = (activity as MainActivity).startTimerActivity()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}