package com.weblab.baragregator;

import android.content.SharedPreferences;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {


    String userToken;
    String passHash;
    Button LogInButton;
    ProgressBar LogInProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        EmptyReq();
    }

    private void EmptyReq() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = CommonParameters.SERVER_IP + CommonParameters.INIT_REQ;

        StringRequest req = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        queue.add(req);
    }

    public void RegistrationForUserButtonClicked (View view) {
        StartRegistrationActivity();
    }

    public void RestorePasswordButtonClicked (View view) {
        StartRestoringPasswordActivity();
    }

    public void LogInButtonClicked (View view) throws NoSuchAlgorithmException {
        LogInButton = findViewById(R.id.LogInButton);
        LogInProgressBar = findViewById(R.id.LogInLoading);
        EditText passwordField = findViewById(R.id.passwordField);


        LogInButton.setVisibility(View.INVISIBLE);
        LogInProgressBar.setVisibility(View.VISIBLE);

        // Password sending
        String password = passwordField.getText().toString();
        passHash = "";
        MessageDigest md = MessageDigest.getInstance("SHA256");
        md.update(password.getBytes());
        passHash = CommonParameters.FromBytesToHexString(md.digest());
        //Log.d("PASS_HASH", passHash);

        userToken = GetDataFromSharedPreferences();
        SendAuthorizationReq(passHash, userToken);
    }


    public String GetDataFromSharedPreferences() {
        SharedPreferences sp = getSharedPreferences(CommonParameters.SHARED_PREF_NAME, MODE_PRIVATE);
        return sp.getString(CommonParameters.TOKEN_TAG, "ERROR");
    }


    private void SendAuthorizationReq(final String passHash, final String userToken) {


        RequestQueue queue = Volley.newRequestQueue(this);

        String url = CommonParameters.SERVER_IP + CommonParameters.AUTHORIZATION_REQ;

        Log.d("TOKEN", userToken);

        JSONObject jsonRequestBody = new JSONObject();
        try {
            jsonRequestBody.put(CommonParameters.PASSHASH_TAG, passHash);
            jsonRequestBody.put(CommonParameters.TOKEN_TAG, userToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonRequestBody,

                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String roleID;
                        String status;
                        String sessionToken;
                        try {
                            roleID = response.getString(CommonParameters.USER_ROLE_ID_TAG);
                            status = response.getString(CommonParameters.STATUS_TAG);
                            sessionToken = response.getString(CommonParameters.SESSION_TOKEN_TAG);
                            CheckStatusAndRoleID(status, roleID, sessionToken);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        queue.add(jsonObjectRequest);
    }


    private void CheckStatusAndRoleID(String status, String roleID, String sessionToken) {
        if (status.equals(CommonParameters.STATUS_OK)) {
            ShowMessageInToast(roleID);
            StartActivityDependsOnRoleID(roleID, sessionToken);
        }
        else if (status.equals(CommonParameters.STATUS_WRONG_PASSWORD)) {
            ShowMessageInToast("Wrong password!");
            LogInButton.setVisibility(View.VISIBLE);
            LogInProgressBar.setVisibility(View.INVISIBLE);
        }
        else if (status.equals(CommonParameters.STATUS_WRONG_TOKEN)) {
            StartRestoringTokenActivity();
        }
    }


    private void StartActivityDependsOnRoleID(String role, String sessionToken) {
        if (role.equals(CommonParameters.ROLE_ADMIN)) {
            StartAppAdminRoleActivity(sessionToken);
        }
        else if (role.equals(CommonParameters.ROLE_BAR)) {
            StartBarAdminActivity(sessionToken);
        }
        else if (role.equals(CommonParameters.ROLE_USER)) {
            StartSimpleUserRoleActivity(sessionToken);
        }
        else if (role.equals(CommonParameters.ROLE_WAITER)) {
            StartWaiterRoleActivity(sessionToken);
        }
    }


    public void MainExitButtonClicked(View view) {
        finish();
    }


    private void StartSimpleUserRoleActivity(String sessionToken) {
        Intent intent = new Intent(this, BarChoosingActivity.class);
        intent.putExtra(CommonParameters.SESSION_TOKEN_TAG, sessionToken);
        startActivity(intent);
        finish();
    }


    private void StartAppAdminRoleActivity(String sessionToken) {
        Intent intent = new Intent(this, AppAdminRoleActivity.class);
        intent.putExtra(CommonParameters.SESSION_TOKEN_TAG, sessionToken);
        startActivity(intent);
        finish();
    }

    private void StartBarAdminActivity(String sessionToken) {
        Intent intent = new Intent (this, WaitersListActivity.class);
        intent.putExtra(CommonParameters.SESSION_TOKEN_TAG, sessionToken);
        startActivity(intent);
        finish();
    }


    private void StartWaiterRoleActivity(String sessionToken) {
        Intent intent = new Intent (this, WaiterRoleActivity.class);
        intent.putExtra(CommonParameters.SESSION_TOKEN_TAG, sessionToken);
        startActivity(intent);
        finish();
    }


    private void StartRestoringPasswordActivity() {
        Intent intent = new Intent(this, RequestEmailActivity.class);
        intent.putExtra(CommonParameters.INTENT_FLAG, CommonParameters.RESTORE_PASSWORD_FLAG);
        startActivity(intent);
        finish();
    }

    private void StartRegistrationActivity() {
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
        finish();
    }

    private void StartRestoringTokenActivity() {
        Intent intent = new Intent(this, RequestEmailActivity.class);
        intent.putExtra(CommonParameters.INTENT_FLAG, CommonParameters.RESTORE_TOKEN_FLAG);
        intent.putExtra(CommonParameters.PASSHASH_TAG, passHash);
        startActivity(intent);
        finish();
    }


    public void ShowMessageInToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
