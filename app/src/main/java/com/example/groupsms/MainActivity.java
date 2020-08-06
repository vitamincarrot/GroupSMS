package com.example.groupsms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    EditText num;
    EditText msg;
    Button sendBtn;
    Button retryBtn;
    Button addBtn;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    ArrayList<String> myDataset = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        num = findViewById(R.id.phonenumber);
        msg = findViewById(R.id.message);
        sendBtn = findViewById(R.id.sendbtn);
        retryBtn = findViewById(R.id.retrybtn);
        addBtn = findViewById(R.id.add);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);


        checkForSmsPermission();


        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                myDataset.add(num.getText().toString());
                Log.d("data", "Data add : " + myDataset.get(0));

                RecyclerView recyclerView = findViewById(R.id.recyclerView);
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

                MyAdapter adapter = new MyAdapter(myDataset);
                recyclerView.setAdapter(adapter);

            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputMsgText = msg.getText().toString();
                if (myDataset.size() > 0 && inputMsgText.length() > 0) {
                    for (int i = 0; i < myDataset.size(); i++) {
                        sendSMS(myDataset.get(i), inputMsgText);
                        toastMsgShort(myDataset.get(i) + "\n" + inputMsgText);

                    }

                } else {
                    toastMsgLong("Enter the PhoneNumber and Message!");
                }

            }
        });

        retryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkForSmsPermission();
            }
        });




    }

    private void sendSMS(String phoneNum, String message) {
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNum, null, message, null, null);
    }

    private void checkForSmsPermission() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS) !=
                PackageManager.PERMISSION_GRANTED) {
            Log.d("checkself", "Dont grented!");
            // Permission not yet granted. Use requestPermissions().
            // MY_PERMISSIONS_REQUEST_SEND_SMS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    MY_PERMISSIONS_REQUEST_SEND_SMS);

            Log.d("checkself", "" + Manifest.permission.SEND_SMS);

        } else {
            // Permission already granted. Enable the SMS button.
            Log.d("checkself", "Grented!");
            Log.d("checkself", "" + Manifest.permission.SEND_SMS);
            enableSmsButton();
        }
    }

    private void disableSmsButton() {
        Toast.makeText(this, "SMS usage disabled", Toast.LENGTH_LONG).show();
        sendBtn.setVisibility(View.INVISIBLE);
        retryBtn.setVisibility(View.VISIBLE);
    }

    private void enableSmsButton() {
        sendBtn.setVisibility(View.VISIBLE);
        retryBtn.setVisibility(View.INVISIBLE);
    }

    private void toastMsgLong(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }

    private void toastMsgShort(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    // 권한을 요구시 어떤 버튼(허용, 거부)을 눌렀는지 응답을 받음.
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_SEND_SMS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("checkself", "Permission has been granted");
                enableSmsButton();
            } else {
                Log.d("checkself", "Permission has been denied or request cancelled");
                disableSmsButton();
            }
        }
    }

}