package com.niran.psychoquiz.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.niran.psychoquiz.LoadingState
import com.niran.psychoquiz.R
import com.niran.psychoquiz.databinding.FragmentMultiplicationBinding
import com.niran.psychoquiz.utils.AppUtils
import com.niran.psychoquiz.viewmodels.MultiplicationViewModel


class MultiplicationFragment : Fragment() {

    private var _binding: FragmentMultiplicationBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MultiplicationViewModel by viewModels()

    private val navArgs: MultiplicationFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentMultiplicationBinding.inflate(inflater)

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loadingState.observe(viewLifecycleOwner) { loadingState ->
            loadingState?.let {
                when (it) {
                    LoadingState.LOADING -> {
                        viewModel.outputOneArray = navArgs.outputOneArray
                        Log.d(
                            "TAG",
                            "initialized outputOneArray with size: ${navArgs.outputOneArray.size}"
                        )
                        viewModel.outputTwoArray = navArgs.outputTwoArray
                        Log.d(
                            "TAG",
                            "initialized outputTwoArray with size: ${navArgs.outputTwoArray.size}"
                        )
                        viewModel.restart()
                    }
                    LoadingState.SUCCESS -> bind()
                    LoadingState.ERROR -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                        navigateToMultiplicationSettingsFragment()
                    }
                }
            }
        }
    }

    private fun bind() = binding.apply {

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
                    viewModel.validateAnswer(it.toString().toInt()).also { correctAnswer ->
                        if (correctAnswer) {
                            inputEt.text.clear()
                            viewModel.restart()
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
                navigateToMultiplicationSettingsFragment()
                true
            }
            android.R.id.home -> {
                view?.findNavController()?.navigateUp()
                true
            }
            else -> true
        }

    private fun navigateToMultiplicationSettingsFragment() = view?.findNavController()?.navigate(
        MultiplicationFragmentDirections.actionMultiplicationFragmentToMultiplicationSettingsFragment()
    )

    private fun hideKeyBoard() = AppUtils.hideKeyBoard(requireActivity())

    override fun onDestroyView() {
        super.onDestroyView()
        hideKeyBoard()
        _binding = null
    }
}