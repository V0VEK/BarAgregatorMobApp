package com.weblab.baragregator;

import android.content.Intent;
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

public class RequestEmailActivity extends AppCompatActivity {

    String flag;
    String email;
    String passHash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_email);

        flag = getIntent().getStringExtra(CommonParameters.INTENT_FLAG);

        if (flag.equals(CommonParameters.RESTORE_TOKEN_FLAG)) {
            passHash = getIntent().getStringExtra(CommonParameters.PASSHASH_TAG);
        }
    }


    public void RestoringOTPConfirmationClicked(View view) {
        EditText emailField = findViewById(R.id.RestoringE_mailField);
        email = emailField.getText().toString();

        SendEmailToServer(email);
    }



    private void SendEmailToServer(String email) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = CommonParameters.SERVER_IP + CommonParameters.OTP_REQ;

        JSONObject jsonReqBody = new JSONObject();
        try {
            jsonReqBody.put(CommonParameters.EMAIL_TAG, email);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonReqBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            HandleRespStatus(response.getString(CommonParameters.STATUS_TAG));
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


    private void HandleRespStatus(String status) {
        if (status.equals(CommonParameters.STATUS_OK)) {
            StartOTPConfirmationActivity();
            finish();
        }
        if (status.equals(CommonParameters.STATUS_USER_NOT_EXISTS)) {
            ShowMessageInToast("User with this email doesn't exists");
        }
    }


    private void StartOTPConfirmationActivity() {
        Intent intent = new Intent(this, OTPConfirmationActivity.class);
        intent.putExtra(CommonParameters.INTENT_FLAG, flag);
        intent.putExtra(CommonParameters.EMAIL_TAG, email);
        if (flag.equals(CommonParameters.RESTORE_TOKEN_FLAG)) {
            intent.putExtra(CommonParameters.PASSHASH_TAG, passHash);
        }
        startActivity(intent);
    }


    public void ShowMessageInToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
