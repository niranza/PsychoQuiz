package com.niran.psychoquiz.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.niran.psychoquiz.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentHomeBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            startQuizBtn.setOnClickListener { navigateToChooseLetterFragment() }
        }
    }

    private fun navigateToChooseLetterFragment() = view?.findNavController()
        ?.navigate(HomeFragmentDirections.actionHomeFragmentToChooseLetterFragment())

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}