package com.usamamehmood.shiftdrive;

import android.app.Application;

import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

//This is the notification class that sends push notification when a specific event occurs

public class Notifications extends Application {
    private static final String ONESIGNAL_APP_ID = "a1f6f003-4cb0-42f3-beb1-fe667a7bb5aa";

    @Override
    public void onCreate() {
        super.onCreate();

        // Enable verbose OneSignal logging to debug issues if needed.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

        // OneSignal Initialization
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);


        String externalUserId = "11223344556677"; // You will supply the external user id to the OneSignal SDK

// Setting External User Id with Callback Available in SDK Version 4.0.0+
        OneSignal.setExternalUserId(externalUserId, new OneSignal.OSExternalUserIdUpdateCompletionHandler() {
            @Override
            public void onSuccess(JSONObject results) {
                // The results will contain push and email success statuses
                OneSignal.onesignalLog(OneSignal.LOG_LEVEL.VERBOSE, "Set external user id done with results: " + results.toString());
                try {
                    if (results.has("push") && results.getJSONObject("push").has("success")) {
                        boolean isPushSuccess = results.getJSONObject("push").getBoolean("success");
                        OneSignal.onesignalLog(OneSignal.LOG_LEVEL.VERBOSE, "Set external user id for push status: " + isPushSuccess);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    if (results.has("email") && results.getJSONObject("email").has("success")) {
                        boolean isEmailSuccess = results.getJSONObject("email").getBoolean("success");
                        OneSignal.onesignalLog(OneSignal.LOG_LEVEL.VERBOSE, "Sets external user id for email status: " + isEmailSuccess);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(OneSignal.ExternalIdError error) {
                // The results will contain push and email failure statuses
                // Use this to detect if external_user_id was not set and retry when a better network connection is made
                OneSignal.onesignalLog(OneSignal.LOG_LEVEL.VERBOSE, "Set external user id done with error: " + error.toString());
            }
        });


    }
}