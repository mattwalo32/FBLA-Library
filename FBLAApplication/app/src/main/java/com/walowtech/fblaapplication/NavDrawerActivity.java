package com.walowtech.fblaapplication;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.walowtech.fblaapplication.Utils.NavbarAdapter;

import java.util.ArrayList;

/**
 * Created by mattw on 9/29/2017.
 */

//TODO doc
public class NavDrawerActivity extends AppCompatActivity {

    public DrawerLayout drawerLayout;
    public ListView drawerList;
    public NavbarAdapter navbarAdapter;
    public ArrayList<NavbarItem> navbarItems = new ArrayList<>();
    public ActionBarDrawerToggle toggle;
    public View header;

    protected void onCreateDrawer(){
        drawerLayout = (DrawerLayout) findViewById(R.id.m_nav_drawer);

        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.string_empty, R.string.string_empty){
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        toggle.setDrawerIndicatorEnabled(true);
        drawerLayout.addDrawerListener(toggle);

        navbarItems.add(new NavbarItem(R.drawable.ic_account_black, "Account"));
        navbarItems.add(new NavbarItem(R.drawable.ic_settings_black, "Settings"));
        navbarItems.add(new NavbarItem(R.drawable.ic_info_black, "Information"));
        navbarItems.add(new NavbarItem(R.drawable.ic_fees_black, "Fees"));
        navbarItems.add(new NavbarItem(R.drawable.ic_logout_black, "Logout"));

        navbarAdapter = new NavbarAdapter(this, navbarItems);

        drawerList = (ListView) findViewById(R.id.nav_layout);
        header = getLayoutInflater().inflate(R.layout.navbar_header, null);
        drawerList.addHeaderView(header, null, false);

        drawerList.setAdapter(navbarAdapter);

        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO when item clicked
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return toggle != null && toggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if(toggle != null)
         toggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(toggle != null)
            toggle.onConfigurationChanged(newConfig);
    }
}
