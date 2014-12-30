package benny.so.smsforwarder.common;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;

public class Message {
    private static final String TAG = "Message";

    public static void broadcast(Context context, String action, Bundle bundle) {
        Intent intent = new Intent();
        intent.setAction(action);
        intent.putExtras(bundle);
        Log.i(TAG, "broadcast to: " + action);
        context.sendBroadcast(intent);
    }

    public static String getContactName(Context context, String number) {
        Uri lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        Cursor c = context.getContentResolver().query(lookupUri, new String[]{ContactsContract.Data.DISPLAY_NAME}, null, null, null);
        try {
            c.moveToFirst();
            String displayName = c.getString(0);
            return displayName;
        } catch (Exception e) {
            return null;
        } finally {
            c.close();
        }
    }
}
