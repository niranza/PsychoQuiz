package com.niran.psychoquiz

import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.niran.psychoquiz.databinding.ActivityTimerBinding
import com.niran.psychoquiz.viewmodels.TimerViewModel

class TimerActivity : AppCompatActivity() {

    private var _binding: ActivityTimerBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TimerViewModel by viewModels()

    private val maxNumberOfChapters = 8
    private var totalNumberOfChapters = maxNumberOfChapters

    private val timerRunning get() = viewModel.timerRunning

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityTimerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        supportActionBar?.apply {
            setTitle(R.string.timer)
            setDisplayHomeAsUpEnabled(true)
        }

        binding.apply {

            //for configuration changes
            if (timerRunning) {
                switchEssaySetting.isClickable = false
                layoutChaptersSettings.visibility = View.GONE
                btnStartPause.setText(R.string.pause)
            } else {
                switchEssaySetting.isClickable = true
                layoutChaptersSettings.visibility = View.VISIBLE
                btnStartPause.setText(R.string.start)
            }

            ArrayAdapter(
                this@TimerActivity,
                R.layout.support_simple_spinner_dropdown_item,
                (1..maxNumberOfChapters).toList().reversed()
            ).also { spinnerChapters.adapter = it }

            spinnerChapters.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    totalNumberOfChapters = parent?.getItemAtPosition(position) as Int
                    tvChapter.apply {
                        if (text != getString(R.string.essay))
                            text = getString(R.string.chapter_title, 1, totalNumberOfChapters)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

            btnStartPause.setOnClickListener {
                if (timerRunning) pauseTimer() else startTimer()
            }

            btnReset.setOnClickListener { resetTimer() }

            switchEssaySetting.setOnCheckedChangeListener { _, isChecked ->
                viewModel.resetTimer(isChecked)
            }

            viewModel.timeLeftInMillis.observe(this@TimerActivity) { formattedTime ->
                tvCountdown.text = formattedTime
                if (formattedTime == FORMATTED_FIVE_MINUETS) playAudio(R.raw.five_minuets_left)
            }

            viewModel.eventTimerFinished.observe(this@TimerActivity) { timerFinished ->
                if (timerFinished) {
                    playAudio(R.raw.test_ended)
                    btnStartPause.setText(R.string.start)
                    resetTimer()
                }
            }

            viewModel.chapter.observe(this@TimerActivity) { chapterNumber ->
                if (timerRunning) playAudio(R.raw.move_to_next_chapter)

                tvChapter.text = if (chapterNumber == 0) getString(R.string.essay)
                else getString(R.string.chapter_title, chapterNumber, totalNumberOfChapters)
            }
        }
    }

    private fun startTimer() = binding.apply {
        if (!btnReset.isVisible) playAudio(R.raw.test_began)
        viewModel.startTimer(totalNumberOfChapters)
        switchEssaySetting.isClickable = false
        layoutChaptersSettings.visibility = View.GONE
        btnStartPause.setText(R.string.pause)
        btnReset.visibility = View.GONE
    }

    private fun pauseTimer() = binding.apply {
        viewModel.pauseTimer()
        btnStartPause.setText(R.string.start)
        btnReset.visibility = View.VISIBLE
    }

    private fun resetTimer() = binding.apply {
        viewModel.resetTimer(switchEssaySetting.isChecked)
        switchEssaySetting.isClickable = true
        layoutChaptersSettings.visibility = View.VISIBLE
        btnStartPause.visibility = View.VISIBLE
        btnReset.visibility = View.GONE
    }

    private fun playAudio(audioRaw: Int) = MediaPlayer.create(this, audioRaw).start()

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val FORMATTED_FIVE_MINUETS = "05:00"
    }
}