package com.weblab.baragregator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.List;

public class BarChoosingActivity extends AppCompatActivity {

    ListView barList;
    String sessionToken;
    List<String> barsNames;
    List<Integer> barsIDs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_choosing);

        sessionToken = getIntent().getStringExtra(CommonParameters.SESSION_TOKEN_TAG);


        BarListRequest();
    }


    private void BarListRequest() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = CommonParameters.SERVER_IP + CommonParameters.GET_BAR_LIST_REQ;

        JSONObject jsonBodyReq = new JSONObject();
        try {
            jsonBodyReq.put(CommonParameters.SESSION_TOKEN_TAG, sessionToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBodyReq,

                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String barList;
                        try {
                            String status = response.getString(CommonParameters.STATUS_TAG);
                            JSONArray ids = response.getJSONArray(CommonParameters.BAR_IDS_TAG);
                            JSONArray barsNames = response.getJSONArray(CommonParameters.BAR_NAMES_TAG);

                            HandleResponse(status, ids, barsNames);

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


    private void HandleResponse(String status, JSONArray ids, JSONArray names) {
        if (status.equals(CommonParameters.STATUS_OK)) {
            barsIDs = new ArrayList<>();
            barsNames = new ArrayList<>();
            for (int i = 0; i < ids.length(); i++) {
                try {
                    barsIDs.add(ids.getInt(i));
                    barsNames.add(names.getString(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            barList = findViewById(R.id.BarList);
            ArrayAdapter<String> aa = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, barsNames);
            barList.setAdapter(aa);
            barList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    StartReceiptNumberChoosingActivity(sessionToken, barsIDs.get(position));
                }
            });
        }
    }


    private void StartReceiptNumberChoosingActivity(String sessionToken, Integer barID) {
        Intent intent = new Intent(this, ReceiptNumberChoosingActivity.class);
        intent.putExtra(CommonParameters.SESSION_TOKEN_TAG, sessionToken);
        intent.putExtra(CommonParameters.BAR_ID_TAG, barID);
        startActivity(intent);
        finish();
    }


}
