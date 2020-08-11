package com.example.groupsms;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;
    EditText num, msg;
    Button sendBtn, retryBtn, addBtn, checkDelBtn, inputButton1;
    String inputText1 = "Text1";
    RecyclerView recyclerView;
    /*public RecyclerView.Adapter mAdapter;
    public RecyclerView.LayoutManager layoutManager;*/
    ArrayList<String> myDataset = new ArrayList<>();
    ArrayList<String> checkText = new ArrayList<>();
    File file;
    Workbook workbook;
    List<String> excelTitle, excelContent, excelThumb;
    String path = "/data/data/Download/androidExcelTest.xls";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        num = findViewById(R.id.phonenumber);
        msg = findViewById(R.id.message);
        sendBtn = findViewById(R.id.sendbtn);
        retryBtn = findViewById(R.id.retrybtn);
        addBtn = findViewById(R.id.add);
        recyclerView = findViewById(R.id.recyclerView);
        checkDelBtn = findViewById(R.id.checkDel);
        inputButton1 = findViewById(R.id.inputText1);

        excelTitle = new ArrayList<>();
        excelContent = new ArrayList<>();
        excelThumb = new ArrayList<>();
        Log.d("MainActivity_Log", "(onCreate) " + path);

        file = new File(path);

        final RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        final MyAdapter adapter = new MyAdapter(myDataset);
        recyclerView.setAdapter(adapter);

        checkForSmsPermission();
        excelRead(file);

        inputButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertSomeText(inputText1);
            }
        });

        inputButton1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.d("MainActivity_Log", "(changeTextBtn1) Long Clicked.");
                final LinearLayout linear = (LinearLayout) View.inflate(MainActivity.this, R.layout.dialog_buttontextchange, null);

                new AlertDialog.Builder(MainActivity.this)
                        .setView(linear)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                EditText changeBtnText1 = linear.findViewById(R.id.changeEditText1);
                                inputText1 = changeBtnText1.getText().toString();
                                inputButton1.setText(inputText1);
                                dialog.dismiss();
                            }
                        })

                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
                return true;
            }
        });

        checkDelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkText = adapter.checkPos;
                for (int i = 0; i < checkText.size(); i++) {
                    myDataset.remove(checkText.get(i));
                }

                adapter.checkPos.removeAll(adapter.checkPos);
                adapter.notifyDataSetChanged();

            }
        });


        //"+"버튼 클릭시 번호 리스트 추가
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (num.length() > 0) {
                    myDataset.add(num.getText().toString());
                    Log.d("MainActivity_Log", "(addBtn) Data add : " + myDataset.get(0));
                    adapter.notifyDataSetChanged();
                    num.getText().clear();

                } else {
                    toastMsgShort("Enter the phonenumber!");

                }

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

    private void insertSomeText(String st) {
        int start = Math.max(msg.getSelectionStart(), 0);
        int end = Math.max(msg.getSelectionEnd(), 0);
        msg.getText().replace(Math.min(start, end), Math.max(start, end), st);
    }


    //SMS 메세지 보내기
    private void sendSMS(String phoneNum, String message) {
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNum, null, message, null, null);
    }

    //SMS 보내기 권한 허용 했는지 확인
    private void checkForSmsPermission() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS) !=
                PackageManager.PERMISSION_GRANTED) {
            Log.d("MainActivity_Log", "(checkForSmsPermission) Dont grented!");
            // Permission not yet granted. Use requestPermissions().
            // MY_PERMISSIONS_REQUEST_SEND_SMS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    MY_PERMISSIONS_REQUEST_SEND_SMS);

            Log.d("MainActivity_Log", "(checkForSmsPermission) " + Manifest.permission.SEND_SMS);

        } else {
            // Permission already granted. Enable the SMS button.
            Log.d("MainActivity_Log", "(checkForSmsPermission) Grented!");
            Log.d("MainActivity_Log", "(checkForSmsPermission) " + Manifest.permission.SEND_SMS);
            enableSmsButton();
        }
    }

    //Retry 버튼 보이게
    private void disableSmsButton() {
        Toast.makeText(this, "SMS usage disabled", Toast.LENGTH_LONG).show();
        sendBtn.setVisibility(View.INVISIBLE);
        retryBtn.setVisibility(View.VISIBLE);
    }

    //Send 버튼 보이게
    private void enableSmsButton() {
        sendBtn.setVisibility(View.VISIBLE);
        retryBtn.setVisibility(View.INVISIBLE);
    }

    //Toast 메세지 길게
    private void toastMsgLong(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }

    //Toast 메세지 짧게
    private void toastMsgShort(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    // 권한을 요구시 어떤 버튼(허용, 거부)을 눌렀는지 응답을 받음.
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_SEND_SMS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("MainActivity_Log", "(onRequestPermissionsResult) Permission has been granted");
                enableSmsButton();
            } else {
                Log.d("MainActivity_Log", "(onRequestPermissionsResult) Permission has been denied or request cancelled");
                disableSmsButton();
            }
        }
    }

    public void excelRead(File file) {
        WorkbookSettings ws = new WorkbookSettings();
        ws.setGCDisabled(true);
        if (file != null) {
            try {
                workbook = Workbook.getWorkbook(file);
                Sheet sheet = workbook.getSheet(0);
                for (int i = 0; i < sheet.getRows(); i++) {
                    Cell[] row = sheet.getRow(i);
                    excelTitle.add(row[0].getContents());
                    excelContent.add(row[1].getContents());
                    excelThumb.add(row[2].getContents());

                }
                for (int i = 0; i < excelTitle.size(); i++) {
                    Log.d("MainActivity_Log", "(excelRead) " + excelTitle.get(i));
                    Log.d("MainActivity_Log", "(excelRead) " + excelContent.get(i));
                    Log.d("MainActivity_Log", "(excelRead) " + excelThumb.get(i));

                }

            } catch (IOException e) {
                Log.d("MainActivity_Log", "(excelRead) IOException" + e);

                e.printStackTrace();
            } catch (BiffException e) {
                Log.d("MainActivity_Log", "(excelRead) BiffException" + e);

                e.printStackTrace();
            }
        }
    }

}