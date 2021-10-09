package com.niran.psychoquiz.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.niran.psychoquiz.adapters.LetterAdapter
import com.niran.psychoquiz.databinding.FragmentEnglishBinding
import com.niran.psychoquiz.ui.fragments.WordListFragment.Companion.DISPLAY_ALL_WORDS_LIST

class EnglishFragment : Fragment() {

    private var _binding: FragmentEnglishBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentEnglishBinding.inflate(inflater)

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
            rvLetter.apply {
                adapter = letterAdapter
                setHasFixedSize(true)
            }
            btnPractice.setOnClickListener { navigateToQuizFragment() }
            btnDisplayAllWords.setOnClickListener { navigateToWordListFragment() }
        }

    }

    private fun navigateToQuizFragment() = findNavController()
        .navigate(
            EnglishFragmentDirections.actionChooseLetterFragmentToQuizFragment(
                false
            )
        )

    private fun navigateToWordListFragment(letter: String = DISPLAY_ALL_WORDS_LIST.toString()) =
        view?.findNavController()?.navigate(
            EnglishFragmentDirections.actionChooseLetterFragmentToWordListFragment(
                letter
            )
        )

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}