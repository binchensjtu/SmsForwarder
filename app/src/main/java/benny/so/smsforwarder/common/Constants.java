package benny.so.smsforwarder.common;

/**
 * Created by binchen on 12/29/14.
 */
public class Constants {
    public static final String MAIL_RESULT = "MAIL_RESULT";

    public static final String ACTION = "ACTION";
    public static final String SENDER = "SENDER";
    public static final String SMS_BODY = "SMS_BODY";
    public static final String RECEIVE_TIME = "RECEIVE_TIME";
    public static final String END_TIME = "END_TIME";

    public enum ActionType {
        SMS, MISSING_CALL, BATTERY
    }
}
