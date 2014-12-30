package benny.so.smsforwarder.listener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import benny.so.smsforwarder.R;
import benny.so.smsforwarder.common.Constants;
import benny.so.smsforwarder.common.Message;

public class BatteryListener extends BroadcastReceiver {
    private static final String TAG = "BatteryListener";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.ACTION, Constants.ActionType.BATTERY);
        Log.i(TAG, "Battery low");
        broadcast(context, bundle);
    }

    public static void broadcast(Context context, Bundle bundle) {
        String action = context.getString(R.string.receive_battery);
        Message.broadcast(context, action, bundle);
    }
}
