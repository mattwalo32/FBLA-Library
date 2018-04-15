package com.walowtech.fblaapplication.Utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.walowtech.fblaapplication.MainActivity;
import com.walowtech.fblaapplication.R;

import junit.framework.Assert;

import java.lang.reflect.Array;
import java.util.Map;

import static android.content.Context.ALARM_SERVICE;

/**
 * Service to restart notification alarm.
 *
 * In the case of a reboot, this service is called to reset any notifications that
 * are set to occur in the future. This is nessecary because all notifications are
 * cleared upon reboot.
 *
 * @author Matthew Walowski
 * @version 1.0
 * @since 1.0
 */

//Created 1/5/2018
public class ResetNotificationService extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        /*SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        //Get all pref
        Map<String, ?> keys = sharedPreferences.getAll();
        for(Map.Entry<String, ?>entry : keys.entrySet()){ //Loop through all pref
            if(entry.getKey().contains("ALARM-BID")){ //If this is an alarm pref
                //Get all items under alarm
                int BID = Integer.parseInt(entry.getValue().toString());
                String to = sharedPreferences.getString("ALARM" + BID + "-TO", null);
                String body = sharedPreferences.getString("ALARM" + BID + "-BODY", null);
                String title = sharedPreferences.getString("ALARM" + BID + "-TITLE", null);
                int type = Integer.parseInt(sharedPreferences.getString("ALARM" + BID + "-TYPE", null));
                long ringTime = Long.parseLong(sharedPreferences.getString("ALARM" + BID + "-RING-TIME", null));

                //Send alarm
                Intent i = new Intent("com.walowtech.fblaapplication.Utils.RequestPushService");
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
                i.setClass(context, RequestPushService.class);
                i.putExtra("TO", to);
                i.putExtra("BODY", body);
                i.putExtra("TITLE", title);
                i.putExtra("DEMONSTRATION", true);
                i.putExtra("BID", BID);
                i.putExtra("ALARM_TYPE", type);
                context.startService(i);

                PendingIntent service = PendingIntent.getService(context, type + BID, i, 0);

                alarmManager.set(AlarmManager.RTC, ringTime, service);
            }
        }*/
    }
}
