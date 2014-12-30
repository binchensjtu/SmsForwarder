package benny.so.smsforwarder.listener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import benny.so.smsforwarder.R;
import benny.so.smsforwarder.common.Constants;
import benny.so.smsforwarder.service.MailService;
import benny.so.smsforwarder.service.SMSService;

public class ActionListener extends BroadcastReceiver {
    private final static String TAG = "ActionListener";

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if (!prefs.getBoolean(context.getString(R.string.pref_general_cb_enable_key), false)) {
            Log.i(TAG, "forwarder is disabled...");
            return;
        }
        Bundle bundle = intent.getExtras();
        if (intent.getAction().equals(context.getString(R.string.receive_mail_result))) {
            boolean success = intent.getBooleanExtra(Constants.MAIL_RESULT, false);

            Log.i(TAG, "send mail result: " + success);
            if (!success) {
                Log.i(TAG, "try to send sms");
                sendSMS(context, bundle, prefs);
            }

        } else {
            Constants.ActionType type = (Constants.ActionType) bundle.getSerializable(Constants.ACTION);
            if (Constants.ActionType.MISSING_CALL == type && !prefs.getBoolean(context.getString(R.string.pref_other_cb_call_enable_key), false)) {
                return;
            }

            if (Constants.ActionType.BATTERY == type && !prefs.getBoolean(context.getString(R.string.pref_other_cb_battery_enable_key), false)) {
                return;
            }

            if (prefs.getBoolean(context.getString(R.string.pref_mail_cb_enable_key), false)) {
                sendEmail(context, bundle, prefs);
            } else if (prefs.getBoolean(context.getString(R.string.pref_sms_cb_enable_key), false)) {
                sendSMS(context, bundle, prefs);
            }
        }

    }

    private void sendEmail(Context context, Bundle bundle, SharedPreferences prefs) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if (!prefs.getBoolean(context.getString(R.string.pref_mail_cb_enable_key), false)) {
            return;
        }
        Intent intent = new Intent(context, MailService.class);
        intent.putExtras(bundle);
        context.startService(intent);
        Log.i(TAG, "send email");
    }

    private void sendSMS(Context context, Bundle bundle, SharedPreferences prefs) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if (!prefs.getBoolean(context.getString(R.string.pref_sms_cb_enable_key), false)) {
            return;
        }
        Intent intent = new Intent(context, SMSService.class);
        intent.putExtras(bundle);
        context.startService(intent);
        Log.i(TAG, "send sms");
    }

}
