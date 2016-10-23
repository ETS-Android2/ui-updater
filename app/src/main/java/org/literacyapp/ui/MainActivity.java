package org.literacyapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button mMainButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(getClass().getName(), "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        // Ask for root access (encourage user to select "Remember my choice")
//        try {
//            java.lang.Process process = Runtime.getRuntime().exec("su");
//
//            // Attempt to write a file to a root-only folder
//            DataOutputStream dataOutputStream = new DataOutputStream(process.getOutputStream());
//            dataOutputStream.writeBytes("echo \"Do I have root?\" >/system/sd/temporary.txt\n");
//
//            // Close the terminal
//            dataOutputStream.writeBytes("exit\n");
//            dataOutputStream.flush();
//
//            process.waitFor();
//
//            int exitValue = process.exitValue();
//            Log.i(getClass().getName(), "exitValue: " + exitValue);
//            if (exitValue == 1) {
//                // Root access denied
//                finish();
//                return;
//            } else {
//                // Root access allowed
//            }
//        } catch (IOException | InterruptedException e) {
//            Log.e(getClass().getName(), null, e);
//            // Root access denied
//            finish();
//            return;
//        }
//
//        mMainButton = (Button) findViewById(R.id.mainButton);
//        mMainButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.i(getClass().getName(), "mMainButton onClick");
//
//                Intent intent = new Intent();
//                intent.setAction("literacyapp.intent.NEW_STUDENT");
//                sendBroadcast(intent);
//
//                Toast.makeText(getApplicationContext(), "Background process started. Please wait...", Toast.LENGTH_LONG).show();
//
//                finish();
//            }
//        });

        Intent intent = new Intent();
        intent.setAction("literacyapp.intent.NEW_STUDENT");
        intent.setPackage("org.literacyapp.ui");
        sendBroadcast(intent);

        finish();
    }
}
