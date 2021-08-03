package com.niran.psychoquiz

import android.app.Application
import com.niran.psychoquiz.database.AppDatabase
import com.niran.psychoquiz.repositories.SettingRepository
import com.niran.psychoquiz.repositories.WordRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class PsychoQuizApplication : Application() {

    val scope = CoroutineScope(SupervisorJob())

    val database: AppDatabase by lazy { AppDatabase.getDatabase(this, scope) }

    val wordRepository: WordRepository by lazy { WordRepository(database.wordDao()) }

    val settingRepository: SettingRepository by lazy { SettingRepository(database.settingDao()) }

}