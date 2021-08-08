package com.niran.psychoquiz.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.niran.psychoquiz.databinding.FragmentMathBinding

class MathFragment : Fragment() {

    private var _binding: FragmentMathBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentMathBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {

            multiplicationBtn.setOnClickListener { navigateToMultiplicationFragment() }
            divisionBtn.setOnClickListener { }
            powerBtn.setOnClickListener { }

        }
    }

    private fun navigateToMultiplicationFragment() = view?.findNavController()?.navigate(
        MathFragmentDirections
            .actionMathFragmentToMultiplicationFragment(
                IntArray(12) { i -> i + 1 },
                IntArray(12) { i -> i + 1 }
            ))

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}