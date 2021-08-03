package com.niran.psychoquiz.database.models.settings.superclasses

open class BooleanSetting(

    open val settingId: Int,

    open val settingValue: Boolean,
) {
    interface Interface {

        var settingValue: Boolean

        fun getValue(): Boolean

        fun getKey(): Any

        fun getAllValid(): List<Any>

        fun getNames(): List<String>
    }
}