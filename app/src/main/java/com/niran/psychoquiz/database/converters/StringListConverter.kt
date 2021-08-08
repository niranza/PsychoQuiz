package com.niran.psychoquiz.database.converters

import androidx.room.TypeConverter

class StringListConverter {

    @TypeConverter
    fun fromStringList(stringList: List<String>): String = stringList.joinToString(REGEX)


    @TypeConverter
    fun toStringList(string: String): List<String> = string.split(REGEX)


    companion object {
        private const val REGEX = ".,."
    }

}