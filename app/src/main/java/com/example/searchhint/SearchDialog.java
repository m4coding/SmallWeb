package com.example.searchhint;

import com.example.smallweb.ConstantDefine;
import com.example.smallweb.MainActivity;
import com.example.smallweb.R;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Dialog;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

public class SearchDialog extends Dialog {

	private Context mContext;
	private ActionBar mActionBar;
	private ImageView mBaiduView;
	private ImageView mGoogleView;
	private ImageView mBingView;

	public SearchDialog(Context context) {
		// TODO Auto-generated constructor stub
		super(context, R.style.DialogTheme);
		mContext = context;
	}

	public SearchDialog(Context context, ActionBar ab) {
		// TODO Auto-generated constructor stub
		// 设置dialog的主题
		super(context, R.style.DialogTheme);
		mContext = context;
		mActionBar = ab;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initView();
	}

	@SuppressLint("NewApi")
	private void initView() {

		setContentView(R.layout.search_dialog);
		mBaiduView = (ImageView) findViewById(R.id.baidu);
		mGoogleView = (ImageView) findViewById(R.id.google);
		mBingView = (ImageView) findViewById(R.id.bing);

		WindowManager.LayoutParams lp = getWindow().getAttributes();
		// 根据ActionBar的高度来设置Dialog的显示位置 x y 分别为相对于gravity的偏移位置
		if (mActionBar != null) {
			lp.y = mActionBar.getHeight();
		} else {
			lp.y = 65;
		}

		lp.x = 0;
		// 设置透明度
		lp.alpha = 0.8f;
		// 宽度与高度都是与内容匹配的
		lp.width = LayoutParams.WRAP_CONTENT;
		lp.height = LayoutParams.WRAP_CONTENT;
		// 顶部与左边显示
		lp.gravity = Gravity.TOP | Gravity.LEFT;
		// 设置属性
		getWindow().setAttributes(lp);

		initViewClick();
	}

	private void initViewClick() {
		mBaiduView.setOnClickListener(mOnClickListener);
		mGoogleView.setOnClickListener(mOnClickListener);
		mBingView.setOnClickListener(mOnClickListener);
	}

	// 加上android.view.View，表明使用的是View的点击监听器，而不是dialog的点击监听器
	private android.view.View.OnClickListener mOnClickListener = new android.view.View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			// 根据点击的情况更换搜索引擎和搜索引擎图标
			switch (v.getId()) {
			case R.id.baidu: {
				((MainActivity) mContext)
						.setSearchEngine(ConstantDefine.BAIDU_SEARCH);
				((MainActivity) mContext).setSearchView(R.drawable.baidu48x48);
				dismiss();
				break;
			}
			case R.id.google: {
				((MainActivity) mContext)
						.setSearchEngine(ConstantDefine.GOOGLE_SEARCH);
				((MainActivity) mContext).setSearchView(R.drawable.google48x48);
				dismiss();
				break;
			}
			case R.id.bing: {
				((MainActivity) mContext)
						.setSearchEngine(ConstantDefine.BING_SEARCH);
				((MainActivity) mContext).setSearchView(R.drawable.bing48x48);
				dismiss();
				break;
			}
			}
		}
	};
}