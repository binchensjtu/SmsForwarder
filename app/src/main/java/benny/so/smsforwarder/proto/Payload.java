package benny.so.smsforwarder.proto;

import android.content.Context;
import android.os.Bundle;

import java.text.SimpleDateFormat;
import java.util.Date;

import benny.so.smsforwarder.common.Constants;
import benny.so.smsforwarder.common.Constants.ActionType;
import benny.so.smsforwarder.common.Message;

public class Payload {
    private static final SimpleDateFormat DF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private String subject;
    private String body;

    private Date receiveTime;
    private Date endTime;

    public Payload(String subject, String body) {
        this.subject = subject;
        this.body = body;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    public void setReceiveTime(Date receiveTime) {
        this.receiveTime = receiveTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String toSMS() {
        return body.isEmpty() ? subject : subject + ":" + body;
    }

    public String getMailBody() {
        String content = this.body;
        if (receiveTime != null && endTime != null) {
            content += "\n" + "From:" + DF.format(receiveTime) + " to " + DF.format(endTime);
        }
        return content;
    }

    public static Payload fromBundle(Context context, Bundle bundle) {

        ActionType action = (ActionType) bundle.getSerializable(Constants.ACTION);
        String subject = null;
        String body = null;
        String name = null;

        switch (action) {
            case SMS:
                name = Message.getContactName(context, bundle.getString(Constants.SENDER));
                subject = (name == null || name.isEmpty()) ? "来自" + bundle.getString(Constants.SENDER) + "短信" : "来自" + bundle.getString(Constants.SENDER) + "(" + name + ")" + "短信";
                body = bundle.getString(Constants.SMS_BODY);
                return new Payload(subject, body);
            case MISSING_CALL:
                name = Message.getContactName(context, bundle.getString(Constants.SENDER));
                subject = (name == null || name.isEmpty()) ? "来自" + bundle.getString(Constants.SENDER) + "短信" : "来自" + bundle.getString(Constants.SENDER) + "(" + name + ")" + "未接来电";
                body = "";
                Payload payload = new Payload(subject, body);
                payload.setReceiveTime((Date) bundle.getSerializable(Constants.RECEIVE_TIME));
                payload.setEndTime((Date) bundle.getSerializable(Constants.END_TIME));
                return payload;
            case BATTERY:
                subject = "本机电量低";
                body = "";
                return new Payload(subject, body);
            default:

        }
        return null;
    }

}
