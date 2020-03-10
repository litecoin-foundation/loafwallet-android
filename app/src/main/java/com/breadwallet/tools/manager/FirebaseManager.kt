package com.breadwallet.tools.manager

import android.content.Context
import android.os.Bundle
import com.breadwallet.tools.threads.BRExecutor
import com.breadwallet.tools.util.CustomEvent
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics

/**
 * Litewallet
 * <p/>
 * Created by Kerry Washington on March 6, 2020
 * Copyright (c) 2020 Litecoin Foundation Ltd
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

object FirebaseManager {

    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var firebaseCrashlytics: FirebaseCrashlytics

    fun initWith(context: Context) {
        firebaseAnalytics = FirebaseAnalytics.getInstance(context)
        firebaseCrashlytics = FirebaseCrashlytics.getInstance()
    }

    fun logEvent(itemName: CustomEvent, params: Bundle?) {
        val params = Bundle()
        firebaseAnalytics.logEvent(itemName.toString(), params)
    }

    fun reportRuntimeException(er: RuntimeException?) {
        try {
            FirebaseCrashlytics.getInstance().recordException(er!!)
        } catch (e: Exception) {
            firebaseAnalytics.logEvent(CustomEvent._20200112_ERR.toString(), null)
        }
    }

    fun reportException(throwable: Throwable) {
        firebaseCrashlytics.recordException(throwable)
    }

    fun reportException(er: Exception?) {
        try {
            FirebaseCrashlytics.getInstance().recordException(er!!)
        } catch (e: Exception) {
            firebaseAnalytics.logEvent(CustomEvent._20200112_ERR.toString(), null)
        }
    }
}