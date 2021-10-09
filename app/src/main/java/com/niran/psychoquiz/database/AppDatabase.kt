package com.niran.psychoquiz.database

import android.content.Context
import androidx.room.*
import androidx.room.migration.AutoMigrationSpec
import androidx.sqlite.db.SupportSQLiteDatabase
import com.niran.psychoquiz.R
import com.niran.psychoquiz.database.converters.StringListConverter
import com.niran.psychoquiz.database.daos.DatabaseLoaderDao
import com.niran.psychoquiz.database.daos.QuestionDao
import com.niran.psychoquiz.database.daos.SettingDao
import com.niran.psychoquiz.database.daos.WordDao
import com.niran.psychoquiz.database.models.DatabaseLoader
import com.niran.psychoquiz.database.models.Question
import com.niran.psychoquiz.database.models.Word
import com.niran.psychoquiz.database.models.settings.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import timber.log.Timber

@Database(
    entities = [
        Word::class,
        WordFirstLetterSetting::class,
        WordTypeSetting::class,
        OutputOneMultiplicationSetting::class,
        OutputOneDivisionSetting::class,
        OutputOnePowerSetting::class,
        OutputTwoMultiplicationSetting::class,
        OutputTwoDivisionSetting::class,
        DatabaseLoader::class,
        Question::class
    ],
    version = 2,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2, spec = AppDatabase.MyAutoMigration::class),
    ]
)
@TypeConverters(StringListConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun wordDao(): WordDao
    abstract fun settingDao(): SettingDao
    abstract fun databaseLoaderDao(): DatabaseLoaderDao
    abstract fun questionDao(): QuestionDao

    @Suppress("unused")
    class MyAutoMigration : AutoMigrationSpec {
        override fun onPostMigrate(db: SupportSQLiteDatabase) {
            super.onPostMigrate(db)
            INSTANCE?.let { _ ->
                CoroutineScope(Dispatchers.IO).launch {
                    Timber.d("onPostMigrate Called")
                    //add new words here
                    Timber.d("onPostMigrate Ended")
                }
            }
        }
    }

    class RoomCallBack(
        private val context: Context,
        private val scope: CoroutineScope,
        private val onFinishedLoading: () -> Unit
    ) : RoomDatabase.Callback() {

        private var executeOnOpen = MutableStateFlow(true)
        private suspend fun executeBeforeOnOpen(action: suspend () -> Unit) {
            executeOnOpen.value = false
            action()
            executeOnOpen.value = true
        }

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            INSTANCE?.let { database ->
                scope.launch {
                    executeBeforeOnOpen {
                        Timber.d("onCreate Called")
                        database.databaseLoaderDao().insertDatabaseLoader(DatabaseLoader())
                        Timber.d("onCreate Ended")
                        delay(100)
                    }
                }
            }
        }

        override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
            super.onDestructiveMigration(db)

            INSTANCE?.let { database ->
                scope.launch {
                    executeBeforeOnOpen {
                        Timber.d("onDestructiveMigration Called")
                        database.databaseLoaderDao().insertDatabaseLoader(DatabaseLoader())
                        Timber.d("onDestructiveMigration Ended")
                        delay(100)
                    }
                }
            }
        }

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)

            INSTANCE?.let { database ->
                scope.launch {
                    executeOnOpen.collect {
                        if (it) {
                            Timber.d("onOpen Called")
                            if (!isDataLoaded(database.databaseLoaderDao()))
                                populateDatabase(database)
                            onFinishedLoading()
                            cancel()
                            Timber.d("onOpen Ended")
                        }
                    }
                }
            }
        }

        private suspend fun populateDatabase(database: AppDatabase) {
            Timber.d("Deleting Database")
            deleteAllDatabase(database)
            Timber.d("Repopulating Database")
            populateWordTable(database.wordDao())
            populateSettingTables(database.settingDao())
            finishLoad(database.databaseLoaderDao())
        }

        private suspend fun deleteAllDatabase(database: AppDatabase) {
            database.wordDao().apply {
                deleteAllWords()
            }
            database.settingDao().apply {
                deleteAllWordFirstLetterSettings()
                deleteAllWordTypeSettings()
                deleteAllOutputOneMultiplicationSettings()
                deleteAllOutputTwoMultiplicationSettings()
                deleteAllOutputOneDivisionSettings()
                deleteAllOutputTwoDivisionSettings()
                deleteAllOutputOnePowerSettings()
            }
        }

        private suspend fun populateWordTable(wordDao: WordDao) {

            val res = context.resources

            val wordTexts = res.getStringArray(R.array.word_text)
            val wordTranslations = res.getStringArray(R.array.word_translation)

            for (i in wordTexts.indices) {
                wordDao.insertWord(
                    Word(
                        wordText = wordTexts[i],
                        wordTranslation = wordTranslations[i]
                    )
                )
            }
        }

        private suspend fun populateSettingTables(settingDao: SettingDao) {
            populateWordFirstLetterSetting(settingDao)
            populateWordTypeSetting(settingDao)
            populateMultiplicationSetting(settingDao)
            populateDivisionSetting(settingDao)
            populatePowerSetting(settingDao)
        }

        //region Populating Settings
        private suspend fun populateWordFirstLetterSetting(settingDao: SettingDao) {
            for (i in WordFirstLetterSetting.Constant.keyList.indices)
                settingDao.insertWordFirstLetterSetting(
                    WordFirstLetterSetting(settingKey = WordFirstLetterSetting.Constant.keyList[i])
                )
        }

        private suspend fun populateWordTypeSetting(settingDao: SettingDao) {
            for (i in WordTypeSetting.Constant.keyList.indices) settingDao.insertWordTypeSetting(
                WordTypeSetting(
                    settingValue = WordTypeSetting.Constant.defaultSettingValues[i],
                    settingKey = WordTypeSetting.Constant.keyList[i]
                )
            )
        }

        private suspend fun populateMultiplicationSetting(settingDao: SettingDao) {
            for (i in OutputOneMultiplicationSetting.Constant.keyList.indices)
                settingDao.insertOutputOneMultiplicationSetting(
                    OutputOneMultiplicationSetting(settingKey = OutputOneMultiplicationSetting.Constant.keyList[i])
                )
            for (i in OutputTwoMultiplicationSetting.Constant.keyList.indices)
                settingDao.insertOutputTwoMultiplicationSetting(
                    OutputTwoMultiplicationSetting(settingKey = OutputTwoMultiplicationSetting.Constant.keyList[i])
                )
        }

        private suspend fun populateDivisionSetting(settingDao: SettingDao) {
            for (i in OutputOneDivisionSetting.Constant.keyList.indices)
                settingDao.insertOutputOneDivisionSetting(
                    OutputOneDivisionSetting(settingKey = OutputOneDivisionSetting.Constant.keyList[i])
                )
            for (i in OutputTwoDivisionSetting.Constant.keyList.indices)
                settingDao.insertOutputTwoDivisionSetting(
                    OutputTwoDivisionSetting(settingKey = OutputTwoDivisionSetting.Constant.keyList[i])
                )
        }

        private suspend fun populatePowerSetting(settingDao: SettingDao) {
            for (i in OutputOnePowerSetting.Constant.keyList.indices)
                settingDao.insertOutputOnePowerSetting(
                    OutputOnePowerSetting(settingKey = OutputOnePowerSetting.Constant.keyList[i])
                )
        }
        //endregion Populating Settings

        private suspend fun finishLoad(databaseLoaderDao: DatabaseLoaderDao) =
            databaseLoaderDao.insertDatabaseLoader(DatabaseLoader(isLoaded = true))

        private suspend fun isDataLoaded(databaseLoaderDao: DatabaseLoaderDao) =
            databaseLoaderDao.getDatabaseLoader().isLoaded

        companion object {
            const val TAG = "RoomCallBack"
        }
    }

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope,
            onFinishedLoading: () -> Unit
        ): AppDatabase = INSTANCE ?: synchronized(this) {
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "app_database"
            )
                .fallbackToDestructiveMigration()
                .fallbackToDestructiveMigrationOnDowngrade()
                .addCallback(RoomCallBack(context, scope, onFinishedLoading))
                .build().also { INSTANCE = it }
        }
    }
}