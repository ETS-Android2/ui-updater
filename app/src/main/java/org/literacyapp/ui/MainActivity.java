package org.literacyapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.literacyapp.ui.service.StatusBarService;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(getClass().getName(), "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Ask for permission
        // TODO

        Intent serviceIntent = new Intent(this, StatusBarService.class);
        startService(serviceIntent);

        Intent intent = new Intent();
        intent.setAction("literacyapp.intent.action.STUDENT_UPDATED");
        intent.setPackage("org.literacyapp.ui");
        sendBroadcast(intent);

        finish();
    }
}
