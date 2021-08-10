package com.niran.psychoquiz.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.niran.psychoquiz.databinding.FragmentQuizEndBinding


class QuizEndFragment : Fragment() {

    private var _binding: FragmentQuizEndBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentQuizEndBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {

            restartBtn.setOnClickListener { navigateToQuizFragment() }

        }
    }

    private fun navigateToQuizFragment() = view?.findNavController()
        ?.navigate(QuizEndFragmentDirections.actionQuizEndFragmentToQuizFragment(true))

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}