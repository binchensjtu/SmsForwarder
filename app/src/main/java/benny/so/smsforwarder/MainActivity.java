package benny.so.smsforwarder;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import benny.so.smsforwarder.common.Message;

public class MainActivity extends PreferenceActivity {
    private static final String TAG = "MainActivity";

    private static final String ns = Context.NOTIFICATION_SERVICE;

    private NotificationCompat.Builder builder;

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        builder = getNotificationBuilder(this);
        setupSimplePreferencesScreen();

    }

    /**
     * Shows the simplified settings UI if the device configuration if the
     * device configuration dictates that a simplified, single-pane UI should be
     * shown.
     */
    private void setupSimplePreferencesScreen() {

        // In the simplified UI, fragments are not used at all and we instead
        // use the older PreferenceActivity APIs.

        // Add 'general' preferences.
        addPreferencesFromResource(R.xml.pref_general);

        // Add 'notifications' preferences, and a corresponding header.
        PreferenceCategory fakeHeader = new PreferenceCategory(this);
        fakeHeader.setTitle(R.string.pref_header_mail);
        getPreferenceScreen().addPreference(fakeHeader);
        addPreferencesFromResource(R.xml.pref_mail);

        fakeHeader = new PreferenceCategory(this);
        fakeHeader.setTitle(R.string.pref_header_sms);
        getPreferenceScreen().addPreference(fakeHeader);
        addPreferencesFromResource(R.xml.pref_sms);

        fakeHeader = new PreferenceCategory(this);
        fakeHeader.setTitle(R.string.pref_header_other);
        getPreferenceScreen().addPreference(fakeHeader);
        addPreferencesFromResource(R.xml.pref_other);

        bindEnablePreference(findPreference(getString(R.string.pref_general_cb_enable_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_mail_et_receive_address_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_mail_et_send_address_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_mail_et_send_smtp_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_mail_et_send_user_key)));

        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_sms_et_receive_address_key)));

    }


    private Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();
            preference.setSummary(stringValue);
            return true;
        }
    };

    private void bindEnablePreference(Preference checkBoxPreference) {

        Preference.OnPreferenceChangeListener cbChangeListener = new Preference.OnPreferenceChangeListener() {

            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Boolean result = Boolean.valueOf(newValue.toString());
                if (result) {
                    String content = Message.getNotificationContent(MainActivity.this);
                    builder.setContentText(content);
                    showNotification(MainActivity.this, builder);
                } else {
                    dismissNotification(MainActivity.this);
                }
                return true;
            }
        };
        checkBoxPreference.setOnPreferenceChangeListener(cbChangeListener);
        cbChangeListener.onPreferenceChange(checkBoxPreference, PreferenceManager.getDefaultSharedPreferences(checkBoxPreference.getContext()).getBoolean(checkBoxPreference.getKey(), false));

    }


    private void bindPreferenceSummaryToValue(Preference preference) {
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    public static NotificationCompat.Builder getNotificationBuilder(Context context) {
        return new NotificationCompat.Builder(context)
                .setContentTitle(context.getString(R.string.pref_general_cb_enable_on_description))
                .setSmallIcon(R.drawable.ic_launcher)
                .setAutoCancel(false)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setContentIntent(
                        PendingIntent.getActivity(context, 10,
                                new Intent(context, MainActivity.class)
                                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Notification.FLAG_NO_CLEAR),
                                0)
                );
    }

    public static void showNotification(Context context, NotificationCompat.Builder builder) {
        Log.i(TAG, "show notification");
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(ns);

        mNotificationManager.notify(1, builder.build());
    }

    private static void dismissNotification(Context context) {
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(ns);
        mNotificationManager.cancelAll();
    }
}
