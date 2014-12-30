package benny.so.smsforwarder.listener;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import benny.so.smsforwarder.MainActivity;

public class BootUpReceiver extends BroadcastReceiver {
    private static final String TAG = "BootUpReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "boot complete");
        Intent i = new Intent(context, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}
