package com.guan.peanutsp;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.baidu.tts.auth.AuthInfo;
import com.baidu.tts.client.SpeechError;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static com.baidu.tts.client.SpeechSynthesizer.AUDIO_BITRATE_AMR_15K85;
import static com.baidu.tts.client.SpeechSynthesizer.AUDIO_ENCODE_AMR;
import static com.baidu.tts.client.SpeechSynthesizer.MIX_MODE_DEFAULT;
public class SpeakerService extends Service {
    private BroadcastReceiver speakerControler;
    public SpeakerService() {
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    static final String CN_FEMALE_FILENAME = "bd_etts_speech_female.dat";
    static final String CN_MALE_FILENAME = "bd_etts_speech_male.dat";
    static final String CN_TEXT_FILENAME = "bd_etts_text.dat";
    static final String EN_FEMALE_FILENAME = "bd_etts_speech_female_en.dat";
    static final String EN_MALE_FILENAME = "bd_etts_speech_male_en.dat";
    static final String EN_TEXT_FILENAME = "bd_etts_text_en.dat";
    private final static String CONTROLERACTION="com.guan.SpeakerControler";
    private SpeechSynthesizer speechSynthesizer;
    private String parentPath;
    @Override
    public void onCreate() {
        super.onCreate();
        if(speechSynthesizer==null){
            initSpeechSynthesize();
        }
        if (speakerControler==null)
        ininBroadcast();
    }

    private void ininBroadcast() {
        speakerControler = new Controller();
        IntentFilter intentfilter = new IntentFilter(CONTROLERACTION);
        registerReceiver(speakerControler,intentfilter);
    }

    private void initSpeechSynthesize() {
        speechSynthesizer = SpeechSynthesizer.getInstance();
        speechSynthesizer.setContext(getApplicationContext());
        speechSynthesizer.setSpeechSynthesizerListener(new SpeechSynthesizerListener() {
            @Override
            public void onSynthesizeStart(String s) {
                Log.d("test2", "开始数据");
            }

            @Override
            public void onSynthesizeDataArrived(String s, byte[] bytes, int i) {
                Log.d("test2", "数据到了 ");

            }

            @Override
            public void onSynthesizeFinish(String s) {
                Log.d("test2", "结束 ");
            }

            @Override
            public void onSpeechStart(String s) {
                Log.d("test2", "说话了 ");
            }

            @Override
            public void onSpeechProgressChanged(String s, int i) {
            }

            @Override
            public void onSpeechFinish(String s) {
                Log.d("test2", "说完了 ");
            }

            @Override
            public void onError(String arg0, SpeechError arg1) {
                // TODO Auto-generated method stub
//                Log.d("test2", "不好了，出错了 ");
                Toast.makeText(getApplicationContext(), "抱歉出错了，请稍后重试", Toast.LENGTH_SHORT).show();
            }
        });
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE,
                parentPath + File.separator + CN_TEXT_FILENAME);
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE,
                parentPath + File.separator + CN_FEMALE_FILENAME);
        speechSynthesizer.setAppId("8900167");
        speechSynthesizer.setApiKey("N6I9aushznHZsTTLel7NOPyA", "80c5bfe7d18e59430072eef0b7a076fe");
        AuthInfo authInfo = speechSynthesizer.auth(TtsMode.MIX);
        Log.d("authInfo", String.valueOf(authInfo.isSuccess()));
        speechSynthesizer.initTts(TtsMode.MIX);
        doFile();
        initSpeaker();
    }
    private void initSpeaker() {
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_VOLUME, "5");
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEED, "5");
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_PITCH, "5");
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, "0");
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_MIX_MODE, MIX_MODE_DEFAULT);
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_AUDIO_ENCODE, AUDIO_ENCODE_AMR);
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_AUDIO_RATE, AUDIO_BITRATE_AMR_15K85);
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_VOCODER_OPTIM_LEVEL, "0");
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent.getStringExtra("InputText")!=null) {
            String input = intent.getStringExtra("InputText");
            Log.d("test2", input);
            if (speechSynthesizer == null) {
                initSpeechSynthesize();
            }
            speechSynthesizer.speak(input);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        speechSynthesizer.release();
        if(null!=speakerControler)
        unregisterReceiver(speakerControler);
        super.onDestroy();
    }

    private void doFile() {
        //这段代码可以优化，对于父目录只执行一次判断，这样导致多次判断
        parentPath = getParentPath();
        if (parentPath == null) {
            Toast.makeText(getApplicationContext(), "获取储存卡位置失败，请打开应用读取内存卡的权限", Toast.LENGTH_SHORT).show();
        } else {
           if( creatParentDir()) {
               copyFileFromAssetsToSDcard(CN_FEMALE_FILENAME);
               copyFileFromAssetsToSDcard(CN_MALE_FILENAME);
               copyFileFromAssetsToSDcard(CN_TEXT_FILENAME);
               copyFileFromAssetsToSDcard("english" + File.separator + EN_FEMALE_FILENAME);
               copyFileFromAssetsToSDcard("english" + File.separator + EN_MALE_FILENAME);
               copyFileFromAssetsToSDcard("english" + File.separator + EN_TEXT_FILENAME);
           }
        }
    }

    private String getParentPath() {
        String appName = null;
        String parentPath = null;
        appName = "PeanutSpeaker0";
        parentPath = Environment.getExternalStorageDirectory().toString();
        return parentPath + "/" + appName;
    }

    private boolean creatParentDir() {
        File parent = new File(parentPath);
        if (!Environment.getExternalStorageDirectory().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(getApplicationContext(), "SD卡不可用", Toast.LENGTH_SHORT).show();
            return false;
        }else
            return true;
    }

    private void copyFileFromAssetsToSDcard(String filename) {
        File file = new File(parentPath + File.separator + filename);
        if (!file.exists()) {
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                inputStream = getResources().getAssets().open(filename);
                outputStream = new FileOutputStream(parentPath + File.separator + filename);
                byte[] buffer = new byte[1024];
                int temp = 0;
                while ((temp = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, temp);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
    public class Controller extends BroadcastReceiver{
        public Controller(){
            super();
        }
        @Override
        public void onReceive(Context context, Intent intent) {
            if(speechSynthesizer==null){
                initSpeechSynthesize();
            }
            switch (intent.getIntExtra("CONTROLTYPE",0)){
                case MainActivity.TYPE_GENDER:
                    speechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, String.valueOf(intent.getIntExtra("GENDER",0)));
                    break;
                case MainActivity.TYPE_LANGUAGE:
                    break;
                case MainActivity.TYPE_SPEED:
                    speechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEED, String.valueOf(intent.getIntExtra("SPEED",5)));
                    break;
                case MainActivity.TYPE_VOLUME:
                    speechSynthesizer.setParam(SpeechSynthesizer.PARAM_VOLUME, String.valueOf(intent.getIntExtra("VOLUME",5)));
                    break;
            }
        }
    }

}
