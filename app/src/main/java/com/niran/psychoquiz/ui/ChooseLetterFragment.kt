package com.niran.psychoquiz.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.niran.psychoquiz.DISPLAY_ALL_WORDS_LIST
import com.niran.psychoquiz.databinding.FragmentChooseLetterBinding
import com.niran.psychoquiz.utils.adapters.LetterAdapter

class ChooseLetterFragment : Fragment() {

    private var _binding: FragmentChooseLetterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentChooseLetterBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val letterAdapter = LetterAdapter(object : LetterAdapter.LetterClickHandler {
            override fun onLetterClicked(letter: Char) {
                navigateToWordListFragment(letter.toString())
            }
        })

        binding.apply {
            letterRv.apply {
                adapter = letterAdapter
                setHasFixedSize(true)
            }
            practiceBtn.setOnClickListener { navigateToQuizFragment() }
            displayAllWordsBtn.setOnClickListener { navigateToWordListFragment() }
        }

    }

    private fun navigateToQuizFragment() = findNavController()
        .navigate(ChooseLetterFragmentDirections.actionChooseLetterFragmentToQuizFragment())

    private fun navigateToWordListFragment(letter: String = DISPLAY_ALL_WORDS_LIST.toString()) =
        view?.findNavController()?.navigate(
            ChooseLetterFragmentDirections.actionChooseLetterFragmentToWordListFragment(letter)
        )

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}