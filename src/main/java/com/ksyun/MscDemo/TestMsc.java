package com.ksyun.MscDemo;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.iflytek.cloud.speech.RecognizerListener;
import com.iflytek.cloud.speech.RecognizerResult;
import com.iflytek.cloud.speech.ResourceUtil;
import com.iflytek.cloud.speech.SpeechConstant;
import com.iflytek.cloud.speech.SpeechError;
import com.iflytek.cloud.speech.SpeechRecognizer;
import com.iflytek.cloud.speech.SpeechUtility;
import com.ksyun.MscDemo.util.DebugLog;
import com.ksyun.MscDemo.util.JsonParser;

public class TestMsc {

	private static Map<String, String> mParamMap = new HashMap<String, String>();

	private static StringBuilder sb = new StringBuilder("");

	/**
	 * 听写监听器
	 */
	private static RecognizerListener recognizerListener = new RecognizerListener() {
		/**
		 * 听写结果回调接口(返回Json格式结果，用户可参见附录)；
		 * 一般情况下会通过onResults接口多次返回结果，完整的识别内容是多次结果的累加；
		 * 关于解析Json的代码可参见MscDemo中JsonParser类；</br>
		 * isLast等于true时会话结束。
		 */

		public void onBeginOfSpeech() {
			DebugLog.Log("onBeginOfSpeech enter");
			System.out.println("听写中。。。");
		}

		public void onEndOfSpeech() {
			DebugLog.Log("onEndOfSpeech enter");
		}

		/**
		 * 获取听写结果. 获取RecognizerResult类型的识别结果，并对结果进行累加，显示到Area里
		 */
		public void onResult(RecognizerResult results, boolean islast) {
			DebugLog.Log("onResult enter");

			// 如果要解析json结果，请考本项目示例的 com.iflytek.util.JsonParser类
			System.err.println(results.getResultString());
			String result = JsonParser.parseIatResult(results.getResultString());

			if (null != result) {
				// DebugLog.Log("onResult new font size=" + fontSize);
				sb.append(result);
			}

			if (islast) {
				System.out.println(sb);
			}
		}

		public void onVolumeChanged(int volume) {
			DebugLog.Log("onVolumeChanged enter");
			if (volume == 0)
				volume = 1;
			else if (volume >= 6)
				volume = 6;
		}

		public void onError(SpeechError error) {
			DebugLog.Log("onError enter");
			if (null != error) {
				DebugLog.Log("onError Code：" + error.getErrorCode());
			}
		}

		public void onEvent(int eventType, int arg1, int agr2, String msg) {
			DebugLog.Log("onEvent enter");
		}
	};

	public static void main(String[] args) {
		/**
		 * 1、初始化
		 */
		// 创建用户语音配置对象后才可以使用语音服务
		SpeechUtility.createUtility(SpeechConstant.APPID + "= 5965ef97 ");

		/**
		 * 2、语音听写
		 */
		// 1、创建SpeehRecognizer对象
		SpeechRecognizer mIat = SpeechRecognizer.createRecognizer();

		// 2、设置听写参数，详见《MSC Reference Manual》SpeechConstant类
		mIat.setParameter(SpeechConstant.DOMAIN, "iat");
		mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
		mIat.setParameter(SpeechConstant.ACCENT, "mandarin");

		initParamMap();
		for (Entry<String, String> entry : mParamMap.entrySet()) {
			mIat.setParameter(entry.getKey(), entry.getValue());
		}

		// 本地识别时设置资源，并启动引擎
		final String engType = mParamMap.get(SpeechConstant.ENGINE_TYPE);

		if (SpeechConstant.TYPE_LOCAL.equals(engType)) {
			// 启动合成引擎
			mIat.setParameter(ResourceUtil.ENGINE_START, SpeechConstant.ENG_ASR);

			// 设置资源路径
			final String rate = mParamMap.get(SpeechConstant.SAMPLE_RATE);
			final String tag = rate.equals("16000") ? "16k" : "8k";
			String curPath = System.getProperty("user.dir");
			DebugLog.Log("Current path=" + curPath);
			String resPath = ResourceUtil.generateResourcePath(curPath + "/asr/common.jet") + ";"
					+ ResourceUtil.generateResourcePath(curPath + "/asr/src_" + tag + ".jet");
			System.out.println("resPath=" + resPath);
			mIat.setParameter(ResourceUtil.ASR_RES_PATH, resPath);
		} // end of if is TYPE_LOCAL

		// 3、开始听写
		mIat.startListening(recognizerListener);
	}

	private static class DefaultValue {
		public static final String ENG_TYPE = SpeechConstant.TYPE_CLOUD;
		public static final String SPEECH_TIMEOUT = "60000";
		public static final String NET_TIMEOUT = "20000";
		public static final String LANGUAGE = "zh_cn";

		public static final String ACCENT = "mandarin";
		public static final String DOMAIN = "iat";
		public static final String VAD_BOS = "5000";
		public static final String VAD_EOS = "1800";

		public static final String RATE = "16000";
		public static final String NBEST = "1";
		public static final String WBEST = "1";
		public static final String PTT = "1";

		public static final String RESULT_TYPE = "json";
		public static final String SAVE = "0";
	}

	private static void initParamMap() {
		mParamMap.put(SpeechConstant.ENGINE_TYPE, DefaultValue.ENG_TYPE);
		mParamMap.put(SpeechConstant.SAMPLE_RATE, DefaultValue.RATE);
		mParamMap.put(SpeechConstant.NET_TIMEOUT, DefaultValue.NET_TIMEOUT);
		mParamMap.put(SpeechConstant.KEY_SPEECH_TIMEOUT, DefaultValue.SPEECH_TIMEOUT);

		mParamMap.put(SpeechConstant.LANGUAGE, DefaultValue.LANGUAGE);
		mParamMap.put(SpeechConstant.ACCENT, DefaultValue.ACCENT);
		mParamMap.put(SpeechConstant.DOMAIN, DefaultValue.DOMAIN);
		mParamMap.put(SpeechConstant.VAD_BOS, DefaultValue.VAD_BOS);

		mParamMap.put(SpeechConstant.VAD_EOS, DefaultValue.VAD_EOS);
		mParamMap.put(SpeechConstant.ASR_NBEST, DefaultValue.NBEST);
		mParamMap.put(SpeechConstant.ASR_WBEST, DefaultValue.WBEST);
		mParamMap.put(SpeechConstant.ASR_PTT, DefaultValue.PTT);

		mParamMap.put(SpeechConstant.RESULT_TYPE, DefaultValue.RESULT_TYPE);
		mParamMap.put(SpeechConstant.ASR_AUDIO_PATH, null);
	}
}
