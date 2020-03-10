package com.breadwallet.tools.manager

import android.content.Context
import android.os.Bundle
import com.breadwallet.tools.threads.BRExecutor
import com.breadwallet.tools.util.CustomEvent
import com.google.firebase.analytics.FirebaseAnalytics

object AnalyticsManager {
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    fun initWith(context: Context) {
        firebaseAnalytics = FirebaseAnalytics.getInstance(context)
    }

    fun logEvent(itemName: CustomEvent, params: Bundle?) {
        val params = Bundle()
        firebaseAnalytics.logEvent(itemName.toString(), params)
    }

}