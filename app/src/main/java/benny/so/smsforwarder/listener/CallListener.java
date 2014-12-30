package benny.so.smsforwarder.listener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.Date;

import benny.so.smsforwarder.R;
import benny.so.smsforwarder.common.Constants;
import benny.so.smsforwarder.common.Message;

public class CallListener extends BroadcastReceiver {
    private final static String TAG = "CallListener";
    private static int lastState = TelephonyManager.CALL_STATE_IDLE;
    private static Date callStartTime;
    private static String savedNumber;

    @Override
    public void onReceive(Context context, Intent intent) {
        String stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
        String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
        int state = 0;
        if (stateStr.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
            state = TelephonyManager.CALL_STATE_IDLE;
        } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
            state = TelephonyManager.CALL_STATE_OFFHOOK;
        } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
            state = TelephonyManager.CALL_STATE_RINGING;
        }
        onCallStateChanged(context, state, number);
    }

    private void onCallStateChanged(Context context, int state, String number) {
        if (lastState == state) {
            return;
        }
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                callStartTime = new Date();
                savedNumber = number;
                Log.d(TAG, "onIncomingCallStarted, number: " + savedNumber + " time:" + callStartTime.toString());
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                //Transition of ringing->offhook are pickups of incoming calls.  Nothing done on them
                Log.d(TAG, "onIncomingCallAnswered, number: " + savedNumber + " time:" + callStartTime.toString());
                break;
            case TelephonyManager.CALL_STATE_IDLE:
                Date endTime = new Date();
                if (lastState == TelephonyManager.CALL_STATE_RINGING) {
                    Log.d(TAG, "onMissedCall, number: " + savedNumber + " start time:" + callStartTime.toString() + ", end time:" + endTime);
                    onMissingCall(context, savedNumber, callStartTime, endTime);
                } else {
                    Log.d(TAG, "onIncomingCallAnswered Ended, number: " + savedNumber + " time:" + callStartTime.toString());
                }
                break;
        }
        lastState = state;
    }

    private void onMissingCall(Context context, String number, Date callStartTime, Date callEndTime) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.ACTION, Constants.ActionType.MISSING_CALL);
        bundle.putString(Constants.SENDER, number);
        bundle.putSerializable(Constants.RECEIVE_TIME, callStartTime);
        bundle.putSerializable(Constants.END_TIME, callEndTime);
        broadcast(context, bundle);
    }

    public static void broadcast(Context context, Bundle bundle) {
        String action = context.getString(R.string.receive_call);
        Message.broadcast(context, action, bundle);
    }
}
