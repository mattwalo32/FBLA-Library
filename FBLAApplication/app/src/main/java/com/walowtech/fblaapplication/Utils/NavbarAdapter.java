package com.walowtech.fblaapplication.Utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.walowtech.fblaapplication.AboutActivity;
import com.walowtech.fblaapplication.AccountActivity;
import com.walowtech.fblaapplication.LoginActivity;
import com.walowtech.fblaapplication.MainActivity;
import com.walowtech.fblaapplication.NavbarItem;
import com.walowtech.fblaapplication.R;
import com.walowtech.fblaapplication.SettingsActivity;

import java.util.ArrayList;

/**
 * Adapts information to NavBar.
 *
 * An ArrayList of NavbarItems is passed through the constructor
 * and is adapted to the navbar by setting the image and text and
 * returning the resultant view.
 *
 * @author Matthew Walowski
 * @version 1.0
 * @since 1.0
 */

//Created 9/24/17
public class NavbarAdapter extends ArrayAdapter<NavbarItem>{

    public NavbarAdapter(Context context, ArrayList<NavbarItem> data) {
        super(context, 0, data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final NavbarItem currentItem = getItem(position);
        if(convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.navbar_item, parent, false);

        ImageView imageView = (ImageView) convertView.findViewById(R.id.nav_drawer_item_image);
        TextView textView = (TextView) convertView.findViewById(R.id.nav_drawer_item_text);

        imageView.setImageResource(currentItem.image);
        textView.setText(currentItem.text);
        textView.setTypeface(MainActivity.handWriting);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int navItemIndex = currentItem.index;
                switch(navItemIndex){
                    case 0: //Account
                        Intent accountIntent = new Intent(getContext(), AccountActivity.class);
                        getContext().startActivity(accountIntent);
                        break;
                    case 1: //Settings
                        Intent settingIntent = new Intent(getContext(), SettingsActivity.class);
                        getContext().startActivity(settingIntent);
                        break;
                    case 2: //Information
                        Intent aboutIntent = new Intent(getContext(), AboutActivity.class);
                        getContext().startActivity(aboutIntent);
                        break;
                    case 3: //Fees

                        break;
                    case 4: //Logout
                        //Remove saved user data
                        SharedPreferences sharedPreferences = getContext().getSharedPreferences(getContext().getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                        SharedPreferences.Editor prefEditor = sharedPreferences.edit();
                        prefEditor.remove("UID");
                        prefEditor.remove("NAME");
                        prefEditor.remove("EMAIL");
                        prefEditor.remove("PASSWORD");
                        prefEditor.apply();

                        Intent loginIntent = new Intent(getContext(), LoginActivity.class);
                        getContext().startActivity(loginIntent);
                        break;
                }
            }
        });

        return convertView;
    }
}
