package com.walowtech.fblaapplication;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.walowtech.fblaapplication.Utils.RequestPushService;

import java.util.Map;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by mattw on 10/22/2017.
 */

//TODO doc
public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.pref_general);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Set listener to clearLiked preference
        Preference clearLiked = (Preference) findPreference("pref_key_clear_liked");
        clearLiked.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                SharedPreferences sharedPreferences = getActivity().getApplicationContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                SharedPreferences.Editor prefEditor = sharedPreferences.edit();

                Map<String,?> keys = sharedPreferences.getAll();

                for(Map.Entry<String, ?> entry : keys.entrySet()){
                    if(entry.getKey().contains("LIKED"))
                        prefEditor.remove(entry.getKey());
                }

                prefEditor.commit();


                Toast.makeText(getActivity().getApplicationContext(), "Cleared liked books", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        //Set listener for app crash preference
        Preference crashApp = (Preference) findPreference("pref_key_crash_app");
        //This WILL throw an Index Out of Bound Exception. This is for demonstration purposes
        crashApp.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { //Crash button listener
            @Override
            //THIS WILL CRASH THE APP
            public boolean onPreferenceClick(Preference preference) {
                int[] list = new int[2];
                for(int i = 0; i < 5; i++){
                    list[i] = 1;
                }
                return true;
            }
        });

        //Set listener for forcing notification sequence
        Preference forceNotifications = (Preference) findPreference("pref_key_notification_seq");
        forceNotifications.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(getActivity(), RequestPushService.class);
                AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);

                intent.putExtra("TO", FirebaseInstanceId.getInstance().getToken());
                intent.putExtra("BODY", "This is just a reminder that a book you have checked out is due in 5 days.");
                intent.putExtra("TITLE", "Don't forget to return your book!");
                intent.putExtra("DEMONSTRATION", true);
                intent.putExtra("ALARM_TYPE", getResources().getInteger(R.integer.ALARM_5_DAY_WARNING));
                PendingIntent serviceIntent = PendingIntent.getService(getActivity(), -2, intent, 0);

                long currentTime = System.currentTimeMillis();

                alarmManager.set(
                        AlarmManager.RTC,
                        currentTime + 5000,
                        serviceIntent
                );

                Toast.makeText(getActivity(), "Close app to view notification - Notification will send in about 5 seconds.", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

    }
}
