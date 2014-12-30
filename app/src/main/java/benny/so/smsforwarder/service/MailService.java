package benny.so.smsforwarder.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import benny.so.smsforwarder.R;
import benny.so.smsforwarder.common.Constants;
import benny.so.smsforwarder.proto.Payload;


public class MailService extends IntentService {
    private final static String TAG = "MailService";
    private final static String MAIL_SMTP_HOST = "mail.smtp.host";
    private final static String MAIL_SMTP_AUTH = "mail.smtp.auth";
    private final static String SMTP = "smtp";

    public MailService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle bundle = intent.getExtras();
        Payload payload = Payload.fromBundle(this, bundle);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        String smtpHost = prefs.getString(getString(R.string.pref_mail_et_send_smtp_key), "smtp.126.com");
        String from = prefs.getString(getString(R.string.pref_mail_et_send_address_key), "send@example.com");
        String to = prefs.getString(getString(R.string.pref_mail_et_receive_address_key), "receive@example.com");

        String user = prefs.getString(getString(R.string.pref_mail_et_send_user_key), "send");
        String pwd = prefs.getString(getString(R.string.pref_mail_et_send_pwd_key), "");

        Properties properties = new Properties();
        properties.put(MAIL_SMTP_HOST, smtpHost);
        properties.put(MAIL_SMTP_AUTH, "true");
        Session session = Session.getInstance(properties);

        MimeMessage message = new MimeMessage(session);
        Intent mailIntent = new Intent();
        mailIntent.setAction(getResources().getString(R.string.receive_mail_result));
        mailIntent.putExtras(bundle);
        try {
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject(payload.getSubject());
            message.setText(payload.getMailBody());
            Transport transport = session.getTransport(SMTP);
            transport.connect(smtpHost, user, pwd);

            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
            mailIntent.putExtra(Constants.MAIL_RESULT, true);
        } catch (Exception e) {
            e.printStackTrace();
            mailIntent.putExtra(Constants.MAIL_RESULT, false);
        } finally {
            sendBroadcast(mailIntent);
        }
    }


}
