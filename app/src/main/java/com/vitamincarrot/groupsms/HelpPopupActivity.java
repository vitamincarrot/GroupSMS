package com.vitamincarrot.groupsms;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

public class HelpPopupActivity extends Activity {

    CheckBox checkBox;
    Button button;
    Intent data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_help_popup);

        checkBox = findViewById(R.id.closeViewHelpCheck);
        button = findViewById(R.id.closeViewHelpBtn);

        data = new Intent();
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                data.putExtra("checked", isChecked);
                Log.d("HelpPopupActivity_Log", "(isChecked) " + isChecked);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK, data);
                finish();
            }
        });
    }
}