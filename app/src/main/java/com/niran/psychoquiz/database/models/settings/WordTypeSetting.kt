package com.niran.psychoquiz.database.models.settings

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.niran.psychoquiz.database.models.settings.superclasses.BooleanSetting

@Entity(tableName = "word_type_setting_table")
data class WordTypeSetting(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "setting_id")
    override val settingId: Int = -1,

    @ColumnInfo(name = "setting_value")
    override val settingValue: Boolean = true,
) : BooleanSetting(settingId, settingValue)