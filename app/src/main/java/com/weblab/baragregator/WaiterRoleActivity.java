package com.weblab.baragregator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class WaiterRoleActivity extends AppCompatActivity {

    ListView receiptsListView;
    String sessionToken;
    int selectedPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiter_role);

        sessionToken = getIntent().getStringExtra(CommonParameters.SESSION_TOKEN_TAG);

    }


    private void Init() {
        selectedPosition = 0;
    }

    private void SendReceiptListReq() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = CommonParameters.SERVER_IP + CommonParameters.WAITER_ROLE_REQ;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(CommonParameters.SESSION_TOKEN_TAG, sessionToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public void AddReceiptClicked(View view) {
        EditText receiptNum = findViewById(R.id.NewReceiptNumField);
        String num = receiptNum.getText().toString();

        Integer n = Integer.parseInt(num);

        StartReceiptAddingActivity(n);
    }

    private void StartReceiptAddingActivity(Integer receiptNum){
        Intent intent = new Intent(this, ReceiptAddingActivity.class);
        intent.putExtra(CommonParameters.BAR_RECEIPT_ID_TAG, receiptNum);
        intent.putExtra(CommonParameters.SESSION_TOKEN_TAG, sessionToken);
        startActivity(intent);
        finish();
    }

}
