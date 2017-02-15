package com.example.settingview;

import java.util.ArrayList;

import com.example.smallweb.ConstantDefine;
import com.example.smallweb.MainActivity;
import com.example.smallweb.SendLoadingSignal;
import com.example.smallweb.R;
import com.example.smallweb.SettingActivity;
import com.example.smallweb.SmallWebView;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SettingFloatingView extends LinearLayout implements
		SendLoadingSignal {

	private final static String TAG = "SetupFloatingView";
	private static SettingFloatingView sSettingView;
	private Context mContext;
	private WindowManager mWindowManager;
	private WindowManager.LayoutParams mLayoutParams;
	private View mView;

	// 触摸屏幕时的view相对于实际屏幕的坐标
	private float mDownSreenX;
	private float mDownSreenY;
	// 触摸屏幕时的view相对于LayoutParams设置的Gravity的坐标
	private int mDownX;
	private int mDownY;

	private ImageView mFloatingImageView;
	private ImageView mSettingImageView;
	private ImageView mShutdownImageView;
	private FrameLayout mNewTabView;
	private ImageView mBackImageView;
	private ImageView mGoImageView;
	private ImageView mUpdateImageView;
	private ImageView mStopImageView;
	private TextView mNewTabTextView;

	private Boolean mHasVisiable = false;
	private Boolean mHasCanBack = false;
	private Boolean mHasCanGo = false;
	private Boolean mHasStop = false;

	private FrameLayout mFrameLayout;
	private ArrayList<SmallWebView> mListSmallWebView;

	// 初始化触摸监听器
	OnTouchListener mOnTouchListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			Log.i("gesture","floatingView onTouch");
			// 实现随手指移动浮窗
			// TODO Auto-generated method stub
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN: {
				// 这个是屏幕为标准的
				mDownSreenX = event.getRawX();
				mDownSreenY = event.getRawY();
				// 这个坐标是以LayoutParams的Gravity参数为标准的
				mDownX = mLayoutParams.x;
				mDownY = mLayoutParams.y;
				break;
			}
			case MotionEvent.ACTION_MOVE: {
				// 得相对坐标差
				int dx = (int) (event.getRawX() - mDownSreenX);
				int dy = (int) (event.getRawY() - mDownSreenY);
				// 根据相对坐标差来进行设置
				mLayoutParams.x = mDownX + dx;
				mLayoutParams.y = mDownY + dy;
				// 设置为没有固定位置，这样才能移动浮窗
				// mLayoutParams.gravity = Gravity.NO_GRAVITY;
				// 注意第一个参数是SettingView.this
				mWindowManager.updateViewLayout(SettingFloatingView.this,
						mLayoutParams);
				break;
			}
			case MotionEvent.ACTION_UP: {
				// if(mDownSreenX - event.getRawX() == 0 && mDownSreenY -
				// event.getRawY() == 0) {
				// switch(v.getId()) {
				// case R.id.settingButton: {
				// Log.i("test","setting");
				// break;
				// }
				// case R.id.newTabButton: {
				// Log.i("test","new");
				// break;
				// }
				// case R.id.updateButton: {
				// Log.i("test","update");
				// break;
				// }
				// case R.id.backButton: {
				// Log.i("test","back");
				// break;
				// }
				// case R.id.goButton: {
				// Log.i("test","go");
				// break;
				// }
				// }
				// }
			}
			}
			// 返回false，触发onLongclick，onClick;返回true，不触发onLongClick，onClick
			return false;
		}
	};

	OnClickListener mOnClickListener = new OnClickListener() {
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.floatingImageView: {
				Log.i("test", "floating");
				if (mHasVisiable) {
					// mSettingImageView.getLeft();
					// int[] a = new int[]{0,0};
					// int[] b = new int[]{0,0};
					// mSettingImageView.getLocationOnScreen(a);
					// mFloatingImageView.getLocationOnScreen(b);
					// Log.i("test",a[0] + " " + a[1]);
					// Log.i("test",b[0] + " " + b[1]);
					// AnimationSet animationClose = new AnimationSet(true);
					// TranslateAnimation firstAnimation = new
					// TranslateAnimation
					// (0, -a[0], 0, 0);
					// //new TranslateAnimation(fromXType, fromXValue, toXType,
					// toXValue, fromYType, fromYValue, toYType, toYValue)
					// RotateAnimation secondAnimation = new RotateAnimation
					// (0, 359,Animation.RELATIVE_TO_SELF,
					// 0.5f,Animation.RELATIVE_TO_SELF , 0.5f);
					// secondAnimation.setRepeatCount(-1);
					// animationClose.addAnimation(firstAnimation);
					// animationClose.addAnimation(secondAnimation);
					// animationClose.setDuration(200);
					// mSettingImageView.setAnimation(animationClose);
					// animationClose.startNow();
					mSettingImageView.setVisibility(GONE);
					mShutdownImageView.setVisibility(GONE);
					// animationClose.setDuration(300);
					// mSettingImageView.setAnimation(animationClose);
					// animationClose.startNow();
					mNewTabView.setVisibility(GONE);

					// animationClose.setDuration(400);
					// mSettingImageView.setAnimation(animationClose);
					// animationClose.startNow();
					mUpdateImageView.setVisibility(GONE);

					// animationClose.setDuration(500);
					// mSettingImageView.setAnimation(animationClose);
					// animationClose.startNow();
					if (mHasCanBack) {
						mBackImageView.setVisibility(GONE);
					}

					// animationClose.setDuration(600);
					// mSettingImageView.setAnimation(animationClose);
					// animationClose.startNow();
					if (mHasCanGo) {
						mGoImageView.setVisibility(GONE);
					}

					// animationClose.setDuration(3000);
					// mSettingImageView.setAnimation(animationClose);
					// animationClose.setAnimationListener(new
					// AnimationListener() {
					//
					// @Override
					// public void onAnimationStart(Animation animation) {
					// // TODO Auto-generated method stub
					// Log.i("test","an");
					// }
					//
					// @Override
					// public void onAnimationRepeat(Animation animation) {
					// // TODO Auto-generated method stub
					//
					// }
					//
					// @Override
					// public void onAnimationEnd(Animation animation) {
					// // TODO Auto-generated method stub
					// mSettingImageView.setVisibility(GONE);
					// }
					// });
					// mSettingImageView.startAnimation(animationClose);
					if (mHasStop) {
						mStopImageView.setVisibility(GONE);
					}
					mHasVisiable = false;
				} else {
					mSettingImageView.setVisibility(VISIBLE);
					mShutdownImageView.setVisibility(VISIBLE);
					mNewTabView.setVisibility(VISIBLE);
					mUpdateImageView.setVisibility(VISIBLE);
					if (mHasCanBack) {
						mBackImageView.setVisibility(VISIBLE);
					}
					if (mHasCanGo) {
						mGoImageView.setVisibility(VISIBLE);
					}
					if (mHasStop) {
						mStopImageView.setVisibility(VISIBLE);
					}
					mHasVisiable = true;
				}
				break;
			}
			case R.id.settingImageView: {
				Log.i("test", "setting");
				Intent intent = new Intent(mContext, SettingActivity.class);
				mContext.startActivity(intent);
				setVisibility(GONE);
				break;
			}
			case R.id.shutdownImageView: {
				Log.i("test", "shutdown");
				((MainActivity) mContext).showCloseDialog();
				break;
			}
			case R.id.newTabView: {
				Log.i("test", "new");
				newTab();
				break;
			}
			case R.id.updateImageView: {
				Log.i("test", "update");
				String url = (String) ((MainActivity) mContext)
						.getSearchTextView().getTag();
				if (url != null) {
					((MainActivity) mContext).webSearch(url);
				}
				break;
			}
			case R.id.backImageView: {
				Log.i("test", "back");
				WebView webView = ((MainActivity) mContext).getWebView();
				if (webView.canGoBack()) {
					webView.goBack();
				}
				break;
			}
			case R.id.goImageView: {
				Log.i("test", "go");
				WebView webView = ((MainActivity) mContext).getWebView();
				if (webView.canGoForward()) {
					webView.goForward();
				}
				break;
			}
			case R.id.stopImageView: {
				Log.i("test", "stop");
				WebView webView = ((MainActivity) mContext).getWebView();
				webView.stopLoading();
				break;
			}
			}
		}
	};

	private SettingFloatingView(Context context) {
		super(context);
		mContext = context;
		init();
	}

	private void init() {
		mWindowManager = ((WindowManager) this.mContext
				.getSystemService(Context.WINDOW_SERVICE));
		initLayoutParams();
		initView();
	}

	private void initLayoutParams() {

		mLayoutParams = new WindowManager.LayoutParams();
		mLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;

		// mLayoutParams.flags =
		// WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
		// mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
		// | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
		// | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
		mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
				| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

		mLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		mLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		// mLayoutParams.gravity = Gravity.LEFT | Gravity.BOTTOM;
		// 设置在屏幕的左下角显示
		mLayoutParams.x = -mWindowManager.getDefaultDisplay().getWidth() / 2;
		mLayoutParams.y = mWindowManager.getDefaultDisplay().getHeight() / 2;
		mLayoutParams.format = PixelFormat.RGBA_8888;
	}

	private void initView() {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		mView = inflater.inflate(R.layout.setting_view, null);
		@SuppressWarnings("deprecation")
		int screenWidth = mWindowManager.getDefaultDisplay().getWidth();
		this.addView(mView, WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT);
		mWindowManager.addView(this, mLayoutParams);

		mFloatingImageView = (ImageView) mView
				.findViewById(R.id.floatingImageView);
		mSettingImageView = (ImageView) mView
				.findViewById(R.id.settingImageView);
		mShutdownImageView = (ImageView) mView
				.findViewById(R.id.shutdownImageView);
		mNewTabView = (FrameLayout) mView.findViewById(R.id.newTabView);
		mBackImageView = (ImageView) mView.findViewById(R.id.backImageView);
		mGoImageView = (ImageView) mView.findViewById(R.id.goImageView);
		mUpdateImageView = (ImageView) mView.findViewById(R.id.updateImageView);
		mStopImageView = (ImageView) mView.findViewById(R.id.stopImageView);
		mNewTabTextView = (TextView) mView.findViewById(R.id.newTabTextView);

		mFloatingImageView.setOnTouchListener(mOnTouchListener);
		mSettingImageView.setOnTouchListener(mOnTouchListener);
		mNewTabView.setOnTouchListener(mOnTouchListener);
		mBackImageView.setOnTouchListener(mOnTouchListener);
		mGoImageView.setOnTouchListener(mOnTouchListener);
		mUpdateImageView.setOnTouchListener(mOnTouchListener);
		mStopImageView.setOnTouchListener(mOnTouchListener);

		mFloatingImageView.setOnClickListener(mOnClickListener);
		mSettingImageView.setOnClickListener(mOnClickListener);
		mShutdownImageView.setOnClickListener(mOnClickListener);
		mNewTabView.setOnClickListener(mOnClickListener);
		mBackImageView.setOnClickListener(mOnClickListener);
		mGoImageView.setOnClickListener(mOnClickListener);
		mUpdateImageView.setOnClickListener(mOnClickListener);
		mStopImageView.setOnClickListener(mOnClickListener);

		mFrameLayout = ((MainActivity) mContext).getFrameLayout();
		mListSmallWebView = ((MainActivity) mContext).getListSmallWebView();
	}

	public static SettingFloatingView getInstance(Context context) {
		if (null == sSettingView) {
			sSettingView = new SettingFloatingView(context);
			return sSettingView;
		} else {
			return sSettingView;
		}
	}

//	@Override
//	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		// switch (event.getAction()) {
		// case MotionEvent.ACTION_DOWN: {
		// //这个是屏幕为标准的
		// mDownSreenX = (int) event.getRawX();
		// mDownSreenY = (int) event.getRawY();
		// //这个坐标是以LayoutParams的Gravity参数为标准的
		// mDownX = mLayoutParams.x;
		// mDownY = mLayoutParams.y;
		// break;
		// }
		// case MotionEvent.ACTION_MOVE: {
		// //得相对坐标差
		// int dx = (int) (event.getRawX() - mDownSreenX);
		// int dy = (int) (event.getRawY() - mDownSreenY);
		// //根据相对坐标差来进行设置
		// mLayoutParams.x = mDownX + dx;
		// mLayoutParams.y = mDownY + dy;
		// mWindowManager.updateViewLayout(this,mLayoutParams);
		// break;
		// }
		// }
		// return true;
//		return false;
//	}

	private void newTab() {
		mListSmallWebView.add(new SmallWebView(mContext));
		mFrameLayout.removeAllViews();
		mFrameLayout.addView(mListSmallWebView
				.get(mListSmallWebView.size() - 1).getWebView());
		mListSmallWebView.get(mListSmallWebView.size() - 1).getWebView()
				.requestFocus();
		// 加载主页
		((MainActivity) mContext).webSearch(ConstantDefine.HOME);
		// 更新TextView显示的标签个数
		mNewTabTextView.setText(mListSmallWebView.size() + "");
		// 设置最后一个标签高亮，因为新建的标签是在最后的
		((MainActivity) mContext).setGrayId(mListSmallWebView.size() - 1);
	}

	public void newTab(String url) {
		mListSmallWebView.add(new SmallWebView(mContext));
		mFrameLayout.removeAllViews();
		mFrameLayout.addView(mListSmallWebView
				.get(mListSmallWebView.size() - 1).getWebView());
		mListSmallWebView.get(mListSmallWebView.size() - 1).getWebView()
				.requestFocus();
		// 加载主页
		((MainActivity) mContext).webSearch(url);
		// 更新TextView显示的标签个数
		mNewTabTextView.setText(mListSmallWebView.size() + "");
		// 设置最后一个标签高亮，因为新建的标签是在最后的
		((MainActivity) mContext).setGrayId(mListSmallWebView.size() - 1);
	}

	@Override
	public void onCanBackOrGo(Boolean canBack, Boolean canGo) {
		// TODO Auto-generated method stub
		// 处理回退按键
		if (canBack != null) {
			if (canBack) {
				// 可以回退时显示回退按键
				if (mHasVisiable) {
					mBackImageView.setVisibility(VISIBLE);
				}
				mHasCanBack = true;
			} else {
				// 不可以回退时不显示回退按键
				if (mHasVisiable) {
					mBackImageView.setVisibility(GONE);
				}
				mHasCanBack = false;
			}
			return;
		}

		// 处理前进按键
		if (canGo != null) {
			if (canGo) {
				// 可以回退时显示回退按键
				if (mHasVisiable) {
					mGoImageView.setVisibility(VISIBLE);
				}
				mHasCanGo = true;
			} else {
				// 不可以回退时不显示回退按键
				if (mHasVisiable) {
					mGoImageView.setVisibility(GONE);
				}
				mHasCanGo = false;
			}
			return;
		}
	}

	@Override
	public void onStartLoading(Boolean yes) {
		// TODO Auto-generated method stub
		if (yes) {
			mHasStop = true;
			if (mHasVisiable) {
				mStopImageView.setVisibility(VISIBLE);
			}
		} else {
			mHasStop = false;
			if (mHasVisiable) {
				mStopImageView.setVisibility(GONE);
			}
		}
	}

	public void setNewTabTextView(String text) {
		mNewTabTextView.setText(text);
	}

	public void show() {
		setVisibility(VISIBLE);
	}

	public void hide() {
		setVisibility(GONE);
	}

	public void destroy() {
		sSettingView = null;
	}

	// 定位浮窗位置
	public void locateFloatingViewLayoutInCenter() {
		// 没有定义gravity时，原点的默认位置是在中心的
		mLayoutParams.x = -mWindowManager.getDefaultDisplay().getWidth() / 2;
		// 比原点高一点，不至于遮挡弹出来的软输入法键盘
		mLayoutParams.y = -mWindowManager.getDefaultDisplay().getHeight() / 15;
		mWindowManager.updateViewLayout(this, mLayoutParams);
	}

	// 恢复浮窗原来的位置
	public void recoverFloatingViewLayoutLocation() {
		// 左下角
		mLayoutParams.x = -mWindowManager.getDefaultDisplay().getWidth() / 2;
		mLayoutParams.y = mWindowManager.getDefaultDisplay().getHeight() / 2;
		mWindowManager.updateViewLayout(this, mLayoutParams);
	}

}