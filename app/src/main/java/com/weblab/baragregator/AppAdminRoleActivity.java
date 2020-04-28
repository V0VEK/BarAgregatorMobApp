package com.weblab.baragregator;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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

import static android.media.CamcorderProfile.get;

public class AppAdminRoleActivity extends AppCompatActivity {

    String sessionToken;
    ListView barList;
    List<String> namesList;
    List<Integer> idsList;
    int selectedPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_admin_role);

        sessionToken = getIntent().getStringExtra(CommonParameters.SESSION_TOKEN_TAG);

        Init();
    }


    private void Init() {
        selectedPosition = 0;
        BarListReq();
    }

    public void AddBarButtonClicked (View view) {
        StartBarAddingActivity();
    }


    public void DeleteBarButtonClicked (View view) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = CommonParameters.SERVER_IP + CommonParameters.DELETE_BAR_REQ;

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(CommonParameters.SESSION_TOKEN_TAG, sessionToken);
            jsonObject.put(CommonParameters.BAR_ID_TAG, idsList.get(selectedPosition));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString(CommonParameters.STATUS_TAG);
                            ShowMessageInToast(status);
                            Restart();

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


    private void BarListReq() {
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
                        try {
                            String status = response.getString(CommonParameters.STATUS_TAG);
                            JSONArray names = response.getJSONArray(CommonParameters.BAR_NAMES_TAG);
                            JSONArray ids = response.getJSONArray(CommonParameters.BAR_IDS_TAG);

                            HandleStatus(status, names, ids);

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


    private void HandleStatus(String status, JSONArray names, JSONArray ids) {

        if (status.equals(CommonParameters.STATUS_OK)) {
            namesList = new ArrayList<>();
            idsList = new ArrayList<>();
            for (int i = 0; i < names.length(); i++) {
                try {
                    namesList.add(names.getString(i));
                    idsList.add(ids.getInt(i));
                    //Log.d("BAR", names.getString(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        barList = findViewById(R.id.AdminBarList);
        ArrayAdapter<String> aa = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, namesList);
        barList.setAdapter(aa);
        barList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        barList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedPosition = position;
            }
        });
    }


    private void Restart() {
        Intent intent = new Intent(this, AppAdminRoleActivity.class);
        intent.putExtra(CommonParameters.SESSION_TOKEN_TAG, sessionToken);
        startActivity(intent);
    }

    private void StartBarAddingActivity() {
        Intent intent = new Intent(this, AddNewBarAdminActivity.class);
        intent.putExtra(CommonParameters.SESSION_TOKEN_TAG, sessionToken);
        startActivity(intent);
        finish();
    }


    public void AdminExitButtonClicked (View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void ShowMessageInToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
