package com.walowtech.fblaapplication;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
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
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.walowtech.fblaapplication.Utils.ValidationUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Activity that allows user to create account
 *
 * The credentials entered by the user are passed to
 * the server, and a new account is created
 *
 * @author Matthew Walowski
 * @version 1.0
 * @since 1.0
 */

//Created 9/14/17
public class CreateAccountActivity extends Activity implements LoaderManager.LoaderCallbacks{

    private FrameLayout topPanel;
    private LinearLayout bottomPanel;

    private EditText editName;
    private EditText editEmail;
    private EditText editPassword;
    private EditText editSchoolID;

    private FloatingActionButton fab;

    JSONObject jsonResponse;

    ProgressBar progressBar;

    private String name;
    private String email;
    private String password;
    private String schoolID;

    private TextView textCreateAccount;
    private TextView textName;
    private TextView textEmail;
    private TextView textPassword;
    private TextView textSchoolID;

    private Typeface handWriting;

    private final String BASE_URL = "walowtech.com";
    private final String PARAM_ACTION = "ACTION";
    private final String PARAM_NAME = "NAME";
    private final String PARAM_EMAIL = "EMAIL";
    private final String PARAM_PASSWORD = "PASSWORD";
    private final String PARAM_SCHOOLID = "SID";
    private final String PATH0 = "apis";
    private final String PATH1 = "FBLALibrary";
    private final String PATH2 = "api.php";
    private final String SCHEME = "https";
    private final String VALUE_ACTION = "ACTION_CREATE_NEW_USER";

    private final String KEY_MESSAGE = "message";
    private final String KEY_SUCCESS = "successCode";
    private final String KEY_UID = "UID";

    private URL requestURL;

    private int UID, success;
    private String message;

    private final int DOWNLOAD_JSON_LOADER = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        topPanel = (FrameLayout) findViewById(R.id.ca_fl_top_panel);
        bottomPanel = (LinearLayout) findViewById(R.id.ca_ll_bottom_panel);

        editName = (EditText) findViewById(R.id.ca_et_name);
        editEmail = (EditText) findViewById(R.id.ca_et_email);
        editPassword = (EditText) findViewById(R.id.ca_et_password);
        editSchoolID = (EditText) findViewById(R.id.ca_et_school_id);
        EditText[] ets = {editName, editEmail, editPassword, editSchoolID};

        fab = (FloatingActionButton) findViewById(R.id.ca_fab_bottom);
        progressBar = (ProgressBar) findViewById(R.id.ca_progressbar);

        textCreateAccount = (TextView) findViewById(R.id.ca_tv_create_account);
        textName = (TextView) findViewById(R.id.ca_tv_name);
        textEmail = (TextView) findViewById(R.id.ca_tv_email);
        textPassword = (TextView) findViewById(R.id.ca_tv_password);
        textSchoolID = (TextView) findViewById(R.id.ca_tv_school_id);

        handWriting = Typeface.createFromAsset(getAssets(), "fonts/hand_writing.ttf");
        textCreateAccount.setTypeface(handWriting);
        textName.setTypeface(handWriting);
        textEmail.setTypeface(handWriting);
        textPassword.setTypeface(handWriting);
        textSchoolID.setTypeface(handWriting);

        setElevations();
        setEditTextColors(ets, R.color.colorPrimary300);
    }

    @Override
    public Loader<JSONObject> onCreateLoader(int id, Bundle args) {
        jsonResponse = null;
        return new DownloadJSONLoader(this, requestURL);
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        if(data != null || data != "") {
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
     * Starts Loader that creates user data
     *
     * First the data is validated and the URL request is built with Uri.Builder
     * Then, the user's email, password, name and ID are sent to the database
     * via URL in the form of a URL query, and the resulting
     * JSONObject from the input stream is returned and assigned
     * to the variable jsonResponse, which gets parsed.
     *
     * @param v The view that calls the method. This parameter is unused, but
     *          necessary for the onClick attribute to be implemented
     */
    public void createAccount(View v){
        if(!NetworkJSONUtils.checkInternetConnection(this)) {
            ErrorUtils.errorDialog(this, "Network Error", "It seems you don't have any network connection. Reset your connection and try again.");
            return;
        }
        setEditTextColors(new EditText[] {editEmail, editSchoolID}, R.color.colorPrimary300);

        name = editName.getText().toString();
        email = editEmail.getText().toString();
        password = editPassword.getText().toString();
        schoolID = editSchoolID.getText().toString();

        if(!ValidationUtils.validateName(name).first){
            editName.setError(ValidationUtils.validateName(name).second);
            return;
        }
        if(!ValidationUtils.validateEmail(email).first){
            editEmail.setError(ValidationUtils.validateEmail(email).second);
            return;
        }
        if(!ValidationUtils.validatePassword(password).first){
            editPassword.setError(ValidationUtils.validatePassword(password).second);
            return;
        }
        if(!ValidationUtils.validateSchoolID(schoolID).first){
            editSchoolID.setError(ValidationUtils.validateSchoolID(schoolID).second);
            return;
        }

        fab.animate().scaleX(.1f).scaleY(.1f).alpha(0f).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(250);
        progressBar.setVisibility(View.VISIBLE);

        Uri.Builder builder = new Uri.Builder();
        builder.scheme(SCHEME)
                .authority(BASE_URL)
                .appendPath(PATH0)
                .appendPath(PATH1)
                .appendPath(PATH2)
                .appendQueryParameter(PARAM_ACTION, VALUE_ACTION)
                .appendQueryParameter(PARAM_NAME, name)
                .appendQueryParameter(PARAM_EMAIL, email)
                .appendQueryParameter(PARAM_PASSWORD, password)
                .appendQueryParameter(PARAM_SCHOOLID, schoolID)
                .build();
        String urlString = builder.toString();

        try{
            requestURL = new URL(urlString);
        }catch(MalformedURLException MURLE){
            ErrorUtils.errorDialog(this, "There was an error with the url", "Currently the server can not be reached. Make sure your username and password are entered correctly");
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

                SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                SharedPreferences.Editor sharedPrefEditor = sharedPref.edit();
                sharedPrefEditor.putInt("UID", UID);
                sharedPrefEditor.putString("NAME", name);
                sharedPrefEditor.putString("EMAIL", email);
                sharedPrefEditor.putString("PASSWORD", password);
                sharedPrefEditor.apply();

                Toast.makeText(this, "Account Created!", Toast.LENGTH_SHORT).show();

                launchMainActivity();
            }else{
                message = jsonObject.getString(KEY_MESSAGE);

                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

                Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
                if(success == 0){
                    editSchoolID.setText("");
                    editSchoolID.startAnimation(shake);
                    setEditTextColors(new EditText[]{editSchoolID}, R.color.colorInvalid);
                }else if(success == 2) {
                    editEmail.setText("");
                    editEmail.startAnimation(shake);
                    setEditTextColors(new EditText[]{editEmail}, R.color.colorInvalid);
                }
            }
        }catch(JSONException JSONE){
            ErrorUtils.errorDialog(this, "Data Format Error", "There seems to be an error with the data format. Please try again later.");
        }
    }

    /**
     * launches the main activity and clears stack.
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
     * xml to ensure compatiability across supported APIs.
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
