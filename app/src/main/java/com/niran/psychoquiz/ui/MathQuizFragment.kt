package com.niran.psychoquiz.ui

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.app.AlertDialog
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.color.MaterialColors
import com.niran.psychoquiz.PsychoQuizApplication
import com.niran.psychoquiz.R
import com.niran.psychoquiz.databinding.FragmentMathQuizBinding
import com.niran.psychoquiz.utils.AppUtils
import com.niran.psychoquiz.utils.enums.LoadingState
import com.niran.psychoquiz.utils.enums.MathType
import com.niran.psychoquiz.utils.getSharedPrefBoolean
import com.niran.psychoquiz.utils.setSharedPrefBoolean
import com.niran.psychoquiz.viewmodels.MathQuizViewModel
import com.niran.psychoquiz.viewmodels.MathQuizViewModelFactory
import com.niran.psychoquiz.viewmodels.QuizViewModel


class MathQuizFragment : Fragment() {

    private var _binding: FragmentMathQuizBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MathQuizViewModel by viewModels {
        MathQuizViewModelFactory((activity?.application as PsychoQuizApplication).mathQuizSettingRepository)
    }

    private val navArgs: MathQuizFragmentArgs by navArgs()

    private val currentMathType get() = MathType.values()[navArgs.mathType]

    private var mute = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMathQuizBinding.inflate(inflater)

        setHasOptionsMenu(true)

        loadMuteState()

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
                        errorDialog(it.message).show()
                        navigateToMathQuizSettingsFragment()
                    }
                }
            }
        }
    }

    private fun bind() = binding.apply {

        tvSymbol.text = currentMathType.symbol

        root.setOnClickListener {
            hideKeyBoard()
            etInput.apply {
                text.clear()
                clearFocus()
            }
        }

        etInput.addTextChangedListener(object : TextWatcher {
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
                                playAudio(R.raw.ding)
                                animateBackground(root)
                                etInput.text.clear()
                                viewModel.loadNewMathQuestion(currentMathType)
                            }
                        }
                }
            }
        })

        viewModel.outputOne.observe(viewLifecycleOwner) { output ->
            tvOutputOne.text = output
        }

        viewModel.outputTwo.observe(viewLifecycleOwner) { output ->
            tvOutputTwo.text = output
        }
    }

    private fun animateBackground(view: View) {
        val colorFrom =
            MaterialColors.getColor(requireContext(), R.attr.wordKnownBgColor, Color.CYAN)
        val colorTo = MaterialColors.getColor(requireContext(), R.attr.defaultBgColor, Color.CYAN)
        ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo).apply {
            duration = 400 // milliseconds
            addUpdateListener { animator -> view.setBackgroundColor(animator.animatedValue as Int) }
            start()
        }
    }

    private fun errorDialog(message: String) = AlertDialog.Builder(activity).apply {
        setTitle(
            when (message) {
                QuizViewModel.INVALID_SETTINGS ->
                    getString(R.string.invalid_settings)
                else -> getString(R.string.error_message, message)
            }
        )
        setPositiveButton(R.string.ok) { _, _ -> }
        create()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_math_quiz_menu, menu)
        menu.findItem(R.id.item_mute).apply {
            if (mute) setIcon(R.drawable.ic_volume_off) else setIcon(R.drawable.ic_volume_on)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.item_mute -> {
                mute = !mute
                saveMuteState()
                if (mute) item.setIcon(R.drawable.ic_volume_off)
                else item.setIcon(R.drawable.ic_volume_on)
                true
            }
            R.id.item_settings -> {
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

    private fun loadMuteState() = activity?.getSharedPrefBoolean(
        getString(R.string.math_quiz_pref_file_key),
        getString(R.string.saved_mute_key)
    )?.let { mute = it }

    private fun saveMuteState() = activity?.setSharedPrefBoolean(
        getString(R.string.math_quiz_pref_file_key),
        getString(R.string.saved_mute_key),
        mute
    )

    @Suppress("SameParameterValue")
    private fun playAudio(audioRaw: Int) =
        MediaPlayer.create(requireContext(), audioRaw).apply { if (!mute) start() }

    private fun hideKeyBoard() = AppUtils.hideKeyBoard(requireActivity())

    override fun onDestroyView() {
        super.onDestroyView()
        hideKeyBoard()
        _binding = null
    }
}