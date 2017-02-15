package com.example.smallweb;

import android.os.Environment;

public class ConstantDefine {
	private ConstantDefine() {

	}

	public static final String BAIDU_SEARCH = "http://www.baidu.com/s?wd=";
	public static final String GOOGLE_SEARCH = "https://www.google.com/search?client=lightning&ie=UTF-8&oe=UTF-8&q=";
	public static final String BING_SEARCH = "http://www.bing.com/search?q=";

	public static final String HTTP = "http://";
	public static final String HTTPS = "htpps://";
	public static final String FTP = "ftp://";
	public static final String FILE = "file://";
	public static final String FOLDER = "folder://";

	public static final String EXTERNAL_STORAGE = Environment
			.getExternalStorageDirectory().toString();

	public static final String TAG = "smallweb";
	// 默认的主页是hao123
	//public static String HOME = "http://m.hao123.com";
	public static String HOME = "http://h5.mse.360.cn/navi.html";
	public static final String BAIDU_HOME = "http://m.hao123.com";
	public static final String _360_HOME = "http://h5.mse.360.cn/navi.html";
	public static final String SOGOU_HOME = "http://m.123.sogou.com";

}