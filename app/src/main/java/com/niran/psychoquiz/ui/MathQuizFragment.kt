package com.niran.psychoquiz.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.niran.psychoquiz.PsychoQuizApplication
import com.niran.psychoquiz.R
import com.niran.psychoquiz.databinding.FragmentMathQuizBinding
import com.niran.psychoquiz.utils.AppUtils
import com.niran.psychoquiz.utils.enums.LoadingState
import com.niran.psychoquiz.utils.enums.MathType
import com.niran.psychoquiz.viewmodels.MathQuizViewModel
import com.niran.psychoquiz.viewmodels.MathQuizViewModelFactory


class MathQuizFragment : Fragment() {

    private var _binding: FragmentMathQuizBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MathQuizViewModel by viewModels {
        MathQuizViewModelFactory((activity?.application as PsychoQuizApplication).mathQuizSettingRepository)
    }

    private val navArgs: MathQuizFragmentArgs by navArgs()

    private val currentMathType get() = MathType.values()[navArgs.mathType]

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMathQuizBinding.inflate(inflater)

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loadingState.observe(viewLifecycleOwner) { loadingState ->
            loadingState?.let {
                when (it) {
                    LoadingState.LOADING -> viewModel.loadGame(currentMathType)
                    LoadingState.SUCCESS -> bind()
                    LoadingState.ERROR -> {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.error_message, it.message),
                            Toast.LENGTH_LONG
                        ).show()
                        navigateToMathQuizSettingsFragment()
                    }
                }
            }
        }
    }

    private fun bind() = binding.apply {

        symbolTv.text = currentMathType.symbol

        root.setOnClickListener {
            hideKeyBoard()
            inputEt.apply {
                text.clear()
                clearFocus()
            }
        }

        inputEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?, start: Int, count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence?, start: Int, before: Int, count: Int
            ) {
            }

            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    if (it.isBlank()) return
                    viewModel.validateAnswer(currentMathType, it.toString().toInt())
                        .also { correctAnswer ->
                            if (correctAnswer) {
                                inputEt.text.clear()
                                viewModel.loadNewMathQuestion(currentMathType)
                            }
                        }
                }
            }
        })

        viewModel.outputOne.observe(viewLifecycleOwner) { output ->
            outputOneTv.text = output
        }

        viewModel.outputTwo.observe(viewLifecycleOwner) { output ->
            outputTwoTv.text = output
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) =
        inflater.inflate(R.menu.settings_menu, menu)

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.settings_item -> {
                navigateToMathQuizSettingsFragment()
                true
            }
            android.R.id.home -> {
                view?.findNavController()?.navigateUp()
                true
            }
            else -> true
        }

    private fun navigateToMathQuizSettingsFragment() = view?.findNavController()?.navigate(
        MathQuizFragmentDirections
            .actionMathQuizFragmentToMathQuizSettingsFragment(currentMathType.ordinal)
    )

    private fun hideKeyBoard() = AppUtils.hideKeyBoard(requireActivity())

    override fun onDestroyView() {
        super.onDestroyView()
        hideKeyBoard()
        _binding = null
    }
}