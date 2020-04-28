package com.weblab.baragregator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class ReceiptNumberChoosingActivity extends AppCompatActivity {

    String sessionToken;
    Integer barID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_number_choosing);

        sessionToken = getIntent().getStringExtra(CommonParameters.SESSION_TOKEN_TAG);
        barID = getIntent().getIntExtra(CommonParameters.BAR_ID_TAG, 0);

        Button findBtn = findViewById(R.id.UserRoleReceiptFindButton);
        findBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView receiptNumField = findViewById(R.id.UserRoleReceiptNumberField);
                String receiptNumS = receiptNumField.getText().toString();
                Integer receiptNum = Integer.parseInt(receiptNumS);
                StartDisplayProductsListInReceiptActivity(receiptNum);
            }
        });
    }

    public void ExitButtonClicked(View view) {
        Intent intent = new Intent(this, BarChoosingActivity.class);
        intent.putExtra(CommonParameters.SESSION_TOKEN_TAG, sessionToken);
        startActivity(intent);
        finish();
    }

    private void StartDisplayProductsListInReceiptActivity(Integer barReceiptID) {
        Intent intent = new Intent(this, UserProductsListActivity.class);
        intent.putExtra(CommonParameters.SESSION_TOKEN_TAG, sessionToken);
        intent.putExtra(CommonParameters.BAR_RECEIPT_ID_TAG, barReceiptID);
        intent.putExtra(CommonParameters.BAR_ID_TAG, barID);
        startActivity(intent);
        finish();
    }
}
