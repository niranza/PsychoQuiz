package com.niran.psychoquiz

import android.app.Application
import com.niran.psychoquiz.database.AppDatabase
import com.niran.psychoquiz.repositories.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber

class PsychoQuizApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }

    private val _finishedLoadingDatabase = MutableStateFlow(false)
    val finishedLoadingDatabase: StateFlow<Boolean> get() = _finishedLoadingDatabase

    private val scope = CoroutineScope(SupervisorJob())

    private val database: AppDatabase by lazy {
        AppDatabase.getDatabase(this, scope) {
            _finishedLoadingDatabase.value = true
        }
    }

    val wordRepository: WordRepository by lazy { WordRepository(database.wordDao()) }

    val quizSettingRepository: QuizSettingRepository by lazy { QuizSettingRepository(database.settingDao()) }

    val databaseLoaderRepository: DatabaseLoaderRepository by lazy {
        DatabaseLoaderRepository(database.databaseLoaderDao())
    }

    val questionRepository: QuestionRepository by lazy { QuestionRepository(database.questionDao()) }

    val mathQuizSettingRepository: MathQuizSettingRepository by lazy {
        MathQuizSettingRepository(database.settingDao())
    }
}