package com.ksyun.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 识别结果实体类
 * 
 * @author yushuanghe
 *
 */
public class MscResult implements Serializable {

	private static final long serialVersionUID = 5181278531859381500L;

	// 第几句
	private int sn;
	// 是否最后一句
	private boolean ls;
	// 开始
	private int bg;
	// 结束
	private int ed;
	// 词
	private ArrayList<Words> ws;

	static class Words {
		// 开始
		private int bg;
		// 中文分词
		private ArrayList<ChineseWord> cw;

		static class ChineseWord {
			// 单字
			private String w;
			// 分数
			private int sc;
		}
	}
}
