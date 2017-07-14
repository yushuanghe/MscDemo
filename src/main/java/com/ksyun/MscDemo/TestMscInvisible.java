package com.ksyun.MscDemo;

import com.iflytek.cloud.speech.*;
import com.ksyun.MscDemo.util.DebugLog;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * 音频流测试
 * Created by yushuanghe on 2017/07/14.
 */
public class TestMscInvisible {
    private static boolean mIsEndOfSpeech = false;

    private static StringBuffer mResult = new StringBuffer();

    public static void main(String[] args) {
        /**
         * 1、初始化
         */
        // 创建用户语音配置对象后才可以使用语音服务
        SpeechUtility.createUtility(SpeechConstant.APPID + "= 5965ef97 ");

        /**
         * 2、音频流听写
         */
        // 1、创建SpeehRecognizer对象
        SpeechRecognizer recognizer = SpeechRecognizer.createRecognizer();

        recognizer.setParameter(SpeechConstant.AUDIO_SOURCE, "-1");
        //写音频流时，文件是应用层已有的，不必再保存
        //		recognizer.setParameter(SpeechConstant.ASR_AUDIO_PATH,
        //				"./iat_test.pcm");
        recognizer.setParameter(SpeechConstant.RESULT_TYPE, "plain");
        recognizer.setParameter(SpeechConstant.DOMAIN, "iat");
        recognizer.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        recognizer.setParameter(SpeechConstant.ACCENT, "mandarin ");
        recognizer.setParameter(SpeechConstant.AUDIO_SOURCE, "-1");

        recognizer.startListening(recListener);

        FileInputStream fis = null;
        final byte[] buffer = new byte[64 * 1024];
        try {
            fis = new FileInputStream(new File("./test.pcm"));
            //fis = new FileInputStream(new File("./tts_test.pcm"));
            if (0 == fis.available()) {
                mResult.append("no audio avaible!");
                recognizer.cancel();
            } else {
                int lenRead = buffer.length;
                while (buffer.length == lenRead && !mIsEndOfSpeech) {
                    lenRead = fis.read(buffer);
                    recognizer.writeAudio(buffer, 0, lenRead);
                }//end of while

                recognizer.stopListening();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != fis) {
                    fis.close();
                    fis = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }//end of try-catch-finally
    }

    /**
     * 听写监听器
     */
    private static RecognizerListener recListener = new RecognizerListener() {

        public void onBeginOfSpeech() {
            DebugLog.Log("onBeginOfSpeech enter");
            DebugLog.Log("*************开始录音*************");
        }

        public void onEndOfSpeech() {
            DebugLog.Log("onEndOfSpeech enter");
            mIsEndOfSpeech = true;
        }

        public void onVolumeChanged(int volume) {
            DebugLog.Log("onVolumeChanged enter");
            if (volume > 0)
                DebugLog.Log("*************音量值:" + volume + "*************");

        }

        public void onResult(RecognizerResult result, boolean islast) {
            DebugLog.Log("onResult enter");
            mResult.append(result.getResultString());

            if (islast) {
                DebugLog.Log("识别结果为:" + mResult.toString());
                mIsEndOfSpeech = true;
                mResult.delete(0, mResult.length());
            }
        }

        public void onError(SpeechError error) {
            mIsEndOfSpeech = true;
            DebugLog.Log("*************" + error.getErrorCode()
                    + "*************");
        }

        public void onEvent(int eventType, int arg1, int agr2, String msg) {
            DebugLog.Log("onEvent enter");
        }

    };
}
