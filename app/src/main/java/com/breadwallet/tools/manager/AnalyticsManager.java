package com.breadwallet.tools.manager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;

import com.breadwallet.tools.threads.BRExecutor;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.breadwallet.tools.util.CustomEvent;

public class AnalyticsManager {
    private static AnalyticsManager instance;
    private FirebaseAnalytics firebaseAnalytics;
    private Handler handler;
    private AnalyticsManager() {
        handler = new Handler();
    }

    public static AnalyticsManager getInstance() {
        if (instance == null) {
            instance = new AnalyticsManager();
        }
        return instance;
    }
    public void initWith(final Context context) {
        firebaseAnalytics = FirebaseAnalytics.getInstance(context);
    }

    public void logEvent( CustomEvent itemName, Bundle params) {

        Bundle itemsParams = null;

        if (params != null) {
            itemsParams = params;
        }
        firebaseAnalytics.logEvent(itemName.toString(), itemsParams);
    }

}
