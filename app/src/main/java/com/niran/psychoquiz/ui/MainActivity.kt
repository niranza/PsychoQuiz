package com.niran.psychoquiz.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.niran.psychoquiz.PsychoQuizApplication
import com.niran.psychoquiz.R
import com.niran.psychoquiz.utils.MyContextWrapper
import com.niran.psychoquiz.utils.getSharedPrefString
import com.niran.psychoquiz.viewmodels.MainViewModel
import com.niran.psychoquiz.viewmodels.MainViewModelFactory
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory((application as PsychoQuizApplication).databaseLoaderRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTheme(R.style.Theme_PsychoQuiz)
        supportActionBar?.hide()
        setContentView(R.layout.splash_screen)
        viewModel.initViewModel()

        lifecycleScope.launch {
            (application as PsychoQuizApplication).finishedLoadingDatabase.collect { finished ->
                if (finished) {
                    startMainActivity()
                    cancel()
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun startMainActivity() {
        setContentView(R.layout.activity_main)
        supportActionBar?.show()

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        navController = navHostFragment.navController

        setupActionBarWithNavController(navController)
    }

    fun startTimerActivity() = startActivity(Intent(this, TimerActivity::class.java))

    override fun attachBaseContext(newBase: Context?) {
        newBase?.apply {
            super.attachBaseContext(
                MyContextWrapper.wrap(
                    this,
                    getSharedPrefString(
                        getString(R.string.main_pref_file_key),
                        getString(R.string.saved_lang_key)
                    ) ?: DEFAULT_LANGUAGE
                )
            )
        }
    }

    companion object {
        private const val DEFAULT_LANGUAGE = "en"
    }
}