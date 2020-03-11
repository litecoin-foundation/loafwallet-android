package com.breadwallet.tools.manager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.content.Context;

import androidx.annotation.Nullable;

import com.breadwallet.BreadApp;
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
    public void init() {
        final Context app = BreadApp.getBreadContext();
        if (app == null)
            return;
        firebaseAnalytics = FirebaseAnalytics.getInstance(app);
    }

    public void logEvent(CustomEvent itemName, @Nullable Bundle params) {

        Bundle itemsParams = new Bundle();
        String generic_text = "generic text no values sent";
        itemsParams.putString("generic_text",generic_text);

        if (params != null) {
            itemsParams.clear();
            itemsParams = params;
        }
        firebaseAnalytics.logEvent(itemName.toString(), itemsParams);
    }

}
