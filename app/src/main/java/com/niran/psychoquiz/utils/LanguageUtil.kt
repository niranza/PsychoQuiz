package com.niran.psychoquiz.utils

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import com.niran.psychoquiz.R
import java.util.*


fun Activity.saveLanguageAndRefresh(lang: String) {
    setSharedPrefString(
        getString(R.string.main_pref_file_key),
        getString(R.string.saved_lang_key),
        lang
    )
    val refresh = Intent(this, this::class.java)
    startActivity(refresh)
    finish()
}

object MyContextWrapper {
    fun wrap(context: Context, language: String): ContextWrapper {
        var currentContext = context
        val config = currentContext.resources.configuration
        val sysLocale = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
            getSystemLocale(config)
        } else {
            getSystemLocaleLegacy(config)
        }
        if (language != "" && sysLocale.language != language) {
            val locale = Locale(language)
            Locale.setDefault(locale)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                setSystemLocale(config, locale)
            } else {
                setSystemLocaleLegacy(config, locale)
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            currentContext = currentContext.createConfigurationContext(config)
        } else {
            @Suppress("DEPRECATION")
            currentContext.resources.updateConfiguration(
                config,
                currentContext.resources.displayMetrics
            )
        }
        return ContextWrapper(currentContext)
    }

    @Suppress("DEPRECATION")
    private fun getSystemLocaleLegacy(config: Configuration): Locale {
        return config.locale
    }

    @TargetApi(Build.VERSION_CODES.N)
    private fun getSystemLocale(config: Configuration): Locale {
        return config.locales[0]
    }

    @Suppress("DEPRECATION")
    private fun setSystemLocaleLegacy(config: Configuration, locale: Locale?) {
        config.locale = locale
    }

    @TargetApi(Build.VERSION_CODES.N)
    private fun setSystemLocale(config: Configuration, locale: Locale?) {
        config.setLocale(locale)
    }
}