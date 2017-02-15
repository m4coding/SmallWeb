package com.example.smallweb;

import com.example.searchhint.HistoryDatabase;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.webkit.WebView;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;

public class SmallWebView {

	private Context mContext;
	private WebView mWebView;
	private ProgressBar mProgressBar;
	private AutoCompleteTextView mSearchTextView;
	private HistoryDatabase mHistoryDatabase;
	private GestureDetector mGestureDetector;

	public SmallWebView(Context context) {
		mContext = context;
		MainActivity mainActiviy = (MainActivity) mContext;
		mProgressBar = mainActiviy.getProgressBar();
		mSearchTextView = mainActiviy.getSearchTextView();
		mHistoryDatabase = mainActiviy.getHistoryDatabase();
		initWebView();
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void initWebView() {
		mWebView = new WebView(mContext);
		// 使能JavaScript
		mWebView.getSettings().setJavaScriptEnabled(true);
		// 设置WebViewClient，将url的处理权交给WebView处理，而不是默认的浏览器
		mWebView.setWebViewClient(new SmallWebViewClient(mContext,
				mProgressBar, mSearchTextView));
		mWebView.setWebChromeClient(new SmallWebChromeClient(mProgressBar,
				mHistoryDatabase));

		// 设置下载监听器，使WebView可以实现点击链接进行下载
		mWebView.setDownloadListener(new SmallWebDownloadListener(
				(MainActivity) mContext));
		
		//给Webview加上手势处理
		initGestureHanlder();
		setWebviewLeftAndRightSlideListener();
	}

	public WebView getWebView() {
		return mWebView;
	}
	
	private void setWebviewLeftAndRightSlideListener() {
		if(mWebView != null) {
			mWebView.setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					// TODO Auto-generated method stub
					return mGestureDetector.onTouchEvent(event);
					//return false;
				}
			});
		}
	}
	
	// 初始化手势滑动处理
		private void initGestureHanlder() {
			mGestureDetector = new GestureHandler(mContext,
					new GestureHandler.OnGestureHandleResult() {

						@Override
						public void onHandle(int direction) {
							// TODO Auto-generated method stub
							switch (direction) {
							// 向右滑
							case GestureHandler.GESTURE_RIGHT: {
								//向右滑时，若Webview可以后退即后退
								if(mWebView.canGoBack()) {
									mWebView.goBack();
								}
							}
								break;
							// 向左滑
							case GestureHandler.GESTURE_LEFT: {
								//向左滑时，若Webview可以前进即前进	
								if(mWebView.canGoForward()) {
									mWebView.goForward();
								}
							}
								break;
							}
						}
					}).getGestureDetector();
		}
}