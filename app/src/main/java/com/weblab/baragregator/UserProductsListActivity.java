package com.weblab.baragregator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class UserProductsListActivity extends AppCompatActivity {

    String sessionToken;
    Integer barReceiptID;
    Integer barID;
    Integer uniqueReceiptID;
    ListView prodList;
    List<Integer> productsIDs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_products_list);

        sessionToken = getIntent().getStringExtra(CommonParameters.SESSION_TOKEN_TAG);
        barReceiptID = getIntent().getIntExtra(CommonParameters.BAR_RECEIPT_ID_TAG, 0);
        barID = getIntent().getIntExtra(CommonParameters.BAR_ID_TAG, 0);

        SendReceiptReq(sessionToken, barID, barReceiptID);
    }



    private void SendReceiptReq(String sessionToken, Integer barID, Integer barReceiptID) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = CommonParameters.SERVER_IP + CommonParameters.GET_BAR_RECEIPT;

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(CommonParameters.SESSION_TOKEN_TAG, sessionToken);
            jsonObject.put(CommonParameters.BAR_ID_TAG, barID);
            jsonObject.put(CommonParameters.BAR_RECEIPT_ID_TAG, barReceiptID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString(CommonParameters.STATUS_TAG);
                            Integer uniqueReceiptID = response.getInt(CommonParameters.UNIQUE_RECEIPT_ID_TAG);
                            JSONArray ids = response.getJSONArray(CommonParameters.PRODUCTS_ID_TAG);
                            JSONArray productsNames = response.getJSONArray(CommonParameters.PRODUCTS_NAMES);

                            HandleResponse(status, uniqueReceiptID, ids, productsNames);
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


    private void HandleResponse(String status, Integer uniqueRecID, JSONArray prodIDs, JSONArray prodNames) {
        if (status.equals(CommonParameters.STATUS_OK)) {
            productsIDs = new ArrayList<>();
            List<String> productsNames = new ArrayList<>();
            uniqueReceiptID = uniqueRecID;

            for (int i = 0; i < prodIDs.length(); i++) {
                try {
                    productsIDs.add(prodIDs.getInt(i));
                    productsNames.add(prodNames.getString(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            prodList = findViewById(R.id.ReceiptProductsListView);
            ArrayAdapter<String> aa = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, productsNames);
            prodList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            prodList.setAdapter(aa);
        }
    }




    public void PayButtonClicked(View view) {
        List<Integer> chosenProducts = new ArrayList<>();
        SparseBooleanArray chosenItems = prodList.getCheckedItemPositions();
        for (int i = 0; i < chosenItems.size(); i++) {
            if (chosenItems.valueAt(i) == true) {
                chosenProducts.add(productsIDs.get(chosenItems.keyAt(i)));
            }
        }
        SendPaymentReq(chosenProducts);
    }


    private void SendPaymentReq(List<Integer> paidProducts) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = CommonParameters.SERVER_IP + CommonParameters.PAYMENT_REQ;

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(CommonParameters.UNIQUE_RECEIPT_ID_TAG, uniqueReceiptID);
            jsonObject.put(CommonParameters.SESSION_TOKEN_TAG, sessionToken);
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < paidProducts.size(); i++) {
                jsonArray.put(paidProducts.get(i));
            }
            jsonObject.put(CommonParameters.PRODUCTS_ID_TAG, jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
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
            ShowMessageInToast("Payment completed successfully");
            ReloadPage();
        }
    }


    private void ReloadPage() {
        Intent intent = new Intent(this, UserProductsListActivity.class);
        intent.putExtra(CommonParameters.SESSION_TOKEN_TAG, sessionToken);
        intent.putExtra(CommonParameters.BAR_ID_TAG, barID);
        intent.putExtra(CommonParameters.BAR_RECEIPT_ID_TAG, barReceiptID);
        startActivity(intent);
        finish();
    }


    public void ProductsListUserExitButtonClicked(View view) {
        finish();
    }



    public void ShowMessageInToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
