package com.example.smallweb;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.GestureDetector.OnGestureListener;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.WindowManager;

public class GestureHandler {

	public static final int GESTURE_UP = 0;
	public static final int GESTURE_DOWN = 1;
	public static final int GESTURE_LEFT = 2;
	public static final int GESTURE_RIGHT = 3;

	private Context mContext;
	private OnGestureHandleResult mOnGestureHandle;

	public int mScreenWidthPixels;
	public int mScreenHeightPixels;

	public GestureHandler(Context context, OnGestureHandleResult listener) {
		mContext = context;
		mOnGestureHandle = listener;

		DisplayMetrics dm = new DisplayMetrics();
		WindowManager windowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		windowManager.getDefaultDisplay().getMetrics(dm);
		mScreenWidthPixels = dm.widthPixels;
		mScreenHeightPixels = dm.heightPixels;
	}

	public GestureDetector getGestureDetector() {
		return new GestureDetector(mContext, mOnGestureListener);
	}

	private OnGestureListener mOnGestureListener = new OnGestureListener() {

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			// TODO Auto-generated method stub
			float x = e2.getX() - e1.getX();
			float y = e2.getY() - e1.getY();
			// 限制必须得划过屏幕的1/3才能算划过    
			float x_limit = mScreenWidthPixels / 3;
			float y_limit = mScreenHeightPixels / 3;
			float x_abs = Math.abs(x);
			float y_abs = Math.abs(y);
			if (x_abs >= y_abs) {
				// gesture left or right
				if (x > x_limit || x < -x_limit) {
					if (x > 0) {
						// right
						doResult(GESTURE_RIGHT);
					} else if (x <= 0) {
						// left
						doResult(GESTURE_LEFT);
					}
				}
			} else {
				// gesture down or up  //不处理向上或向下滑
//				if (y > y_limit || y < -y_limit) {
//					if (y > 0) {
//						// down
//						doResult(GESTURE_DOWN);
//					} else if (y <= 0) {
//						// up
//						doResult(GESTURE_UP);
//					}
//				}
			}
			
			//不能返回true，否则将不能将滑屏事件传递下去，因为有些网页是有可以滑动的图片显示的，这时是需要滑屏事件的
			return false;
		}

		@Override
		public boolean onDown(MotionEvent e) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void onShowPress(MotionEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void onLongPress(MotionEvent e) {
			// TODO Auto-generated method stub

		}

	};

	private void doResult(int result) {
		if (mOnGestureHandle != null) {
			mOnGestureHandle.onHandle(result);
		}
	}

	public interface OnGestureHandleResult {
		public void onHandle(int direction);
	}
}