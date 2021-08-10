package com.niran.psychoquiz.database.models.settings.superclasses

abstract class BooleanSetting {

    abstract val settingId: Int

    abstract var settingValue: Boolean

    abstract val settingKey: Any

    abstract val settingName: String

    interface SettingConstant {

        val keyList: List<Any>
    }
}

fun <T> List<BooleanSetting>.getAllValid(): List<T> = mutableListOf<T>().also {
    for (setting in this)
        if (setting.settingValue) {
            @Suppress("UNCHECKED_CAST")
            it.add(setting.settingKey as T)
        }
}