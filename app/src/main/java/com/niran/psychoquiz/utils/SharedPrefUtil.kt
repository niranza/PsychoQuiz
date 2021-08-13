package com.niran.psychoquiz.utils

import android.content.Context

fun Context.setSharedPrefBoolean(prefsName: String, key: String, value: Boolean) {
    getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        .edit().apply { putBoolean(key, value); apply() }
}

fun Context.getSharedPrefBoolean(prefsName: String, key: String): Boolean =
    with(getSharedPreferences(prefsName, Context.MODE_PRIVATE)) { getBoolean(key, false) }

fun Context.setSharedPrefString(prefsName: String, key: String, value: String) {
    getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        .edit().apply { putString(key, value); apply() }
}

fun Context.getSharedPrefString(prefsName: String, key: String): String? =
    with(getSharedPreferences(prefsName, Context.MODE_PRIVATE)) { getString(key, "")}















