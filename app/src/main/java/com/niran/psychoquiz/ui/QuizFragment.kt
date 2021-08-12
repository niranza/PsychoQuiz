package com.niran.psychoquiz.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.niran.psychoquiz.PsychoQuizApplication
import com.niran.psychoquiz.R
import com.niran.psychoquiz.database.models.Question
import com.niran.psychoquiz.databinding.FragmentQuizBinding
import com.niran.psychoquiz.utils.UiUtil
import com.niran.psychoquiz.utils.enums.LoadingState
import com.niran.psychoquiz.viewmodels.QuizViewModel
import com.niran.psychoquiz.viewmodels.QuizViewModelFactory
import kotlinx.coroutines.delay


class QuizFragment : Fragment() {

    private var _binding: FragmentQuizBinding? = null
    private val binding get() = _binding!!

    private val viewModel: QuizViewModel by viewModels {
        QuizViewModelFactory(
            (activity?.application as PsychoQuizApplication).wordRepository,
            (activity?.application as PsychoQuizApplication).quizSettingRepository,
            (activity?.application as PsychoQuizApplication).questionRepository,
        )
    }

    private val navArgs: QuizFragmentArgs by navArgs()
    private val reloadQuiz get() = navArgs.reloadQuiz

    private var wrongAnswerCount = 0

    private var bindOnce = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentQuizBinding.inflate(inflater)

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {

            viewModel.loadingState.observe(viewLifecycleOwner) { loadingState ->
                loadingState?.let {
                    when (it) {
                        LoadingState.LOADING -> {
                            layoutQuiz.visibility = View.GONE
                            layoutRefresh.isRefreshing = true
                            viewModel.loadGame(reloadQuiz)
                        }
                        LoadingState.SUCCESS -> {
                            if (bindOnce) bindOnce()
                            bind()
                            layoutRefresh.isRefreshing = false
                            layoutQuiz.visibility = View.VISIBLE
                        }
                        LoadingState.ERROR -> {
                            navigateToSettingsFragment()
                            Toast.makeText(
                                activity,
                                getString(R.string.error_message, it.message),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
        }
    }

    private fun bind() = binding.apply {
        if (viewModel.isCurrentIndexInitial) btnPrevious.visibility = View.GONE
        else btnNext.visibility = View.VISIBLE

        tvIndex.text = viewModel.indexString
    }

    private fun bindOnce() = binding.apply {
        layoutRefresh.setOnRefreshListener { refreshGame() }

        val answerButtons = arrayOf(answer1, answer2, answer3, answer4)
        val nextAndPreviousButtons = arrayOf(btnNext, btnPrevious)

        UiUtil.setViewsBackgroundColor(R.attr.defaultBgColor, *nextAndPreviousButtons)

        for (button in answerButtons) button.setOnClickListener {
            lifecycleScope.launchWhenCreated {
                if (viewModel.assertButtonAnswer(button.text.toString())) {
                    UiUtil.setViewsIsClickable(
                        false,
                        *answerButtons,
                        *nextAndPreviousButtons
                    )
                    UiUtil.setViewsBackgroundColor(R.attr.wordKnownBgColor, button)
                    viewModel.loadNewWord(wrongAnswerCount)
                    wrongAnswerCount = 0
                    delay(DELAY_TIME_IN_MILLIS)
                    UiUtil.setViewsBackgroundColor(
                        R.attr.answerButtonsDefaultColor,
                        *answerButtons
                    )
                    viewModel.loadNewQuestion(Question.Load.NEXT)
                    tvIndex.text = viewModel.indexString
                    btnPrevious.visibility = View.VISIBLE
                    UiUtil.setViewsIsClickable(
                        true,
                        *answerButtons,
                        *nextAndPreviousButtons
                    )
                } else {
                    UiUtil.setViewsBackgroundColor(R.attr.wordUnknownBgColor, button)
                    wrongAnswerCount++
                }
            }
        }

        btnNext.setOnClickListener {
            UiUtil.setViewsBackgroundColor(
                R.attr.answerButtonsDefaultColor,
                *answerButtons
            )
            viewModel.loadNewQuestion(Question.Load.NEXT)
            tvIndex.text = viewModel.indexString
            btnPrevious.visibility = View.VISIBLE
        }

        btnPrevious.setOnClickListener {
            UiUtil.setViewsBackgroundColor(
                R.attr.answerButtonsDefaultColor,
                *answerButtons
            )
            if (!viewModel.isCurrentIndexInitial)
                viewModel.loadNewQuestion(Question.Load.PREVIOUS)
            if (viewModel.isCurrentIndexInitial) it.visibility = View.GONE
            tvIndex.text = viewModel.indexString
        }

        viewModel.question.observe(viewLifecycleOwner) { question ->
            if (isQuestionValid(question)) {
                tvQuizHeadline.text = getString(R.string.quiz_headline, question.word.wordText)
                for (i in answerButtons.indices)
                    answerButtons[i].text = question.answers[i]
            }
        }

        viewModel.eventQuizFinished.observe(viewLifecycleOwner) { quizFinished ->
            if (quizFinished) refreshGame()
        }

        viewModel.eventShowDialog.observe(viewLifecycleOwner) { if (it) showDialog() }

        bindOnce = false
    }

    private fun refreshGame() {
        binding.layoutQuiz.visibility = View.GONE
        viewModel.loadGame(true)
    }

    private fun isQuestionValid(question: Question) =
        question.word.wordText != "" && question.answers.isNotEmpty()

    private fun showDialog() = AlertDialog.Builder(activity).apply {
        setTitle(getString(R.string.unknown_dialog_title, viewModel.wordText))
        setPositiveButton(R.string.no) { _, _ -> }
        setNegativeButton(R.string.yes) { _, _ -> viewModel.updateCurrentQuestion() }
        show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) =
        inflater.inflate(R.menu.fragment_quiz_menu, menu)


    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.item_settings -> {
                navigateToSettingsFragment()
                true
            }
            android.R.id.home -> {
                navigateUp()
                true
            }
            else -> true
        }

    private fun navigateToQuizEndFragment() = view?.findNavController()
        ?.navigate(QuizFragmentDirections.actionQuizFragmentToQuizEndFragment())

    private fun navigateToSettingsFragment() = view?.findNavController()
        ?.navigate(QuizFragmentDirections.actionQuizFragmentToSettingsFragment())

    private fun navigateUp() = view?.findNavController()?.navigateUp()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val DELAY_TIME_IN_MILLIS = 700L
    }
}