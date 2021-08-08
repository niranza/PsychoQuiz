package com.niran.psychoquiz.database.models.settings.superclasses

abstract class BooleanSetting(

    open val settingId: Int,

    open var settingValue: Boolean,
) {
    interface Interface {

        val defaultSettingVal: Boolean

        val keyList: List<Any>

        fun getAllValid(booleanSettings: List<BooleanSetting>): List<Any>

        fun getNames(): List<String>
    }
}