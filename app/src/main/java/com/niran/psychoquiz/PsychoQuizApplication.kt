package com.niran.psychoquiz

import android.app.Application
import com.niran.psychoquiz.database.AppDatabase
import com.niran.psychoquiz.repositories.DatabaseLoaderRepository
import com.niran.psychoquiz.repositories.QuestionRepository
import com.niran.psychoquiz.repositories.QuizSettingRepository
import com.niran.psychoquiz.repositories.WordRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PsychoQuizApplication : Application() {

    private val _finishedLoadingDatabase = MutableStateFlow(false)
    val finishedLoadingDatabase: StateFlow<Boolean> get() = _finishedLoadingDatabase

    private val scope = CoroutineScope(SupervisorJob())

    private val database: AppDatabase by lazy {
        AppDatabase.getDatabase(this, scope) {
            _finishedLoadingDatabase.value = true
        }
    }

    val wordRepository: WordRepository by lazy { WordRepository(database.wordDao()) }

    val settingRepository: QuizSettingRepository by lazy { QuizSettingRepository(database.settingDao()) }

    val databaseLoaderRepository: DatabaseLoaderRepository by lazy {
        DatabaseLoaderRepository(database.databaseLoaderDao())
    }

    val questionRepository: QuestionRepository by lazy { QuestionRepository(database.questionDao()) }
}