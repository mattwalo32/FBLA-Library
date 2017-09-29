package com.walowtech.fblaapplication.Utils;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.walowtech.fblaapplication.MainActivity;
import com.walowtech.fblaapplication.NavbarItem;
import com.walowtech.fblaapplication.R;

import java.util.ArrayList;

/**
 * Created by mattw on 9/24/2017.
 */

//Created 9/24/17
public class NavbarAdapter extends ArrayAdapter<NavbarItem>{

    public NavbarAdapter(Context context, ArrayList<NavbarItem> data) {
        super(context, 0, data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NavbarItem currentItem = getItem(position);
        if(convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.navbar_item, parent, false);

        ImageView imageView = (ImageView) convertView.findViewById(R.id.nav_drawer_item_image);
        TextView textView = (TextView) convertView.findViewById(R.id.nav_drawer_item_text);

        imageView.setImageResource(currentItem.image);
        textView.setText(currentItem.text);
        textView.setTypeface(MainActivity.handWriting);

        return convertView;
    }
}
