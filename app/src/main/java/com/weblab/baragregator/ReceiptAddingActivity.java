package com.weblab.baragregator;

import android.app.DownloadManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

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
import java.util.List;

public class ReceiptAddingActivity extends AppCompatActivity {

    ListView productsList;
    String sessionToken;
    Integer barReceiptID;
    List<String> prodNames;
    List<Integer> prodIDs;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_adding);

        sessionToken = getIntent().getStringExtra(CommonParameters.SESSION_TOKEN_TAG);
        barReceiptID = getIntent().getIntExtra(CommonParameters.BAR_RECEIPT_ID_TAG, 0);

        Init();
    }


    private void Init() {
        SendProductsListReq();
    }


    public void ReceiptAddConfirmClicked(View view) {
        SparseBooleanArray items = productsList.getCheckedItemPositions();
        List<Integer> prodIDsAdded = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            if (items.valueAt(i) == true) {
                prodIDsAdded.add(prodIDs.get(items.keyAt(i)));
            }
        }
        SendReceiptToServer(prodIDsAdded);
    }


    private void SendReceiptToServer(List<Integer> prodIDsAdded) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = CommonParameters.SERVER_IP + CommonParameters.ADD_RECEIPT_REQ;

        JSONObject jsonObject = new JSONObject();
        try {
            JSONArray arr = new JSONArray();
            for (Integer id : prodIDsAdded) {
                arr.put(id);
            }
            jsonObject.put(CommonParameters.PRODUCTS_ID_TAG, arr);
            jsonObject.put(CommonParameters.SESSION_TOKEN_TAG, sessionToken);
            jsonObject.put(CommonParameters.BAR_RECEIPT_ID_TAG, barReceiptID);
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
            ShowMessageInToast("Receipt added successfully");
            ReturnToWaiterActivity();
        }
    }


    private void ReturnToWaiterActivity() {
        Intent intent = new Intent (this, WaiterRoleActivity.class);
        intent.putExtra(CommonParameters.SESSION_TOKEN_TAG, sessionToken);
        startActivity(intent);
        finish();
    }


    private void SendProductsListReq() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = CommonParameters.SERVER_IP + CommonParameters.GET_BAR_PRODUCTS_REQ;

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(CommonParameters.SESSION_TOKEN_TAG, sessionToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString(CommonParameters.STATUS_TAG);
                            JSONArray names = response.getJSONArray(CommonParameters.PRODUCTS_LIST_TAG);
                            JSONArray ids = response.getJSONArray(CommonParameters.PRODUCTS_ID_TAG);

                            HandleResponse(names, ids, status);
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


    private void HandleResponse(JSONArray names, JSONArray ids, String status) {
        if (status.equals(CommonParameters.STATUS_OK)) {
            prodNames = new ArrayList<>();
            prodIDs = new ArrayList<>();
            for (int i = 0; i < names.length(); i++) {
                try {
                    prodNames.add(names.getString(i));
                    prodIDs.add(ids.getInt(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            productsList = findViewById(R.id.BarProductsList);
            ArrayAdapter<String> aa = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, prodNames);
            productsList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            productsList.setAdapter(aa);
        }
    }


    public void ShowMessageInToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}
