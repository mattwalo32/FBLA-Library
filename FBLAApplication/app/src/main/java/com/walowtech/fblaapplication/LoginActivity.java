package com.walowtech.fblaapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.walowtech.fblaapplication.Utils.DownloadJSONLoader;
import com.walowtech.fblaapplication.Utils.ErrorUtils;
import com.walowtech.fblaapplication.Utils.NetworkJSONUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Activity for logging in
 *
 * This activity is used to allow the user to enter credentials and log in. If
 * the user has logged in before, their credentials will automatically be entered
 * and they will be logged in.
 *
 * @author Matthew Walowski
 * @version 1.0
 * @since 1.0
 */

//Created 9/10/2017
public class LoginActivity extends Activity implements LoaderManager.LoaderCallbacks{


    //TODO check app version
    private final String LOG_TAG = "LoginActivity";

    private boolean bypassInputs = false;

    private ConnectivityManager conn;

    private FrameLayout topPanel;
    private LinearLayout bottomPanel;

    private EditText editEmail;
    private EditText editPassword;

    private FloatingActionButton fab;

    private ProgressBar progressBar;

    private String email;
    private String password;

    private TextView welcomeMessage;
    private TextView textEmail;
    private TextView textPassword;
    private TextView createAccount;

    private Typeface handWriting;

    private final String BASE_URL = "walowtech.com";
    private final String PARAM_ACTION = "ACTION";
    private final String PARAM_EMAIL = "EMAIL";
    private final String PARAM_PASSWORD = "PASSWORD";
    private final String PATH0 = "apis";
    private final String PATH1 = "FBLALibrary";
    private final String PATH2 = "api.php";
    private final String SCHEME = "https";
    private final String VALUE_ACTION = "ACTION_RETRIEVE_ACCOUNT_DATA";

    private final String KEY_MESSAGE = "Message";
    private final String KEY_NAME = "Name";
    private final String KEY_SUCCESS = "SuccessCode";
    private final String KEY_UID = "UID";

    private JSONObject jsonResponse;
    private URL requestURL;

    private int UID, success;
    private String name, status, message;

    private final int DOWNLOAD_JSON_LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        topPanel = (FrameLayout) findViewById(R.id.l_fl_top_panel);
        bottomPanel = (LinearLayout) findViewById(R.id.l_ll_bottom_panel);

        editEmail = (EditText) findViewById(R.id.l_et_email);
        editPassword = (EditText) findViewById(R.id.l_et_password);
        EditText[] ets = {editEmail, editPassword};

        fab = (FloatingActionButton) findViewById(R.id.l_fab_bottom);
        progressBar = (ProgressBar) findViewById(R.id.l_progressbar);

        welcomeMessage = (TextView) findViewById(R.id.l_tv_welcome_message);
        textEmail = (TextView) findViewById(R.id.l_tv_email);
        textPassword = (TextView) findViewById(R.id.l_tv_password);
        createAccount = (TextView) findViewById(R.id.l_tv_create_account);

        handWriting = Typeface.createFromAsset(getAssets(), "fonts/hand_writing.ttf");
        welcomeMessage.setTypeface(handWriting);
        textEmail.setTypeface(handWriting);
        textPassword.setTypeface(handWriting);
        createAccount.setTypeface(handWriting);

        setElevations();
        setEditTextColors(ets, R.color.colorPrimary300);

        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        email = sharedPref.getString("EMAIL", null);
        password = sharedPref.getString("PASSWORD", null);

        if(NetworkJSONUtils.checkInternetConnection(this)) {
            if (email != null && password != null) {
                bypassInputs = true;
                login(null);
                bypassInputs = false;
            }
        }else{
            ErrorUtils.errorDialog(this, "Network Error", "It seems you don't have any network connection. Reset your connection and try again.");
        }
    }

    @Override
    public Loader<JSONObject> onCreateLoader(int id, Bundle args) {
        jsonResponse = null;
        return new DownloadJSONLoader(LoginActivity.this, requestURL);
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        if(data != null && data != " ") {
            try {
                jsonResponse = new JSONObject(data.toString());
                parseJSON(jsonResponse);
            } catch (JSONException JSONE) {
                ErrorUtils.errorDialog(this, "Data Error", "There was an error with the data format. Please try again later.");
            }
        }else{
            ErrorUtils.errorDialog(this, "Could not connect to server", "No information was retrieved from the server. Please try again later.");
        }

        fab.animate().scaleX(1f).scaleY(1f).alpha(250f).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(250);
        progressBar.setVisibility(View.GONE);

        getLoaderManager().destroyLoader(loader.getId());
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    /**
     * Starts Loader that authenticates user data
     *
     * First the URL to request is built with Uri.Builder
     * Then, the user's email and password are sent to the database
     * via URL in the form of a URL query, and the resulting
     * JSONObject from the input stream is returned and assigned
     * to the variable jsonResponse, which gets parsed.
     *
     * @param v The view that calls the method. This parameter is unused, but
     *          necessary for the onClick attribute to be implemented
     */
    public void login(View v){
        if(!NetworkJSONUtils.checkInternetConnection(this)) {
            ErrorUtils.errorDialog(this, "Network Error", "It seems you don't have any network connection. Reset your connection and try again.");
            return;
        }
        fab.animate().scaleX(.1f).scaleY(.1f).alpha(0f).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(250);
        progressBar.setVisibility(View.VISIBLE);

        setEditTextColors(new EditText[] {editPassword}, R.color.colorPrimary300);

        if(!bypassInputs) {
            email = editEmail.getText().toString();
            password = editPassword.getText().toString();
        }

        Uri.Builder builder = new Uri.Builder();
        builder.scheme(SCHEME)
                .authority(BASE_URL)
                .appendPath(PATH0)
                .appendPath(PATH1)
                .appendPath(PATH2)
                .appendQueryParameter(PARAM_ACTION, VALUE_ACTION)
                .appendQueryParameter(PARAM_EMAIL, email)
                .appendQueryParameter(PARAM_PASSWORD, password)
                .build();
        String urlString = builder.toString();


        try{
            requestURL = new URL(urlString);
        }catch(MalformedURLException MURLE){
            ErrorUtils.errorDialog(this, "There was an error connecting to the server", "Currently the server can not be reached. Make sure your username and password are entered correctly");
            return;
        }

        getLoaderManager().initLoader(DOWNLOAD_JSON_LOADER, null, this);
    }

    /**
     * Parses a JSON object into seperate variables
     *
     * The JSONObject is parsed, and each value of
     * significance is saved in a variable, then
     * written to shared preferences.
     *
     * @param jsonObject The JSONObject to parse
     */
    private void parseJSON(JSONObject jsonObject){
        try {
            success = jsonObject.getInt(KEY_SUCCESS);
            if(success == 1){
                UID = jsonObject.getInt(KEY_UID);
                name = jsonObject.getString(KEY_NAME);

                SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                SharedPreferences.Editor sharedPrefEditor = sharedPref.edit();
                sharedPrefEditor.putInt("UID", UID);
                sharedPrefEditor.putString("NAME", name);
                sharedPrefEditor.putString("EMAIL", email);
                sharedPrefEditor.putString("PASSWORD", password);
                sharedPrefEditor.apply();

                Toast.makeText(this, "Welcome Back!", Toast.LENGTH_SHORT).show();

                launchMainActivity();
            }else{
                message = jsonObject.getString(KEY_MESSAGE);

                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

                Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
                editPassword.setText("");
                editPassword.startAnimation(shake);
                setEditTextColors(new EditText[] {editPassword}, R.color.colorInvalid);
            }
        }catch(JSONException JSONE){
            ErrorUtils.errorDialog(this, "Data Format Error", "There seems to be an error with the data format. Please try again later.");
        }
    }

    /**
     * Transitions to CreateAccountActivity
     *
     * Creates smooth transition animation from LoginActivity
     * to CreateAccountActivity, and launches CreateAccountActivity
     *
     * @param v The view that calls the method. This parameter is unused, but
     *          necessary for the onClick attribute to be implemented
     */
    public void launchCreateAccount(View v){
        Intent i = new Intent(this, CreateAccountActivity.class);

        Pair<View, String> p1 = Pair.create((View)createAccount, getString(R.string.trans_create_account));
        Pair<View, String> p2 = Pair.create((View)topPanel, getString(R.string.trans_top_panel));
        Pair<View, String> p3 = Pair.create((View)bottomPanel, getString(R.string.trans_bottom_panel));
        Pair<View, String> p4 = Pair.create((View)textEmail, getString(R.string.trans_tv_email));
        Pair<View, String> p5 = Pair.create((View)editEmail, getString(R.string.trans_et_email));
        Pair<View, String> p6 = Pair.create((View)textPassword, getString(R.string.trans_tv_password));
        Pair<View, String> p7 = Pair.create((View)editPassword, getString(R.string.trans_et_password));

        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, p1, p2, p3, p4, p5, p6, p7);
        startActivity(i, options.toBundle());
    }

    /**
     * launches the main activity and clears stack
     */
    public void launchMainActivity(){
        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    /**
     * Sets the elevation of predefined views
     *
     * All elevations for the current activity
     * are set upon this method being called
     */
    private void setElevations(){
        ViewCompat.setElevation(topPanel, 8);
        ViewCompat.setElevation(bottomPanel, 12);
    }


    /**
     * Sets the color of the edit text line
     *
     * All EditText views are passed in as an array and
     * each text line color is changed from the default to
     * the app theme color. This method is used  instead of
     * xml to ensure compatibility across supported APIs.
     *
     * @param ets An array of EditText views to have their line color changed
     */
    private void setEditTextColors(EditText[] ets, int color){
        for(EditText et : ets){
            Drawable drawable = et.getBackground();
            drawable.setColorFilter(ContextCompat.getColor(this, color), PorterDuff.Mode.SRC_ATOP);

            if(Build.VERSION.SDK_INT > 16){
                et.setBackground(drawable);
            }else{
                et.setBackgroundDrawable(drawable);
            }
        }
    }
}
