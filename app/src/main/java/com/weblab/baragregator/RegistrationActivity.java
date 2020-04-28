package com.weblab.baragregator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class RegistrationActivity extends AppCompatActivity {

    String emailAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
    }


    public void RegisterButtonClicked(View view) {
        Button regButton = findViewById(R.id.RegistrationSubmitButton);
        ProgressBar progressBar = findViewById(R.id.RegistrationLoading);

        regButton.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);


        // Тут надо проверить e-mail на валидность
        //..
        EditText tmpEditText = findViewById(R.id.RegistrationE_mailField);
        emailAddress = tmpEditText.getText().toString().trim();

        tmpEditText = findViewById(R.id.RegistrationPasswordField);
        String password = tmpEditText.getText().toString().trim();

        SendCredentialsForRegistration(emailAddress, password);

    }


    private void SendCredentialsForRegistration(final String email, final String passwordHash) {
        RequestQueue queue = Volley.newRequestQueue(this);

        String url = CommonParameters.SERVER_IP + CommonParameters.REGISTRATION_REQ;

        JSONObject jsonRequestBody = new JSONObject();
        try {
            jsonRequestBody.put(CommonParameters.EMAIL_TAG, email);
            jsonRequestBody.put(CommonParameters.PASSHASH_TAG, passwordHash);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonRequestBody,

                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            HandleStatus(response.getString(CommonParameters.STATUS_TAG));
                            finish();
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
        if (status.equals(CommonParameters.STATUS_USER_EXISTS)) {
            ShowMessageInToast("User with this email already exists");
        }
        else if (status.equals(CommonParameters.STATUS_OK)) {
            StartOTPConfirmationActivity();
        }
    }


    private void StartOTPConfirmationActivity() {
        Intent intent = new Intent(this, OTPConfirmationActivity.class);
        intent.putExtra(CommonParameters.EMAIL_TAG, emailAddress);
        intent.putExtra(CommonParameters.INTENT_FLAG, CommonParameters.REGISTRATION_FLAG);
        startActivity(intent);
    }

    public void ShowMessageInToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
