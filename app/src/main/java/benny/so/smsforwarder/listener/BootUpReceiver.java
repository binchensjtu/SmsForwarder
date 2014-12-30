package benny.so.smsforwarder.listener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import benny.so.smsforwarder.MainActivity;
import benny.so.smsforwarder.R;
import benny.so.smsforwarder.common.Message;

public class BootUpReceiver extends BroadcastReceiver {
    private static final String TAG = "BootUpReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "boot complete");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if (!prefs.getBoolean(context.getString(R.string.pref_general_cb_enable_key), false)) {
            return;
        }
        NotificationCompat.Builder builder = MainActivity.getNotificationBuilder(context);
        String content = Message.getNotificationContent(context);
        builder.setContentText(content);
        MainActivity.showNotification(context, builder);
    }


}
