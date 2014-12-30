package benny.so.smsforwarder.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;

import benny.so.smsforwarder.R;
import benny.so.smsforwarder.proto.Payload;

public class SMSService extends IntentService {
    private static final String TAG = "SMSService";

    public SMSService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Payload payload = Payload.fromBundle(this, intent.getExtras());
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String number = prefs.getString(getString(R.string.pref_sms_et_receive_address_key), "");
        if (number == null || number.isEmpty()) {
            return;
        }

        SmsManager manager = SmsManager.getDefault();
        manager.sendTextMessage(number, null, payload.toSMS(), null, null);
    }
}
