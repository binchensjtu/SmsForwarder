package benny.so.smsforwarder.listener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import java.util.Date;

import benny.so.smsforwarder.R;
import benny.so.smsforwarder.common.Constants;
import benny.so.smsforwarder.common.Message;

/**
 * Reads incoming SMS and forwards them.
 *
 * @author Bin Chen
 */
public class SMSListener extends BroadcastReceiver {
    private static final String TAG = "SMSListener";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Object[] pdus = (Object[]) bundle.get("pdus");

            for (Object pdu : pdus) {
                SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdu);
                String number = sms.getOriginatingAddress();
                String body = sms.getMessageBody();
                Bundle smsBundle = new Bundle();
                smsBundle.putSerializable(Constants.ACTION, Constants.ActionType.SMS);
                smsBundle.putString(Constants.SENDER, number);
                smsBundle.putString(Constants.SMS_BODY, body);
                smsBundle.putSerializable(Constants.RECEIVE_TIME, new Date());

                Log.i(TAG, "Received: " + number + " " + body);
                broadcast(context, smsBundle);
            }
        }
    }

    public static void broadcast(Context context, Bundle bundle) {
        String action = context.getString(R.string.receive_sms);
        Message.broadcast(context, action, bundle);
    }

}
