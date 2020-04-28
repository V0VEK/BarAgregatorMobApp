package com.weblab.baragregator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class OTPConfirmationActivity extends AppCompatActivity {

    String flag;
    String email;
    String passHash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpconfirmation);

        // flag indicates what need to restore: password or token
        flag = getIntent().getStringExtra(CommonParameters.INTENT_FLAG);

        if (flag.equals(CommonParameters.RESTORE_TOKEN_FLAG)) {
            passHash = getIntent().getStringExtra(CommonParameters.PASSHASH_TAG);
        }

        // email got from previous activity need to send request to the server
        email = getIntent().getStringExtra(CommonParameters.EMAIL_TAG);
    }


    public void OTPVerificationExitClicked(View view) {
        finish();
    }


    public void OTPConfirmationClicked(View view) {
        EditText otpField = findViewById(R.id.OTPField);
        String otp = otpField.getText().toString().trim();
        SendOTPToTheServer(otp, email);
    }


    private void SendOTPToTheServer(String otp, String email) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = CommonParameters.SERVER_IP + CommonParameters.OTP_CONFIRMATION_REQ;

        JSONObject jsonReqBody = new JSONObject();
        try {
            jsonReqBody.put(CommonParameters.EMAIL_TAG, email);
            jsonReqBody.put(CommonParameters.OTP_TAG, otp);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonReqBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String status;
                        String token;
                        try {
                            status = response.getString(CommonParameters.STATUS_TAG);
                            token = response.getString(CommonParameters.TOKEN_TAG);
                            HandleResponseStatus(status, token);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        queue.add(jsonObjectRequest);
    }


    private void HandleResponseStatus(String status, String token) {
        if (status.equals(CommonParameters.STATUS_WRONG_OTP)) {
            ShowMessageInToast("Wrong OTP!");
        }
        else if (status.equals(CommonParameters.STATUS_OK)) {
            SaveTokenInSharedPreferences(CommonParameters.TOKEN_TAG, token);
            // Check flag and start another activity depends on this flag
            if (flag.equals(CommonParameters.RESTORE_PASSWORD_FLAG)) {
                StartSettingNewPasswordActivity();
            }
            else if (flag.equals(CommonParameters.RESTORE_TOKEN_FLAG)) {
                LogInWithNewToken(token);
            }
        }
    }


    private void LogInWithNewToken(String token) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = CommonParameters.SERVER_IP + CommonParameters.AUTHORIZATION_REQ;
        JSONObject jsonBodyReq = new JSONObject();
        try {
            jsonBodyReq.put(CommonParameters.PASSHASH_TAG, passHash);
            jsonBodyReq.put(CommonParameters.TOKEN_TAG, token);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBodyReq,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString(CommonParameters.STATUS_TAG);
                            String sessionToken = response.getString(CommonParameters.SESSION_TOKEN_TAG);
                            String roleID = response.getString(CommonParameters.USER_ROLE_ID_TAG);
                            HandleStatusAndRole(status, roleID, sessionToken);
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

    private void HandleStatusAndRole(String status, String role, String sessionToken) {

        if (status.equals(CommonParameters.STATUS_OK)) {
            StartActivityDependsOnRoleID(role, sessionToken);
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
        //Intent intent = new Intent (this, )
        finish();
    }


    private void StartWaiterRoleActivity(String sessionToken) {
        Intent intent = new Intent (this, WaiterRoleActivity.class);
        intent.putExtra(CommonParameters.SESSION_TOKEN_TAG, sessionToken);
        startActivity(intent);
        finish();
    }


    private void StartSettingNewPasswordActivity() {
        Intent intent = new Intent(this, SetNewPassActivity.class);
        startActivity(intent);
        finish();
    }


    private void SaveTokenInSharedPreferences(String tag, String token) {
        SharedPreferences sPref = getSharedPreferences(CommonParameters.SHARED_PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString(tag, token);
        ed.commit();
    }




    private void ShowMessageInToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
