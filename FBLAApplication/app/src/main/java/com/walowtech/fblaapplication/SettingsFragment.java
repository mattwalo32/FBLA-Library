package com.walowtech.fblaapplication;

import android.content.Context;
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

import java.util.Map;

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

        Preference crashApp = (Preference) findPreference("pref_key_crash_app");
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
    }
}
