package com.niran.psychoquiz.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.niran.psychoquiz.databinding.FragmentMathBinding
import com.niran.psychoquiz.utils.enums.MathType

class MathFragment : Fragment() {

    private var _binding: FragmentMathBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMathBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {

            btnMultiplication.setOnClickListener {
                navigateToMathQuizFragment(MathType.MULTIPLICATION)
            }
            btnDivision.setOnClickListener {
                navigateToMathQuizFragment(MathType.DIVISION)
            }
            btnPower.setOnClickListener { navigateToMathQuizFragment(MathType.POWER) }
        }
    }

    private fun navigateToMathQuizFragment(mathType: MathType) = view?.findNavController()
        ?.navigate(
            MathFragmentDirections.actionMathFragmentToMathQuizFragment(
                mathType.ordinal
            )
        )

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}