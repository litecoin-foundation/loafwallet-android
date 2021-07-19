package com.breadwallet.tools.util

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.preference.PreferenceManager
import com.breadwallet.entities.Language
import java.util.*

/**
 * Litewallet
 * Created by Mohamed Barry on 7/19/21
 * email: mosadialiou@gmail.com
 * Copyright © 2021 Litecoin Foundation. All rights reserved.
 */
class LocaleHelper private constructor() {
    private lateinit var sharedPreferences: SharedPreferences
    lateinit var currentLocale: Language
        private set

    fun setLocale(context: Context): Context {
        return if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
            updateLocale(context, currentLocale)
        } else updateLocaleLegacy(context, currentLocale)
    }

    private fun updateLocale(context: Context, language: Language): Context {
        val locale = getLocale(language)
        Locale.setDefault(locale)
        val config = context.resources.configuration
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }

    @Suppress("deprecation")
    private fun updateLocaleLegacy(context: Context, language: Language): Context {
        val locale = getLocale(language)
        Locale.setDefault(locale)
        val res = context.resources
        val config = res.configuration
        config.locale = locale
        res.updateConfiguration(config, res.displayMetrics)
        return context
    }

    private fun getLocale(language: Language): Locale {
        val codes = language.code.split("-")
        return if (codes.size == 2) {
            Locale(codes[0], codes[1])
        } else {
            Locale(codes[0])
        }
    }

    fun setLocaleIfNeeded(language: Language): Boolean {
        if (language == currentLocale) {
            return false
        }
        currentLocale = language
        sharedPreferences.edit()
                .putString(LANGUAGE_PREF_KEY, language.code)
                .apply()
        return true
    }

    companion object {
        private const val LANGUAGE_PREF_KEY = "lw_language_pref_key"

        lateinit var instance: LocaleHelper
            private set

        fun init(context: Context) {
            instance = LocaleHelper()
            instance.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            val code = instance.sharedPreferences.getString(
                LANGUAGE_PREF_KEY,
                Language.ENGLISH.code
            )
            instance.currentLocale = Language.find(code)
        }
    }
}