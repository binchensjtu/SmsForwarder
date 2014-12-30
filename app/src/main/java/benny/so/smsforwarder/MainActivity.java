package benny.so.smsforwarder;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class MainActivity extends PreferenceActivity {
    private static final String TAG = "MainActivity";

    private static final boolean ALWAYS_SIMPLE_PREFS = false;

    private String ns = Context.NOTIFICATION_SERVICE;

    private NotificationCompat.Builder builder;
    private SharedPreferences prefs;

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        builder = new NotificationCompat.Builder(this)
                .setContentTitle(getString(R.string.pref_general_cb_enable_on_description))
                .setSmallIcon(R.drawable.ic_launcher)
                .setAutoCancel(false)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setContentIntent(
                        PendingIntent.getActivity(this, 10,
                                new Intent(this, MainActivity.class)
                                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Notification.FLAG_NO_CLEAR),
                                0)
                );
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        setupSimplePreferencesScreen();

    }

    /**
     * Shows the simplified settings UI if the device configuration if the
     * device configuration dictates that a simplified, single-pane UI should be
     * shown.
     */
    private void setupSimplePreferencesScreen() {
        if (!isSimplePreferences(this)) {
            return;
        }

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

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this) && !isSimplePreferences(this);
    }

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Determines whether the simplified settings UI should be shown. This is
     * true if this is forced via {@link #ALWAYS_SIMPLE_PREFS}, or the device
     * doesn't have newer APIs like {@link PreferenceFragment}, or the device
     * doesn't have an extra-large screen. In these cases, a single-pane
     * "simplified" settings UI should be shown.
     */
    private static boolean isSimplePreferences(Context context) {
        return ALWAYS_SIMPLE_PREFS
                || Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB
                || !isXLargeTablet(context);
    }


    private void showNotification(String contentText) {
        Log.i(TAG, "show notification");
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);

        builder.setContentText(contentText);
        mNotificationManager.notify(1, builder.build());
    }

    private void dismissNotification() {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
        mNotificationManager.cancelAll();
    }

    private String getNotificationContent() {
        String contentText = null;

        if (prefs.getBoolean(getString(R.string.pref_mail_cb_enable_key), false)) {
            contentText = "转发至" + prefs.getString(getString(R.string.pref_mail_et_receive_address_key), "");
        } else if (prefs.getBoolean(getString(R.string.pref_sms_cb_enable_key), false)) {
            contentText = "转发至" + prefs.getString(getString(R.string.pref_sms_et_receive_address_key), "");
        } else {
            contentText = "尚未设置转发地址";
        }
        return contentText;
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
                    showNotification(getNotificationContent());
                } else {
                    dismissNotification();
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


}
