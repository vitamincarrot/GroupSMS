package com.example.groupsms;

import androidx.appcompat.app.AppCompatActivity;
import jxl.Workbook;

import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    EditText num;
    EditText msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        num = findViewById(R.id.phonenumber);
        msg = findViewById(R.id.message);
        Button sendbtn = findViewById(R.id.sendbtn);

        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputText = num.getText().toString();
                String inputText2 = msg.getText().toString();
                if (inputText.length() > 0 && inputText2.length() > 0) {
                    sendSMS(inputText,inputText2);
                    Toast.makeText(getBaseContext(),inputText + "\n"+inputText2,Toast.LENGTH_SHORT).show();

                }
                else {
                    Toast.makeText(getBaseContext(), "Enter the PhoneNumber and Message!", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void sendSMS(String phonenum, String message){
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phonenum,null,message,null,null);
    }

}