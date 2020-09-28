package com.vitamincarrot.groupsms;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;
    EditText num, name, msg, editText1, editText2, editText3, editText4, editText5;
    Button helpBtn, sendBtn, retryBtn, addBtn, checkDelBtn, inputButton1, inputButton2, inputButton3, inputButton4, inputButton5, readBtn, createBtn;
    String inputText1 = "!$Text1$!",
            inputText2 = "!$Text2$!",
            inputText3 = "!$Text3$!",
            inputText4 = "!$Text4$!",
            inputText5 = "!$Text5$!";
    RecyclerView recyclerView;
    ArrayList<ItemList> myDataSet = new ArrayList<>();
    Workbook workbook;
    private int READ_REQUEST_CODE = 43;
    private int WRITE_REQUEST_CODE = 44;
    private int HELP_REQUEST_CODE = 45;
    int mRows, mColumns, maxSend = 10;
    MyAdapter adapter;
    CheckBox allCheckBox;
    private AdView mAdView;
    private String adUnitId = "ca-app-pub-8572963075903678/5591060883";
    private String adInterstitaialId = "ca-app-pub-8572963075903678/3128549075";
    private String testUnitId = "ca-app-pub-3940256099942544/6300978111";
    private String testInterstitaialId = "ca-app-pub-3940256099942544/1033173712";
    private InterstitialAd mInterstitialAd;
    Boolean helpCloseCheck;
//    private ParcelFileDescriptor pfd;
//    private FileOutputStream fileOutputStream;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        AdView adView = new AdView(this);
        adView.setAdUnitId(adUnitId);
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(adInterstitaialId);
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                mInterstitialAd.loadAd(new AdRequest.Builder().build());

            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        loadBoolean("helpChecked");
        if (!helpCloseCheck) {

            Intent intent = new Intent(getBaseContext(), HelpPopupActivity.class);
            startActivityForResult(intent, HELP_REQUEST_CODE);
        }

        allFindViewById();

        final RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        adapter = new MyAdapter(myDataSet);
        recyclerView.setAdapter(adapter);

        checkForSmsPermission();

        helpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), HelpPopupActivity.class);
                startActivity(intent);
            }
        });

        //엑셀 불러오기 버튼 클릭 시
        readBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartRead();

            }
        });

        //엑셀 샘플파일 생성 버튼 클릭 시
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartWrite();

            }
        });

        //치환 TEXT1 버튼 클릭 시
        inputButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertSomeText(inputText1);
            }
        });

        //치환 TEXT2 버튼 클릭 시
        inputButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertSomeText(inputText2);
            }
        });

        //치환 TEXT3 버튼 클릭 시
        inputButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertSomeText(inputText3);
            }
        });

        //치환 TEXT4 버튼 클릭 시
        inputButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertSomeText(inputText4);
            }
        });

        //치환 TEXT5 버튼 클릭 시
        inputButton5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertSomeText(inputText5);
            }
        });
   
        //체크항목 삭제 버튼 클릭 시
        checkDelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (int i = 0; i < adapter.checkPos.size(); i++) {
                    myDataSet.remove(adapter.checkPos.get(i));
                }
                adapter.checkPos.clear();

                adapter.notifyDataSetChanged();
                allCheckBox.setChecked(false);

            }
        });

        //"+"버튼 클릭시 번호 리스트 추가
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final LinearLayout linear = (LinearLayout) View.inflate(MainActivity.this, R.layout.dialog_selfaddbutton, null);
//
                new AlertDialog.Builder(MainActivity.this)
                        .setView(linear)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                editText1 = linear.findViewById(R.id.editText1);
                                editText2 = linear.findViewById(R.id.editText2);
                                editText3 = linear.findViewById(R.id.editText3);
                                editText4 = linear.findViewById(R.id.editText4);
                                editText5 = linear.findViewById(R.id.editText5);
                                num = linear.findViewById(R.id.phonenumber);
                                name = linear.findViewById(R.id.name);

                                if (num.length() > 0) {
                                    ItemList itemList = new ItemList();
                                    itemList.setName(name.getText().toString());
                                    itemList.setPhoneNumber(num.getText().toString());
                                    itemList.setText1(editText1.getText().toString());
                                    itemList.setText2(editText2.getText().toString());
                                    itemList.setText3(editText3.getText().toString());
                                    itemList.setText4(editText4.getText().toString());
                                    itemList.setText5(editText5.getText().toString());

                                    myDataSet.add(itemList);
                                    Log.d("MainActivity_Log", "(addBtn) Data add : " + myDataSet.get(0));
                                    adapter.notifyDataSetChanged();

                                    dialog.dismiss();

                                } else {
                                    toastMsgShort("전화번호가 입력되지 않았습니다!");
                                    dialog.cancel();

                                }


                            }
                        })

                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();


            }


        });

        //메세지 보내기 버튼 클릭 시
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputMsgText = msg.getText().toString();

                if (myDataSet.size() > 0 && inputMsgText.length() > 0) {
                    if (maxSend > myDataSet.size()) {
                        for (int i = 0; i < myDataSet.size(); i++) {
                            String reMsgText = inputMsgText;

                            for (int j = 0; j < 5; j++) {
                                if (inputMsgText.contains("!$Text1$!")) {
                                    reMsgText = reMsgText.replace("!$Text1$!", myDataSet.get(i).getText1());
                                }

                                if (inputMsgText.contains("!$Text2$!")) {
                                    reMsgText = reMsgText.replace("!$Text2$!", myDataSet.get(i).getText2());
                                }

                                if (inputMsgText.contains("!$Text3$!")) {
                                    reMsgText = reMsgText.replace("!$Text3$!", myDataSet.get(i).getText3());
                                }

                                if (inputMsgText.contains("!$Text4$!")) {
                                    reMsgText = reMsgText.replace("!$Text4$!", myDataSet.get(i).getText4());
                                }

                                if (inputMsgText.contains("!$Text5$!")) {
                                    reMsgText = reMsgText.replace("!$Text5$!", myDataSet.get(i).getText5());
                                }
                            }

                            if (reMsgText.length() > 0) {

                                sendSMS(myDataSet.get(i).getPhoneNumber(), reMsgText);
                                toastMsgShort("메세지 보내는 중... " + (i + 1) + " / " + myDataSet.size());
                            } else {
                                toastMsgLong("전송할 메세지가 없습니다.");
                            }
                        }

                    } else {
                        toastMsgLong("발송 가능한 최대 메세지는 " + (maxSend - 1) + "개 입니다.");
                    }


                } else {
                    toastMsgLong("전화번호나 메세지가 입력되지 않았습니다!");
                }

                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                } else {
                    toastMsgShort("The interstitial wasn't loaded yet.");
                }

            }
        });

        //권한허용 재시도 버튼 클릭 시
        retryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkForSmsPermission();
            }
        });

        allCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    adapter.checkPos.addAll(myDataSet);
                } else {
                    adapter.checkPos.clear();
                }
                adapter.notifyDataSetChanged();
            }
        });

    }


    //불러올 파일 선택창 열기
    public void StartRead() {
        try {
            /*long now = System.currentTimeMillis();
            Date date = new Date(now);
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdfNow
                    = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String formatDate = sdfNow.format(date);
*/


            /*
             * SAF 파일 편집
             * */
/*
            String fileName = formatDate + ".txt";
*/

            Intent intent = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            }
            assert intent != null;
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
/*
            intent.putExtra(Intent.EXTRA_TITLE, fileName);
*/

            startActivityForResult(intent, READ_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //저장할 파일 선택창 열기
    public void StartWrite() {
        Intent intent = null;
        String fileName = "Sample.xls";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        }
        assert intent != null;
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_TITLE, fileName);

        startActivityForResult(intent, WRITE_REQUEST_CODE);
    }

    //선택된 파일 정보 받아오기
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Log.d("MainActivity_Log", "(onActivityResult) READ_REQUEST_CODE");

            Uri uri = data.getData();
            Log.d("MainActivity_Log", "(onActivityResult) " + uri);

            try {
                InputStream file = null;
                if (uri != null) {
                    file = getApplicationContext().getContentResolver().openInputStream(uri);
                }
                excelRead(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.d("MainActivity_Log", "(onCreate) " + e);

            }
        } else if (requestCode == WRITE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            try {
                if (uri != null) {
                    OutputStream outputStream = getApplication().getContentResolver().openOutputStream(uri);
                    excelCreate(outputStream);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        } else if (requestCode == HELP_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            helpCloseCheck = data.getBooleanExtra("checked", false);
            saveBoolean(helpCloseCheck);
            Log.d("MainActivity_Log", "(onActivityResult_HELP_REQUEST_CODE) " + helpCloseCheck);

        }
    }

    //중간에 텍스트 삽입하기
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

    // 엑셀파일 불러오기
    private void excelRead(InputStream file) {
        WorkbookSettings ws = new WorkbookSettings();
        ws.setGCDisabled(true);
        if (file != null) {
            try {
                workbook = Workbook.getWorkbook(file);
                Sheet sheet = workbook.getSheet(0);
                Log.d("MainActivity_Log", "(excelRead getRows) " + sheet.getRows());
                mRows = sheet.getRows();
                mColumns = sheet.getColumns();
                ArrayList<String> cellList = new ArrayList<>();
                if (mColumns < 8) {
                    for (int i = 1; i < mRows; i++) {
                        for (int j = 0; j < 7; j++) {
                            Cell cell = sheet.getCell(j, i);
                            String temp = cell.getContents();
                            cellList.add(j, temp);
                            Log.d("MainActivity_Log", "(excelRead getRows) " + "(" + i + "," + j + ")" + temp);

                        }
                        ItemList itemList = new ItemList();

                        itemList.setName(cellList.get(0));
                        itemList.setPhoneNumber(cellList.get(1));
                        itemList.setText1(cellList.get(2));
                        itemList.setText2(cellList.get(3));
                        itemList.setText3(cellList.get(4));
                        itemList.setText4(cellList.get(5));
                        itemList.setText5(cellList.get(6));
                        myDataSet.add(itemList);
                    }
                    adapter.notifyDataSetChanged();

                } else {
                    toastMsgLong("열(Column)의 문자 개수가 너무 많습니다. ");
                }


            } catch (IOException e) {
                e.printStackTrace();
            } catch (BiffException e) {
                e.printStackTrace();
            }
           /* for (int i = 0; i < text1.size(); i++) {
                Log.d("MainActivity_Log", "(excelRead) " + text1.get(i));
                Log.d("MainActivity_Log", "(excelRead) " + text2.get(i));
                Log.d("MainActivity_Log", "(excelRead) " + text3.get(i));
                Log.d("MainActivity_Log", "(excelRead) " + text4.get(i));
                Log.d("MainActivity_Log", "(excelRead) " + text5.get(i));

            }*/

        }

    }

    // 샘플 엑셀파일 생성하기
    private void excelCreate(OutputStream outputStream) {
        try {
            WritableWorkbook workbook = Workbook.createWorkbook(outputStream);
            WritableSheet sheet = workbook.createSheet("example", 0);
            Label label;
            label = new Label(0, 0, "Name");
            sheet.addCell(label);

            label = new Label(1, 0, "PhoneNumber");
            sheet.addCell(label);

            label = new Label(2, 0, "Text1");
            sheet.addCell(label);

            label = new Label(3, 0, "Text2");
            sheet.addCell(label);

            label = new Label(4, 0, "Text3");
            sheet.addCell(label);

            label = new Label(5, 0, "Text4");
            sheet.addCell(label);

            label = new Label(6, 0, "Text5");
            sheet.addCell(label);

            workbook.write();
            workbook.close();
            toastMsgLong("샘플 엑셀파일이 만들어 졌습니다!");

        } catch (IOException e) {
            e.printStackTrace();
        } catch (RowsExceededException e) {
            e.printStackTrace();
        } catch (WriteException e) {
            e.printStackTrace();
        }


    }

    //FindViewById 실행하기
    private void allFindViewById() {
        msg = findViewById(R.id.message);
        sendBtn = findViewById(R.id.sendbtn);
        retryBtn = findViewById(R.id.retrybtn);
        addBtn = findViewById(R.id.add);
        recyclerView = findViewById(R.id.recyclerView);
        checkDelBtn = findViewById(R.id.checkDel);
        inputButton1 = findViewById(R.id.inputText1);
        inputButton2 = findViewById(R.id.inputText2);
        inputButton3 = findViewById(R.id.inputText3);
        inputButton4 = findViewById(R.id.inputText4);
        inputButton5 = findViewById(R.id.inputText5);
        readBtn = findViewById(R.id.readBtn);
        createBtn = findViewById(R.id.createBtn);
        allCheckBox = findViewById(R.id.allCheck);
        helpBtn = findViewById(R.id.helpBtn);
    }

    //도움말 "더 이상 보지않기" 체크 저장
    public void saveBoolean(Boolean data) {
        SharedPreferences sharedPreferences = getSharedPreferences("saveChecked", MODE_PRIVATE);    // test 이름의 기본모드 설정
        SharedPreferences.Editor editor = sharedPreferences.edit(); //sharedPreferences를 제어할 editor를 선언
        editor.putBoolean("helpChecked", helpCloseCheck); // key,value 형식으로 저장
        editor.apply();    //최종 커밋. 커밋을 해야 저장이 된다.
    }

    //도움말 "더 이상 보지않기" 체크 불러오기
    public void loadBoolean(String key) {
        SharedPreferences sharedPreferences = getSharedPreferences("saveChecked", MODE_PRIVATE);    // test 이름의 기본모드 설정, 만약 test key값이 있다면 해당 값을 불러옴.
        helpCloseCheck = sharedPreferences.getBoolean(key, false);  // TextView에 SharedPreferences에 저장되어있던 값 찍기.
    }
}