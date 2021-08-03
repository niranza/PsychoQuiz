package com.niran.psychoquiz.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.niran.psychoquiz.R
import com.niran.psychoquiz.database.converters.SettingConverter
import com.niran.psychoquiz.database.daos.SettingDao
import com.niran.psychoquiz.database.daos.WordDao
import com.niran.psychoquiz.database.models.Word
import com.niran.psychoquiz.database.models.settings.WordFirstLetterSetting
import com.niran.psychoquiz.database.models.settings.WordTypeSetting
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(
    entities = [Word::class, WordFirstLetterSetting::class, WordTypeSetting::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(SettingConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun wordDao(): WordDao
    abstract fun settingDao(): SettingDao

    class RoomCallBack(
        private val context: Context,
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            INSTANCE?.let { database ->
                scope.launch {
                    populateWordTable(database.wordDao())
                    populateSettingTable(database.settingDao())
                }
            }
        }

        override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
            super.onDestructiveMigration(db)

            INSTANCE?.let { database ->
                scope.launch {
                    populateWordTable(database.wordDao())
                    populateSettingTable(database.settingDao())
                }
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

        private suspend fun populateSettingTable(settingDao: SettingDao) {

            //region WordFirstLetter Setting
            for (setting in Word.FirstLetter.values()) settingDao.insertWordFirstLetterSetting(
                WordFirstLetterSetting(setting.ordinal, setting.settingValue)
            )
            //endregion WordFirstLetter Setting

            //region WordType Setting
            for (setting in Word.Types.values()) settingDao.insertWordTypeSetting(
                WordTypeSetting(setting.ordinal, setting.settingValue)
            )
            //endregion WordType Setting

        }

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)

            INSTANCE?.let { database ->
                scope.launch {
                    initializeSettingValues(database.settingDao())
                }
            }
        }

        private suspend fun initializeSettingValues(settingDao: SettingDao) {

            //region FirstLetter
            val dbFirstLetterList = settingDao.getAllWordFirstLetterSettings()
            val firstLetterList = Word.FirstLetter.values()
            for (i in dbFirstLetterList.indices)
                firstLetterList[i].settingValue = dbFirstLetterList[i].settingValue
            //endregion FirstLetter

            //region WordType
            val dbWordTypeList = settingDao.getAllWordTypeSettings()
            val wordTypeList = Word.Types.values()
            for (i in dbWordTypeList.indices)
                wordTypeList[i].settingValue = dbWordTypeList[i].settingValue
            //endregion WordType
        }
    }

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration()
                    .fallbackToDestructiveMigrationOnDowngrade()
                    .addCallback(RoomCallBack(context, scope))
                    .build().also { INSTANCE = it }
            }

    }

}