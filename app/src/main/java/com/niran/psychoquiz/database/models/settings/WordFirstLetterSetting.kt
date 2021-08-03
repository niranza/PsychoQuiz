package com.niran.psychoquiz.database.models.settings

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "word_first_letter_setting_table")
data class WordFirstLetterSetting(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "setting_id")
    val settingId: Int = -1,

    @ColumnInfo(name = "setting_value")
    val settingValue: Boolean = true,
)
