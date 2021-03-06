package com.followupapp.reminders;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ReminderReceiver extends BroadcastReceiver {
	private static final int P_I_NOTIFICATION_CODE = 241;

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("sms reply", "Got reminder. Timestamp: " + intent.getLongExtra(SmsIncomingReceiver.SMS_RECEIVED_TIMESTAMP, -1L));
		String smsSourceNumber = intent.getStringExtra(SmsIncomingReceiver.SMS_SOURCE_NUMBER);
		String smsSourceName = intent.getStringExtra(SmsIncomingReceiver.SMS_SOURCE_NAME);
		showReplyReminderNotification(context, smsSourceNumber, smsSourceName);
	}
	
	private static void showReplyReminderNotification(Context context, String smsSourceNumber, String smsSourceName) {
		Intent positiveIntent = new Intent(context, ReminderActivity.class);
		positiveIntent.putExtra(SmsIncomingReceiver.SMS_SOURCE_NUMBER, smsSourceNumber);
		positiveIntent.putExtra(SmsIncomingReceiver.SMS_SOURCE_NAME, smsSourceName);
		PendingIntent positivePendingIntent = PendingIntent.getActivity(context, P_I_NOTIFICATION_CODE, positiveIntent, 0);

		Intent negativeIntent = new Intent(context, ReminderActivity.class);
		negativeIntent.putExtra(SmsIncomingReceiver.SMS_SOURCE_NUMBER, smsSourceNumber);
		negativeIntent.putExtra(SmsIncomingReceiver.SMS_SOURCE_NAME, smsSourceName);
//		PendingIntent negativePendingIntent = PendingIntent.getActivity(context, P_I_NOTIFICATION_CODE, negativeIntent, 0);
		//TODO: Handle the 'Clear' button in the notification tray
		PendingIntent negativePendingIntent = null;

		String tickerText = "Reminder: reply to " + smsSourceName;
		String from = "Follow up";
		String message = "Reminder: reply to " + smsSourceName;
		showNotification(context, positivePendingIntent, negativePendingIntent, tickerText, from, message, smsSourceNumber);
		
		//TODO: Schedule subsequent notification
	}

    protected static void showNotification(Context context, PendingIntent positiveIntent, PendingIntent negativeIntent, 
    										String tickerText, CharSequence from, CharSequence message, 
    										String smsSourceNumber) {
        NotificationManager nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(R.drawable.notification_icon, tickerText, System.currentTimeMillis());
        notification.setLatestEventInfo(context, from, message, positiveIntent);
    	notification.defaults = Notification.DEFAULT_ALL;
    	if (negativeIntent != null) {
        	notification.deleteIntent = negativeIntent;
    	}
    	notification.flags = notification.flags | Notification.FLAG_AUTO_CANCEL;
	    int uniqueId = smsSourceNumber.hashCode();
        nm.notify(uniqueId, notification); // unique ids allow for concurrent notifications
    }
}
