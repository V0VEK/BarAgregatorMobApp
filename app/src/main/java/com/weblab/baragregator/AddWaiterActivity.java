package com.weblab.baragregator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class AddWaiterActivity extends AppCompatActivity {

    String sessionToken;
    EditText name;
    EditText email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_waiter);

        sessionToken = getIntent().getStringExtra(CommonParameters.SESSION_TOKEN_TAG);

    }


    public void BarAdminExitClicked(View view) {
        Intent intent = new Intent(this, WaitersListActivity.class);
        intent.putExtra(CommonParameters.SESSION_TOKEN_TAG, sessionToken);
        finish();
    }


    public void SendCredToWaiterClicked(View view) {
        email = findViewById(R.id.waiterEmailField);
        name = findViewById(R.id.waiterNameField);

        SendWaiterAdditionReq(email.getText().toString(), name.getText().toString());
    }


    private void SendWaiterAdditionReq(String email, String name) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = CommonParameters.SERVER_IP + CommonParameters.ADD_WAITER_REQ;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(CommonParameters.SESSION_TOKEN_TAG, sessionToken);
            jsonObject.put(CommonParameters.WAITER_NAME_TAG, name);
            jsonObject.put(CommonParameters.EMAIL_TAG, email);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            HandleStatus(response.getString(CommonParameters.STATUS_TAG));
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
            ShowMessageInToast("Credential sent to the waiter by e-mail");
            name.setText(null);
            email.setText(null);
        }
    }

    public void ShowMessageInToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}