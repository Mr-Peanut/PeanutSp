package com.guan.peanutsp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
public class MainActivity extends AppCompatActivity  {
    public static final int TYPE_GENDER=0;
    public static final int TYPE_LANGUAGE=1;
    public static final int TYPE_VOLUME=2;
    public static final int TYPE_SPEED=3;
    public static final int GENDER_SPECIALMALE=3;
    public static final int GENDER_MOTIONALMALE=4;
    public static final int GENDER_MALE=1;
    public static final int GENDER_FEMALE=0;
    public static final int LANGUAGE_CN=0;
    public static final int LANGUAGE_EN=1;
    private static final String PACKAGE_URL_SCHEME = "package:";
    private final static String CONTROLERACTION = "com.guan.SpeakerControler";
    public static List<Activity> activities=new ArrayList<>();
    private TextView input;
    private TextView statuText;
    private RadioGroup gender;
    private SeekBar volAdjuster;
    private SeekBar speedAdjuster;
    private DrawerLayout drawerLayout;
    private boolean getPermission = false;
    private Button settingButton;
    private int REQUEST_CODE = 1;

    //    private static int backTime=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        activities.add(this);
        setContentView(R.layout.activity_main);
        initView();
        permissionCheck();
    }

    private void permissionCheck() {
        Log.e("permissionCheck", "permissionCheck");
        if ((ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);
            Log.e("permissionCheck", "permissionCheck");
        } else {
            getPermission = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getPermission = true;
            } else {
                getPermission = false;
                Toast.makeText(this, "储存权限被禁止，请在设置—应用—" + String.valueOf(getApplicationContext().getPackageManager().getApplicationLabel(getApplicationInfo()))
                        + "—权限—储存，打开储存权限后重新打开APP", Toast.LENGTH_SHORT).show();
                settingButton.setVisibility(View.VISIBLE);
                settingButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent settingIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        settingIntent.setData(Uri.parse(PACKAGE_URL_SCHEME + getPackageName()));
                        startActivity(settingIntent);
                    }
                });
            }
        }
    }

    @Override
    protected void onResume() {

        super.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        permissionCheck();
        if (getPermission) {
            settingButton.setVisibility(View.INVISIBLE);
        } else {
            settingButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout .isDrawerOpen(GravityCompat.START)) {
            drawerLayout .closeDrawer(GravityCompat.START);
//            backTime=0;
        } else {
            CloseConfirmFragment dialogFragment =new CloseConfirmFragment();
//            dialogFragment.setmController();
            dialogFragment.setActivities(activities);
            dialogFragment.show(getSupportFragmentManager(),"mydialog");
//            backTime++;
//            if(backTime==1){
//                Toast.makeText(this,"再次点击后退键，退出程序",Toast.LENGTH_SHORT).show();
//            }if(backTime==2){
//            finish();
//            }
        }
    }
    private void initSetting() {
        gender= (RadioGroup) findViewById(R.id.speakergender);
        RadioGroup language = (RadioGroup) findViewById(R.id.speakerLanguage);
        volAdjuster = (SeekBar) findViewById(R.id.voladjuster);
        speedAdjuster= (SeekBar) findViewById(R.id.speedjuster);
        RadioGroup.OnCheckedChangeListener groupListener = new GroupListener();
        gender.setOnCheckedChangeListener(groupListener);
        language.setOnCheckedChangeListener(groupListener);
        SeekBar.OnSeekBarChangeListener mSeekBarListener = new SeekbarListener();
        volAdjuster.setOnSeekBarChangeListener(mSeekBarListener);
        speedAdjuster.setOnSeekBarChangeListener(mSeekBarListener);
    }
    private void initView() {
        settingButton = (Button) findViewById(R.id.settingButton);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.app_name));
        toolbar.setLogo(R.mipmap.ic_launcher);
        toolbar.inflateMenu(R.menu.toolbar_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Toast.makeText(MainActivity.this, String.valueOf(item.getItemId()), Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        statuText = (TextView) findViewById(R.id.statuText);
        input = (TextView) findViewById(R.id.inputText);
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                statuText.setText("你输入了" + count + "字节，还可以输入" + (1024 - count) + "字节");
            }

            @Override
            public void afterTextChanged(Editable s) {
                statuText.setText("你输入了" + s.length() + "字节，还可以输入" + (1024 - s.length()) + "字节");
            }
        });
        Button sendButton = (Button) findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (input.getText() == null || input.getText().toString().equals("")) {
                    Toast.makeText(MainActivity.this, "请输入要发声的内容！", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(MainActivity.this, SpeakerService.class);
                    intent.putExtra("InputText", input.getText().toString());
                    startService(intent);
                }
            }
        });
        Button combineButton = (Button) findViewById(R.id.combinButton);
        combineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //导出文件的逻辑
            }
        });
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        MyToggle myToggle = new MyToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        MDrawerListener myToggleLister = new MDrawerListener() {
            @Override
            public void OnDrawerOpen() {
                Intent intent = new Intent(MainActivity.this, SpeakerService.class);
                intent.putExtra("InputText", input.getText().toString());
                startService(intent);
                if (gender == null) {
                    initSetting();
                }
            }
        };
        myToggle.setmDrawerLayoutListener(myToggleLister);
        drawerLayout.setDrawerListener(myToggle);
        myToggle.syncState();
    }

    private void sendControllerMes(int type, String extraTag, int value) {
        Intent controlIntent = new Intent(CONTROLERACTION);
        controlIntent.putExtra("CONTROLTYPE", type);
        controlIntent.putExtra(extraTag, value);
        sendBroadcast(controlIntent);
    }

    @Override
    protected void onDestroy() {
        activities.remove(this);
        super.onDestroy();
    }

    @Override
    public void finish() {
        stopService(new Intent(this, SpeakerService.class));
        super.finish();
    }

    class GroupListener implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.male:
                    sendControllerMes(TYPE_GENDER,"GENDER",GENDER_MALE);
                    break;
                case R.id.female:
                    sendControllerMes(TYPE_GENDER,"GENDER",GENDER_FEMALE);
                    break;
                case R.id.specialmale:
                    sendControllerMes(TYPE_GENDER,"GENDER",GENDER_SPECIALMALE);
                    break;
                case R.id.motionalmale:
                    sendControllerMes(TYPE_GENDER,"GENDER",GENDER_MOTIONALMALE);
                    break;
                case R.id.cn:
                    sendControllerMes(TYPE_LANGUAGE,"LANGUAGE",LANGUAGE_CN);
                    break;
                case R.id.en:
                      sendControllerMes(TYPE_LANGUAGE,"LANGUAGE",LANGUAGE_EN);
                    break;
            }
        }
    }

    class SeekbarListener implements SeekBar.OnSeekBarChangeListener{

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            switch (seekBar.getId()){
                case R.id.speedjuster:
                    sendControllerMes(TYPE_SPEED,"SPEED",speedAdjuster.getProgress());
                    break;
                case R.id.voladjuster:
                    sendControllerMes(TYPE_VOLUME,"SPEED",volAdjuster.getProgress());
                    break;
            }
        }
    }
}
