package com.niran.psychoquiz.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
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

    private var questionDialogShowing = false
    private var refreshDialogGotCanceled = false

    private var bindOnce = true

    private var swipeDownToRefreshSnackBar: Snackbar? = null

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
                            handleErrorWithDialog(it.message).show()
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
        layoutRefresh.setOnRefreshListener {
            swipeDownToRefreshSnackBar?.dismiss()
            refreshGame()
        }

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
                    delay(DELAY_TIME_IN_MILLIS)
                    UiUtil.setViewsBackgroundColor(
                        R.attr.answerButtonsDefaultColor,
                        *answerButtons
                    )
                    viewModel.loadNewWord(wrongAnswerCount)
                    wrongAnswerCount = 0
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
            if (quizFinished)
                if (!questionDialogShowing) refreshDialog().show()
                else refreshDialogGotCanceled = true
        }

        viewModel.eventShowDialog.observe(viewLifecycleOwner) { showDialog ->
            if (showDialog) questionDialog().show()
        }

        bindOnce = false
    }

    private fun refreshGame() = binding.apply {
        layoutRefresh.isRefreshing = true
        layoutQuiz.visibility = View.GONE
        viewModel.loadGame(true)
    }

    private fun isQuestionValid(question: Question) =
        question.word.wordText != "" && question.answers.isNotEmpty()

    private fun handleErrorWithDialog(message: String) = AlertDialog.Builder(activity).apply {
        setTitle(
            when (message) {
                QuizViewModel.INVALID_SETTINGS -> {
                    navigateToSettingsFragment()
                    getString(R.string.invalid_settings)
                }
                QuizViewModel.RECOMMEND_REFRESHING -> {
                    setOnDismissListener { showSwipeToRefreshPopUp() }
                    getString(R.string.changes_detected)
                }
                else -> {
                    navigateToSettingsFragment()
                    getString(R.string.error_message, message)
                }
            }
        )
        setNegativeButton(R.string.ok) { _, _ -> }
        create()
    }

    private fun showSwipeToRefreshPopUp() = binding.apply {
        swipeDownToRefreshSnackBar = Snackbar.make(
            layoutPopupMessage,
            R.string.swipe_down_to_refresh,
            Snackbar.LENGTH_INDEFINITE
        ).also { snackBar -> snackBar.setAction(R.string.multiplication) { snackBar.dismiss() } }
            .apply { show() }
    }

    private fun questionDialog() = AlertDialog.Builder(activity).apply {
        setTitle(getString(R.string.unknown_dialog_title, viewModel.wordText))
        setPositiveButton(R.string.no) { _, _ -> }
        setNegativeButton(R.string.yes) { _, _ -> viewModel.updateCurrentQuestion() }
        setOnDismissListener {
            questionDialogShowing = false
            if (refreshDialogGotCanceled) {
                refreshDialog().show()
                refreshDialogGotCanceled = false
            }

        }
        create()
        questionDialogShowing = true
    }

    private fun refreshDialog() = AlertDialog.Builder(activity).apply {
        setTitle(getString(R.string.refresh_dialog_title))
        setPositiveButton(R.string.no) { _, _ -> }
        setNegativeButton(R.string.yes) { _, _ -> refreshGame() }
        create()
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
        private const val DELAY_TIME_IN_MILLIS = 500L
    }
}