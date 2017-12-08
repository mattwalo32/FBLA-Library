package com.walowtech.fblaapplication.Utils;

import android.app.AlarmManager;
import android.app.DownloadManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.provider.SyncStateContract;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.iid.FirebaseInstanceId;
import com.walowtech.fblaapplication.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

/**
 * Requests a push notification to be sent to the device.
 *
 * This service should be deployed by an alarm days after being sent. This is to
 * create the effect of scheduled notifications.
 *
 * @author Matthew Walowski
 * @version 1.0
 * @since 1.0
 */

//Created 11/18/2017
public class RequestPushService extends Service {

    String PARAM_TO = "\"to\"";
    String PARAM_TITLE = "\"title\"";
    String PARAM_BODY = "\"body\"";
    String PARAM_PRIORITY = "\"priority\"";
    String PARAM_NOTIFICATION = "\"notification\"";
    String PARAM_RESTRICTED_PACKAGE_NAME = "\"restricted_package_name\"";

    String VALUE_TO;
    String VALUE_TITLE;
    String VALUE_BODY;
    String VALUE_PRIORITY = "\"high\"";
    final String DEVELOPMENT_ENDPOINT = "gcm-preprod.googleapis.com:5236";
    final String PRODUCTION_ENDPOINT = "gcm-xmpp.googleapis.com:5235";

    private int BID;

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String LEGACY_SERVER_KEY = "AIzaSyC1auk985MDvJhQWq64m_brOIa90DdoBMM";
    private static final String SERVER_KEY = "AAAA9YXaDZM:APA91bH-1Z73NzH6IQfMQMGaqji1nTt1upI4YZ5hNCou51SAo4Xn6-VCFdisI7m0OBd93STHrO5bbN08ICWpra9cP3qhIUNo6fIhE-gKuY5YHJF6phgtB3FcxWabHODXwwccaRosEmpt";
    //String VALUE_RESTRICTED_PACKAGE_NAME = getPackageName();

    JSONObject jsonRequest;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LoginActivity", "SERVICE STARTED");

        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        //Get all the extras
        VALUE_TO = "\"" + intent.getStringExtra("TO") + "\"";
        boolean demonstration = intent.getBooleanExtra("DEMONSTRATION", false);

        if(!demonstration){ //If this is not invoked for demonstartion purposes
            VALUE_BODY = "\"" + intent.getStringExtra("BODY") + "\"";
            VALUE_TITLE = "\"" + intent.getStringExtra("TITLE") + "\"";
            int alarmType = intent.getIntExtra("ALARM_TYPE", -1);
            BID = intent.getIntExtra("BID", -1);


            Log.i("LoginActivity", "CURRENT BID: " + BID);

            int numMatches = 0;

            //Find out if book is already returned
            Map<String,?> keys = sharedPreferences.getAll();
            for(Map.Entry<String, ?> entry : keys.entrySet()) {
                if(entry.getKey().contains("BOOK")) {
                    if (Integer.valueOf(entry.getValue().toString()) == BID) { //If there is a book found
                        numMatches++;
                    }
                }
            }

            if(numMatches > 0) {

                //Create the JSON object for the body
                createJSONObject();

                //Request the notification from the server
                sendNotification();

                //Vars for computing time to ping
                long oneDay = 60 * 60 * 24 * 1000;
                long fourDays = oneDay * 3;

                //Vars that will be passed to method
                long ringTime = 0;
                String title = null;
                String body = null;
                int nextAlarmType = -1;

                switch (alarmType) {
                    case 0: //5 day warning
                        ringTime = fourDays;
                        title = "Book almost due!";
                        body = "Your book is due tomorrow.";
                        nextAlarmType = R.integer.ALARM_1_DAY_WARNING;
                        break;
                    case 1: //1 day warning
                        nextAlarmType = R.integer.ALARM_0_DAY_WARNING;
                    case 2: //0 day warning
                        nextAlarmType = nextAlarmType == -1 ? R.integer.ALARM_OVERDUE_NOTIFICATION : nextAlarmType;
                        ringTime = oneDay;
                        title = "You have an overdue book!";
                        body = "Your book is overdue! Please return it as soon as possible or you may be charged a fee.";
                        break;
                    case 3: //overdue
                        nextAlarmType = R.integer.ALARM_OVERDUE_NOTIFICATION;
                        ringTime = oneDay;
                        body = "Your book has been overdue for multiple days. Please return it soon!";
                        break;
                    case 4: //fee

                        break;
                }

                //Set up next alarm
                configureAlarm(ringTime, title, body, nextAlarmType);
            }

            //Destroy servcie
            stopSelf();
        }else{

            VALUE_BODY = "\"This is just a reminder that your book is due tomorrow. Return it soon or you may be charged a fee!\"";
            VALUE_TITLE = "\"Don't forget to return your book!\"";
            BID = -5;

            //Create the JSON object for the body
            createJSONObject();

            //Request the notification from the server
            sendNotification();
            stopSelf();
        }
            return super.onStartCommand(intent, flags, startId);

    }

    private void sendNotification() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                OkHttpClient client = new OkHttpClient();
                String URL = "https://fcm.googleapis.com/fcm/send";
                RequestBody body = RequestBody.create(JSON, jsonRequest.toString());
                okhttp3.Request request = new okhttp3.Request.Builder()
                        .header("Authorization", "key=" + SERVER_KEY)
                        .url(URL)
                        .post(body)
                        .build();
                try {
                    okhttp3.Response response = client.newCall(request).execute();
                    String finalResponse = response.body().string();
                }catch(IOException e){
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();

    }

    private void createJSONObject(){
        jsonRequest = null;
        try {
            //Create JSON POST object
            String jsonString = "{" +
                    PARAM_TO + " : " + VALUE_TO + ", " +
                    PARAM_PRIORITY + " : " + VALUE_PRIORITY + ", " +
                    PARAM_NOTIFICATION + " : {" +
                    PARAM_BODY + " : " + VALUE_BODY + ", " +
                    PARAM_TITLE + " : " + VALUE_TITLE +
                    "}" +
                    "}";
            jsonRequest = new JSONObject(jsonString);
        }catch(JSONException JSONE){
            Log.i("LoginActivity", "BAD OBJECT");
            stopSelf();
        }
    }

    @Override
    public void onDestroy() {
        Log.i("LoginActivity", "SERVICE DESTROIED");
        super.onDestroy();
    }

    /**
     * Configures the next alarm to send.
     *
     * @param ringTime The time from now in ms that the alarm should ring.
     * @param body The text body of the notification.
     * @param title The text title of the notification.
     * @param alarmType The type of alarm. This is provided to determine if another alarm should be sent after.
     */
    public void configureAlarm(long ringTime, String body, String title, int alarmType){
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);

        Intent intent = new Intent(this, RequestPushService.class);

        intent.putExtra("TO", FirebaseInstanceId.getInstance().getToken());
        intent.putExtra("BODY", body);
        intent.putExtra("TITLE", title);
        intent.putExtra("ALARM_TYPE", getResources().getInteger(alarmType));
        intent.putExtra("BID", BID);
        PendingIntent serviceIntent = PendingIntent.getService(this, getResources().getInteger(alarmType) + BID, intent, 0);

        alarmManager.set(
                AlarmManager.RTC,
                ringTime + System.currentTimeMillis(),
                serviceIntent
        );
    }
}
