package com.example.smallweb;

import com.example.settingview.SettingFloatingView;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;

/**
 * 开发过程中发现的问题，由于webView.loadUrl(url)时可能会发生多次的重定向，所以导致
 * shouldOverrideUrlLoading方法和onPageStarted多次被调用，而当网页加载完成后
 * 再调用onPageFinished方法，不过在重载的函数中是需要先使用父类的方法的，如 super.onPageStarted(view, url,
 * favicon); super.onPageFinished(view, url); return
 * super.shouldOverrideUrlLoading(view, url);
 * 
 */
public class SmallWebViewClient extends WebViewClient {

	private ProgressBar mProgressBar;
	private AutoCompleteTextView mAutoCTextView;
	private Context mContext;

	public SmallWebViewClient(ProgressBar progressBar) {
		// TODO Auto-generated constructor stub
		mProgressBar = progressBar;
	}

	public SmallWebViewClient(ProgressBar progressBar, AutoCompleteTextView at) {
		// TODO Auto-generated constructor stub
		mProgressBar = progressBar;
		mAutoCTextView = at;
	}

	public SmallWebViewClient(Context context, ProgressBar progressBar,
			AutoCompleteTextView at) {
		// TODO Auto-generated constructor stub
		mProgressBar = progressBar;
		mAutoCTextView = at;
		mContext = context;
	}

	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		// TODO Auto-generated method stub
		// Log.i("test","shouldWeb " + url);
		return super.shouldOverrideUrlLoading(view, url);
	}

	@Override
	public void onPageStarted(WebView view, String url, Bitmap favicon) {
		// TODO Auto-generated method stub
		// Log.i("page","onPageStarted " + url);
		mProgressBar.setVisibility(android.view.View.VISIBLE);
		if (mAutoCTextView != null) {
			// 记录加载完成的url，用于重新加载的使用
			mAutoCTextView.setTag(url);
		}
		SettingFloatingView.getInstance(mContext).onStartLoading(true);
		super.onPageStarted(view, url, favicon);
	}

	@Override
	public void onPageFinished(WebView view, String url) {
		// TODO Auto-generated method stub
		// Log.i("page","onPageFinished " + url);
		// 设置进度条不可见，也即消失
		mProgressBar.setVisibility(android.view.View.GONE);
		// 加载网页完成后设置搜索框显示网址
		if (mAutoCTextView != null) {
			// Log.i("page","setText " + url);
			mAutoCTextView.setText(url);
		}

		SettingFloatingView.getInstance(mContext).onStartLoading(false);

		if (!((MainActivity) mContext).getWebView().canGoBack()) {
			if (SettingFloatingView.getInstance(mContext) != null) {
				// 不可以回退时回调
				SettingFloatingView.getInstance(mContext).onCanBackOrGo(false,
						null);
			}
		} else {
			if (SettingFloatingView.getInstance(mContext) != null) {
				// 可以回退时回调
				SettingFloatingView.getInstance(mContext).onCanBackOrGo(true,
						null);
			}
		}

		if (!((MainActivity) mContext).getWebView().canGoForward()) {
			if (SettingFloatingView.getInstance(mContext) != null) {
				// 不可以前进时回调
				SettingFloatingView.getInstance(mContext).onCanBackOrGo(null,
						false);
			}
		} else {
			if (SettingFloatingView.getInstance(mContext) != null) {
				// 可以前进时回调
				SettingFloatingView.getInstance(mContext).onCanBackOrGo(null,
						true);
			}
		}

		super.onPageFinished(view, url);
	}
}