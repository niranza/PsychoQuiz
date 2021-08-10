package com.niran.psychoquiz.ui

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
    private var reloadQuiz = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentQuizBinding.inflate(inflater)

        setHasOptionsMenu(true)

        reloadQuiz = navArgs.reloadQuiz

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {

            viewModel.loadingState.observe(viewLifecycleOwner) { loadingState ->
                loadingState?.let {
                    when (it) {
                        LoadingState.LOADING -> {
                            viewModel.reloadQuiz = reloadQuiz
                            loadingPb.visibility = View.VISIBLE
                            quizLayout.visibility = View.GONE
                        }
                        LoadingState.SUCCESS -> {
                            bind()
                            loadingPb.visibility = View.GONE
                            quizLayout.visibility = View.VISIBLE
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

        val answerButtons = arrayOf(answer1, answer2, answer3, answer4)
        val nextAndPreviousButtons = arrayOf(nextTvBtn, previousTvBtn)

        UiUtil.setViewsBackgroundColor(R.attr.defaultBgColor, *nextAndPreviousButtons)

        if (viewModel.isCurrentIndexInitial) previousTvBtn.visibility = View.GONE
        else previousTvBtn.visibility = View.VISIBLE

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
                    viewModel.loadNewQuestion(Question.Load.NEXT)
                    indexTv.text = viewModel.indexString
                    previousTvBtn.visibility = View.VISIBLE
                    UiUtil.setViewsIsClickable(
                        true,
                        *answerButtons,
                        *nextAndPreviousButtons
                    )
                } else UiUtil.setViewsBackgroundColor(R.attr.wordUnknownBgColor, button)
            }
        }

        nextTvBtn.setOnClickListener {
            UiUtil.setViewsBackgroundColor(
                R.attr.answerButtonsDefaultColor,
                *answerButtons
            )
            viewModel.loadNewQuestion(Question.Load.NEXT)
            indexTv.text = viewModel.indexString
            previousTvBtn.visibility = View.VISIBLE
        }

        previousTvBtn.setOnClickListener {
            UiUtil.setViewsBackgroundColor(
                R.attr.answerButtonsDefaultColor,
                *answerButtons
            )
            if (!viewModel.isCurrentIndexInitial)
                viewModel.loadNewQuestion(Question.Load.PREVIOUS)
            if (viewModel.isCurrentIndexInitial) it.visibility = View.GONE
            indexTv.text = viewModel.indexString
        }

        indexTv.text = viewModel.indexString

        viewModel.question.observe(viewLifecycleOwner) { question ->
            if (isQuestionValid(question)) {
                quizHeadlineTv.text =
                    getString(R.string.quiz_headline, question.questionWordText)
                for (i in answerButtons.indices)
                    answerButtons[i].text = question.answers[i]
            }
        }

        viewModel.eventQuizFinished.observe(viewLifecycleOwner) { quizFinished ->
            if (quizFinished) {
                Toast.makeText(activity, "Congrats", Toast.LENGTH_LONG).show()
                navigateToQuizEndFragment()
            }
        }
    }

    private fun isQuestionValid(question: Question) =
        question.questionWordText != "" && question.answers.isNotEmpty()

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) =
        inflater.inflate(R.menu.settings_menu, menu)


    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.settings_item -> {
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