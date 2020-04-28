package com.weblab.baragregator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

public class AddNewBarAdminActivity extends AppCompatActivity {

    String sessionToken;
    EditText emailField;
    EditText nameField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_bar_admin);

        sessionToken = getIntent().getStringExtra(CommonParameters.SESSION_TOKEN_TAG);
    }

    public void SendCredentialsToMerchantClicked(View view) {
        emailField = findViewById(R.id.MerchantEmailField);
        String email = emailField.getText().toString();

        nameField = findViewById(R.id.barNameField);
        String barName = nameField.getText().toString();

        // Send e-mail to the app server
        SendEmailToAppServer(email, barName);
    }

    public void AddingMerchantExitButtonClicked(View view) {
        Intent intent = new Intent(this, AppAdminRoleActivity.class);
        intent.putExtra(CommonParameters.SESSION_TOKEN_TAG, sessionToken);
        startActivity(intent);
        finish();
    }


    private void SendEmailToAppServer(String email, String barName) {
        RequestQueue queue = Volley.newRequestQueue(this);

        String url = CommonParameters.SERVER_IP + CommonParameters.ADD_BAR_REQ;


        JSONObject jsonBodyRequest = new JSONObject();

        try {
            jsonBodyRequest.put(CommonParameters.EMAIL_TAG, email);
            jsonBodyRequest.put(CommonParameters.SESSION_TOKEN_TAG, sessionToken);
            jsonBodyRequest.put(CommonParameters.BAR_NAME_TAG, barName);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBodyRequest,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString(CommonParameters.STATUS_TAG);
                            HandleStatus(status);

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


    private void HandleStatus(String status) {
        if (status.equals(CommonParameters.STATUS_OK)) {
            ShowMessageInToast("Password send to bar administrator by email");
            nameField.setText(null);
            emailField.setText(null);
        }
        else if (status.equals(CommonParameters.STATUS_SESSION_NOT_EXISTS)) {
            ShowMessageInToast("Session error!");
            finish();
        }
    }


    public void ShowMessageInToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
