package com.niran.psychoquiz.database.converters

import androidx.room.TypeConverter
import com.niran.psychoquiz.AnyTypes

class SettingConverter {

    @TypeConverter
    fun fromAny(any: Any): String =
        when (any) {
            is Boolean -> AnyTypes.BOOLEAN.ordinal.toString() + REGEX + any.toString()
            is Int -> AnyTypes.INT.ordinal.toString() + REGEX + any.toString()
            is String -> AnyTypes.STRING.ordinal.toString() + REGEX + any.toString()
            is Double -> AnyTypes.DOUBLE.ordinal.toString() + REGEX + any.toString()
            else -> throw IllegalArgumentException("Unknown Type")
        }

    @TypeConverter
    fun toAny(string: String): Any {
        val splitList = string.split(",").toMutableList()
        return when (AnyTypes.values()[splitList[0].toInt()]) {
            AnyTypes.BOOLEAN -> splitList[1].toBoolean()
            AnyTypes.INT -> splitList[1].toInt()
            AnyTypes.STRING -> {
                with(splitList) {
                    removeAt(0)
                    splitList.joinToString(REGEX)
                }
            }
            AnyTypes.DOUBLE -> splitList[1].toDouble()
        }
    }

    companion object {
        private const val REGEX = ","
    }

}