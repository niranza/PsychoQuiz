package com.niran.psychoquiz.utils

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager

object AppUtils {


    val alphabet = ('a'..'z').toList()

    fun hideKeyBoard(activity: Activity) {
        val inputMethodManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as
                InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(activity.currentFocus?.windowToken, 0)
    }

}