package com.example.smallweb;

import com.example.searchhint.HistoryDatabase;

import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ProgressBar;

public class SmallWebChromeClient extends WebChromeClient {

	private ProgressBar mProgressBar;
	private HistoryDatabase mHistoryDatabase;

	public SmallWebChromeClient(ProgressBar progressBar) {
		// TODO Auto-generated constructor stub
		mProgressBar = progressBar;
	}

	public SmallWebChromeClient(ProgressBar progressBar, HistoryDatabase hd) {
		mProgressBar = progressBar;
		mHistoryDatabase = hd;
	}

	@Override
	public void onProgressChanged(WebView view, int newProgress) {
		// TODO Auto-generated method stub
		super.onProgressChanged(view, newProgress);
		mProgressBar.setProgress(newProgress);
	}

	@Override
	public void onReceivedTitle(WebView view, String title) {
		// TODO Auto-generated method stub
		super.onReceivedTitle(view, title);
		// 每次获取到标题后就将条目加入历史数据库中，形成历史记录
		if (mHistoryDatabase != null) {
			mHistoryDatabase.addHistoryItem(view.getUrl(), title);
		}
		// 给每个加载的WebView都带上标题
		view.setTag(title);
	}
}