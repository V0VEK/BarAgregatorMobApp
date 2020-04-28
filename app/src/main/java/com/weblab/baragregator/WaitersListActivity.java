package com.weblab.baragregator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class WaitersListActivity extends AppCompatActivity {


    String sessionToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiters_list);

        sessionToken = getIntent().getStringExtra(CommonParameters.SESSION_TOKEN_TAG);
    }


    public void WaitersListExitClicked(View view ) {
        finish();
    }


    public void WaitersListAddClicked(View view) {
        Intent intent = new Intent(this, AddWaiterActivity.class);
        intent.putExtra(CommonParameters.SESSION_TOKEN_TAG, sessionToken);
        startActivity(intent);
        finish();
    }


    public void WaitersListDeleteClicked(View view) {

    }
}
