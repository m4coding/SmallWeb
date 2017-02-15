package com.example.searchhint;

import com.example.smallweb.ConstantDefine;
import com.example.smallweb.MainActivity;
import com.example.smallweb.R;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.style.SuperscriptSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.PopupWindow;

public class CustomizedPopupWindow extends PopupWindow {

	private Context mContext;
	private ImageView mBaiduView;
	private ImageView mGoogleView;
	private ImageView mBingView;
	private View mView;

	public CustomizedPopupWindow(Context context) {
		// super(context);
		super(context);
		mContext = context;
		initView();
	}

	@SuppressWarnings("deprecation")
	private void initView() {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		mView = inflater.inflate(R.layout.search_dialog, null);

		mBaiduView = (ImageView) mView.findViewById(R.id.baidu);
		mGoogleView = (ImageView) mView.findViewById(R.id.google);
		mBingView = (ImageView) mView.findViewById(R.id.bing);

		mBaiduView.setOnClickListener(mOnClickListener);
		mGoogleView.setOnClickListener(mOnClickListener);
		mBingView.setOnClickListener(mOnClickListener);

		setContentView(mView);
		setWidth(LayoutParams.WRAP_CONTENT);
		setHeight(LayoutParams.WRAP_CONTENT);
		setFocusable(true);
		// 设置各种形状的PopupWindow，这句很重要。设置PopupWindow没有背景，这样就可以完全是mView中的视图，而不干扰其中的背景设置
		setBackgroundDrawable(new BitmapDrawable());
		setTouchInterceptor(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
					dismiss();
					return true;
				}
				return false;
			}
		});

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