package com.example.smallweb;

import com.example.searchhint.CustomizedPopupWindow;
import com.example.searchhint.HistoryDatabase;
import com.example.searchhint.SearchAdapter;
import com.example.searchhint.SearchDialog;
import com.example.settingview.SettingFloatingView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView.OnEditorActionListener;

public class MainActivity extends Activity {

	private final static String TAG = MainActivity.class.getSimpleName();

	private ActionBar mActionBar;
	private AutoCompleteTextView mSearchTextView;
	private SearchAdapter mSearchAdapter;
	// private WebView mWebView;
	private ArrayList<SmallWebView> mListSmallWebView;
	private ProgressBar mProgressBar;
	// private String mHomeString = ConstantDefine.HOME;
	private HistoryDatabase mHistoryDatabase;
	private ImageView mSearchView;
	private ImageView mArrowView;
	private RelativeLayout mNewTabView;
	private DrawerLayout mDrawer;
	private LinearLayout mRightDrawer;
	// 用于存放WebView
	private FrameLayout mFrameLayout;
	// private SearchDialog mSearchDialog;
	private CustomizedPopupWindow mCustomizedPopupWindow;
	// 先默认以百度搜索来搜索内容
	private String mSearchEngine = ConstantDefine.BAIDU_SEARCH;

	// 存放标签textView的临时LinearLayout
	private LinearLayout mTemLinearLayout;
	// 全局记录，用于记录哪个标签是高亮的
	private int mGrayId = 0;

	// SharePrefences操作
	private SharedPreferences mSharePreferences;
	private SharedPreferences.Editor mEidtor;

	LayoutInflater mInflater;

	private LinearLayout mMainRootLayout;

	private int mFirstBottomHeight = -1;

	private GestureDetector mGestureDetector;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// 初始配置文件
		initSettingSave();
		// 读取配置文件的值
		obtainSettingSave();
		// 初始化View
		initView();
		// 将自身保存到MyApplication中
		((MyApplication) getApplication()).setMainActivity(MainActivity.this);

		//android23  申请悬浮窗权限
		if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M) {
			if (!Settings.canDrawOverlays(this)) {
				Log.i(TAG, "to request overlays");
				requestAlertWindowPermission();
			}
		}
	}

	private  void requestAlertWindowPermission() {
		if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M) {
			Toast.makeText(this, getString(R.string.please_open_floating_permission),Toast.LENGTH_LONG).show();
			Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
			intent.setData(Uri.parse("package:" + getPackageName()));
			startActivityForResult(intent, 1);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M) {
			if (requestCode == 1) {
				if (Settings.canDrawOverlays(this)) {
					Log.i(TAG, "onActivityResult overlay success");
				}
			}
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		SettingFloatingView.getInstance(this).show();
	}

	@Override
	protected void onPause() {
		super.onPause();

		SettingFloatingView.getInstance(this).hide();
	}


	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mHistoryDatabase != null) {
			if (mHistoryDatabase.isOpen()) {
				mHistoryDatabase.close();
			}
		}
		SettingFloatingView.getInstance(this).destroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = new MenuInflater(this);
		inflater.inflate(R.menu.menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {

		case R.id.searchBox:
			// 隐藏软输入法
			hideInputMethod();
			webSearch(mSearchTextView.getText().toString());

			return true;

		case R.id.bookMarks:
			// 添加书签
			addBookMark();
			return true;

		case R.id.home:
			// 加载主页
			webSearch(ConstantDefine.HOME);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		WebView webView = (WebView) mFrameLayout.getChildAt(0);
		// 监听返回键，使WebView中的网页可退，而不是直接退出Activity
		if (KeyEvent.KEYCODE_BACK == event.getKeyCode() && webView.canGoBack()) {
			webView.goBack();
			return true;
			// 当不可回退的时候
		} else if (KeyEvent.KEYCODE_BACK == event.getKeyCode()) {
			showCloseDialog();
			return true;
		}
		// SettingFloatingView.getInstance(this).hide();
		return super.onKeyDown(keyCode, event);
	}

//	@Override
//	public boolean onTouchEvent(MotionEvent event) {
//		// TODO Auto-generated method stub
//		Log.i("gesture","onTouch");
////		return mGestureDetector.onTouchEvent(event);
//		return super.onTouchEvent(event);
//	}

	@SuppressLint({ "NewApi", "SetJavaScriptEnabled" })
	private void initView() {
		setContentView(R.layout.main_activity);

		mMainRootLayout = (LinearLayout) findViewById(R.id.mainRootLayout);

		mActionBar = getActionBar();
		// 不显示标题
		mActionBar.setDisplayShowTitleEnabled(false);
		// 不显示应用程序的图标
		mActionBar.setDisplayShowHomeEnabled(false);
		// 显示定制的view
		mActionBar.setDisplayShowCustomEnabled(true);
		mActionBar.setCustomView(R.layout.search);

		mProgressBar = (ProgressBar) findViewById(R.id.processBar);

		mInflater = LayoutInflater.from(this);
		mTemLinearLayout = (LinearLayout) mInflater.inflate(
				R.layout.tab_container, null);

		// 初始化历史数据库
		initDatabase(); // 这句不能放在smallWebView初始化之后，因为它的初始化要用到的数据库

		// 初始化搜索框
		initSearchBox();

		// 初始化存放SmallWebView的列表
		mListSmallWebView = new ArrayList<SmallWebView>();

		// 先创建一个SmallView使用
		mListSmallWebView.add(new SmallWebView(this));
		mFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
		mFrameLayout.addView(mListSmallWebView.get(0).getWebView());

		mNewTabView = (RelativeLayout) findViewById(R.id.newTab);
		mNewTabView.setOnClickListener(mOnClickListener);
		mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		mRightDrawer = (LinearLayout) findViewById(R.id.right_drawer);
		mDrawer.setDrawerListener(mDrawerListener);

		// 设置浮窗初始化
		initSettingView();

		// mWebView = (WebView) findViewById(R.id.webView);
		// //使能JavaScript
		// mWebView.getSettings().setJavaScriptEnabled(true);
		// //设置WebViewClient，将url的处理权交给WebView处理，而不是默认的浏览器
		// mWebView.setWebViewClient(new SmallWebViewClient(this,mProgressBar,
		// mSearchTextView));
		// mWebView.setWebChromeClient(new SmallWebChromeClient(mProgressBar,
		// mHistoryDatabase));

		// 隐藏软输入法
		hideInputMethod();
		webSearch(ConstantDefine.HOME);

		initKeyBoardListen();
	}

	// 设置浮窗初始化
	private void initSettingView() {
		SettingFloatingView.getInstance(this);
	}

	// 初始化历史数据库
	private void initDatabase() {
		mHistoryDatabase = new HistoryDatabase(this);
	}

	// 初始化搜索框
	private void initSearchBox() {

		// 初始化搜索图标
		// mSearchDialog = new SearchDialog(this, mActionBar);
		mCustomizedPopupWindow = new CustomizedPopupWindow(this);
		// 设置点击监听，弹出选择搜索的Dialog
		mSearchView = (ImageView) findViewById(R.id.searchView);
		mSearchView.setOnClickListener(mOnClickListener);
		// 根据mSearchEngine的值来设置mSearchView所显示的图标
		if (mSearchEngine.equals(ConstantDefine.BAIDU_SEARCH)) {

			mSearchView.setImageResource(R.drawable.baidu48x48);

		} else if (mSearchEngine.equals(ConstantDefine.GOOGLE_SEARCH)) {

			mSearchView.setImageResource(R.drawable.google48x48);

		} else if (mSearchEngine.equals(ConstantDefine.BING_SEARCH)) {

			mSearchView.setImageResource(R.drawable.bing48x48);
		}

		mArrowView = (ImageView) findViewById(R.id.arrowView);
		mArrowView.setOnClickListener(mOnClickListener);

		mSearchTextView = (AutoCompleteTextView) findViewById(R.id.searchText);

		// 监听enter键
		mSearchTextView.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				switch (keyCode) {
				case KeyEvent.KEYCODE_ENTER:
					// 隐藏软输入法
					hideInputMethod();
					// 进行搜索
					webSearch(mSearchTextView.getText().toString());
					return true;
				default:
					break;
				}
				return false;
			}
		});

		// 用于监听软件输入法enter键就隐藏输入法界面
		mSearchTextView.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				// TODO Auto-generated method stub
				if (actionId == EditorInfo.IME_ACTION_GO
						|| actionId == EditorInfo.IME_ACTION_DONE
						|| actionId == EditorInfo.IME_ACTION_NEXT
						|| actionId == EditorInfo.IME_ACTION_SEND
						|| actionId == EditorInfo.IME_ACTION_SEARCH) {
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(
							mSearchTextView.getWindowToken(), 0);
					webSearch(mSearchTextView.getText().toString());
					mFrameLayout.getChildAt(0).requestFocus();
					return true;
				}
				return false;
			}
		});

		// 初始化搜索框的自动提示功能
		initSearchBoxHint();
	}

	// 初始化搜索框的自动提示功能
	private void initSearchBoxHint() {
		// 设置输入一个字符就提示
		mSearchTextView.setThreshold(1);
		// 设置下拉列表的宽度为match_parent
		mSearchTextView.setDropDownWidth(-1);
		// 设置下拉列表显示时的定点位置(以进度条为基准)，若没有设置此项则以本身为参考来定点
		mSearchTextView.setDropDownAnchor(R.id.processBar);
		// 为下来列表中的每个项设置监听
		mSearchTextView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				try {
					String url;
					url = ((TextView) arg1.findViewById(R.id.url)).getText()
							.toString();
					// R.string.suggestion对应的字符串为Search For
					if (url.startsWith(getString(R.string.suggestion))) {
						url = ((TextView) arg1.findViewById(R.id.title))
								.getText().toString();
					} else {
						mSearchTextView.setText(url);
					}
					webSearch(url);
					hideInputMethod();
				} catch (NullPointerException e) {
					Log.e("Browser Error: ",
							"NullPointerException on item click");
				}
			}

		});

		mSearchTextView.setSelectAllOnFocus(true);
		mSearchAdapter = new SearchAdapter(this, mHistoryDatabase);
		mSearchTextView.setAdapter(mSearchAdapter);

	}

	private void initKeyBoardListen() {

		mMainRootLayout.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {

					@Override
					public void onGlobalLayout() {
						// TODO Auto-generated method stub
						Rect rect = new Rect();
						// 获取键盘View的矩形框，根据此矩形框可以确定位置的距离
						mMainRootLayout.getGlobalVisibleRect(rect);
						if (-1 == mFirstBottomHeight) {
							// rect.bottom为矩形底部距离原点的y坐标
							mFirstBottomHeight = rect.bottom;
						}

						// 当rect.bottom值小于第一次Activity布局时的值时就表示软输入法键盘弹出
						if (rect.bottom < mFirstBottomHeight) {
							SettingFloatingView.getInstance(MainActivity.this)
									.locateFloatingViewLayoutInCenter();
							// 否则软输入法键盘隐藏
						} else {
							SettingFloatingView.getInstance(MainActivity.this)
									.recoverFloatingViewLayoutLocation();
						}
					}
				});
	}

	private void initSettingSave() {
		// 生成SharedPreferences实例 保存在/data/data/包名/shared_prefs
		mSharePreferences = getApplicationContext().getSharedPreferences(
				PreferenceConstants.PREFERENCES, Context.MODE_PRIVATE);
		mEidtor = mSharePreferences.edit();
	}

	private void obtainSettingSave() {
		// 从配置文件中读取home字段的内容
		String home = mSharePreferences.getString("home", "null");
		// 当有home字段的字符串时才进行对ConstantDefine.HOME赋值，否则使用默认值
		if (!home.equals("null")) {
			ConstantDefine.HOME = home;
		}

		// 从配置文件中读取engine字段的内容
		String engine = mSharePreferences.getString("engine", "null");
		if (!engine.equals("null")) {
			// 当存在engine字段的字符串时才进行对mSearchEngine赋值，否则使用默认值
			mSearchEngine = engine;
		}
	}

	private void newTab() {
		mListSmallWebView.add(new SmallWebView(this));
		mFrameLayout.removeAllViews();
		mFrameLayout.addView(mListSmallWebView
				.get(mListSmallWebView.size() - 1).getWebView());
		// 加载主页
		webSearch(ConstantDefine.HOME);
		// 更新TextView显示的标签个数
		SettingFloatingView.getInstance(MainActivity.this).setNewTabTextView(
				mListSmallWebView.size() + "");
	}

	// 刷新标签显示，grayId参数指定哪个标签是高亮的
	public void updateRightDrawerShow(int grayId) {
		LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		lp1.setMargins(2, 10, 2, 2);
		mTemLinearLayout.setLayoutParams(lp1);
		// 删除容器中所有的view
		mTemLinearLayout.removeAllViews();

		// 根据打开的标签个数，创建View
		for (int i = 0; i < mListSmallWebView.size(); i++) {
			// 当有网页标题不为null时才更新，避免空指针操作
			if (mListSmallWebView.get(i).getWebView().getTag() != null) {
				LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT,
						LinearLayout.LayoutParams.WRAP_CONTENT);
				lp2.setMargins(2, 5, 2, 2);
				View item = mInflater.inflate(R.layout.tab_item, null);
				item.setLayoutParams(lp2);
				item.setOnClickListener(mOnItemClickListener);
				// 指定与item对应的标号
				item.setTag(i);
				TextView textView = (TextView) item
						.findViewById(R.id.tabItemText);
				ImageView imageView = (ImageView) item
						.findViewById(R.id.tabCancel);
				imageView.setOnClickListener(mOnCancelViewClickListener);
				textView.setText((String) (mListSmallWebView.get(i)
						.getWebView().getTag()));
				mTemLinearLayout.addView(item);
				if (i == grayId) {
					// 全局记录，用于记录哪个标签被高亮
					mGrayId = i;
					item.setBackgroundColor(Color.GRAY);
					mListSmallWebView.get(i).getWebView().requestFocus();
				}
			}
		}
		// 右抽屉中的view个数超过1时才删除
		if (mRightDrawer.getChildCount() > 1) {
			mRightDrawer.removeViewAt(1);
		}
		// 更新实际的父容器才可以刷新里面的组件显示
		// mTemLinearLayout.invalidate();
		mRightDrawer.addView(mTemLinearLayout);
		// mRightDrawer.updateViewLayout(mTemLinearLayout, null);
		// mRightDrawer.invalidate();
		// mDrawer.invalidate();
	}

	// 网页搜索 可以搜索域名、IP地址和内容
	public synchronized void webSearch(String url) {
		if (url.equals("")) {
			return;
		}

		// 去掉字符串两端的空白字符
		url = url.trim();
		// 停止正在加载的网页，搜索时多次加载
		((WebView) mFrameLayout.getChildAt(0)).stopLoading();

		if (url.startsWith("www.")) {
			// 加上http://头
			url = ConstantDefine.HTTP + url;
		} else if (url.startsWith("ftp.")) {
			url = ConstantDefine.FTP + url;
		}

		boolean containsPeriod = url.contains(".");
		// 判断是不是ip地址 这个判断目前有个小Bug，比如我输入192168.那么也会按ip地址类进行搜索，那么此时就会出错
		boolean isIPAddress = (TextUtils.isDigitsOnly(url.replace(".", ""))
				&& (url.replace(".", "").length() >= 4) && url.contains("."));
		Log.i("test", url.replace(".", ""));
		boolean aboutScheme = url.contains("about:");
		// 判断是不是有效的url
		boolean validURL = (url.startsWith(ConstantDefine.FTP)
				|| url.startsWith(ConstantDefine.HTTP)
				|| url.startsWith(ConstantDefine.FILE) || url
					.startsWith(ConstantDefine.HTTPS)) || isIPAddress;
		// 判断是不是搜索内容（包含空格或者不包含点，同时不包含about:
		boolean isSearch = ((url.contains(" ") || !containsPeriod) && !aboutScheme);

		if (isIPAddress
				&& (!url.startsWith(ConstantDefine.HTTP) || !url
						.startsWith(ConstantDefine.HTTPS))) {
			url = ConstantDefine.HTTP + url;
		}

		// 搜索内容
		if (isSearch) {
			try {
				url = URLEncoder.encode(url, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			((WebView) mFrameLayout.getChildAt(0)).loadUrl(mSearchEngine + url);
			// 若不是搜索内容，并且不是有效的url则以默认的http协议来进行搜索
		} else if (!validURL) {
			((WebView) mFrameLayout.getChildAt(0)).loadUrl(ConstantDefine.HTTP
					+ url);
			// 有效的url
		} else {
			((WebView) mFrameLayout.getChildAt(0)).loadUrl(url);
		}
	}

	// 隐藏软输入法，并且让WebView获取到焦点
	private void hideInputMethod() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		// 隐藏软输入法
		imm.hideSoftInputFromWindow(mSearchTextView.getWindowToken(), 0);
		webSearch(mSearchTextView.getText().toString());
		// WebView申请获取焦点
		((WebView) mFrameLayout.getChildAt(0)).requestFocus();
	}

	// 添加书签的方法
	private void addBookMark() {
		String url = ((WebView) mFrameLayout.getChildAt(0)).getUrl();
		String title = ((WebView) mFrameLayout.getChildAt(0)).getTitle();
		if (mHistoryDatabase.addBookMarkItem(url, title) != -1) {
			Toast.makeText(MainActivity.this, "添加书签成功", Toast.LENGTH_SHORT)
					.show();
		}

	}

	private OnClickListener mOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.searchView: {
				// mSearchDialog.show();
				mCustomizedPopupWindow.showAsDropDown(mSearchView);
				break;
			}
			case R.id.arrowView: {
				// mSearchDialog.show();
				mCustomizedPopupWindow.showAsDropDown(mSearchView);
				break;
			}
			case R.id.newTab: {
				newTab();
				mGrayId = mListSmallWebView.size() - 1;
				mDrawer.closeDrawers();
				break;
			}
			default:
				break;
			}
		}
	};

	private OnClickListener mOnItemClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			mFrameLayout.removeAllViews();
			// 点击item后显示被点击item对应的WebView
			updateRightDrawerShow((Integer) v.getTag());
			mFrameLayout.addView(mListSmallWebView.get((Integer) v.getTag())
					.getWebView());
			// 获取焦点
			mListSmallWebView.get((Integer) v.getTag()).getWebView()
					.requestFocus();
			// //将全部item的背景设置成白色
			// for(int i = 0;i < ((LinearLayout)v.getParent()).getChildCount();
			// i++) {
			// ((LinearLayout)v.getParent()).getChildAt(i).setBackgroundColor(Color.WHITE);
			// }
			// //将被选中的item的背景设置成灰色，实现高亮正在显示的标签
			// v.setBackgroundColor(Color.GRAY);
		}

	};

	private OnClickListener mOnCancelViewClickListener = new OnClickListener() {

		@SuppressLint("NewApi")
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int tagId = (Integer) ((LinearLayout) v.getParent()).getTag();
			// 若取消的是正在显示的标签，则关闭正在显示的标签，显示最后一个标签并高亮
			if (mFrameLayout.getChildAt(0).equals(
					mListSmallWebView.get(tagId).getWebView())) {
				Log.i("test", "same");
				// 点击的是高亮标签且最后一个标签，并且只有一个标签时退出程序
				if (mListSmallWebView.size() == 1) {
					Log.i("test", "finish");
					MainActivity.this.finish();
					// 结束函数返回，因为执行finish时，不一定立刻摧毁掉MainAcivity，后面的语句还是有可能会执行的，所以return避免这种情况
					return;
				}
				mListSmallWebView.remove(tagId);
				mFrameLayout.removeAllViews();
				mFrameLayout.addView(mListSmallWebView.get(
						mListSmallWebView.size() - 1).getWebView());
				updateRightDrawerShow(mListSmallWebView.size() - 1);
				// 若取消的是最后一个标签，则关闭正在显示的标签，显示最后一个标签并高亮
			} else if (tagId == mListSmallWebView.size() - 1) {
				Log.i("test", "last");
				mListSmallWebView.remove(tagId);
				updateRightDrawerShow(mListSmallWebView.size() - 1);
				// 若取消的不是最后一个标签和正在显示的标签，则正在显示的标签仍然是高亮的
			} else {
				Log.i("test", "other");
				mListSmallWebView.remove(tagId);
				// 由于取消标签后，List中的编号是会发生变化的，所以注意更新mGrayId
				if (tagId > mGrayId) {
					updateRightDrawerShow(mGrayId);
				} else if (tagId < mGrayId) {
					updateRightDrawerShow(mGrayId - 1);
				}
			}
			// 更新TextView显示的标签个数
			SettingFloatingView.getInstance(MainActivity.this)
					.setNewTabTextView(mListSmallWebView.size() + "");
		}

	};

	DrawerListener mDrawerListener = new DrawerListener() {

		@Override
		public void onDrawerClosed(View arg0) {
			// TODO Auto-generated method stub
			// Log.i("test","close");
		}

		// 抽屉打开时的监听
		@Override
		public void onDrawerOpened(View arg0) {
			// TODO Auto-generated method stub
			// Log.i("test","open");
			if (arg0.getId() == R.id.right_drawer) {
				updateRightDrawerShow(mGrayId);
			}
		}

		@Override
		public void onDrawerSlide(View arg0, float arg1) {
			// TODO Auto-generated method stub
			// Log.i("test","slide");
		}

		@Override
		public void onDrawerStateChanged(int arg0) {
			// TODO Auto-generated method stub
			// Log.i("test","stateChanged");
		}

	};

	/**
	 * 设置搜索引擎
	 */
	public void setSearchEngine(String searchEngine) {
		mSearchEngine = searchEngine;
	}

	/**
	 * 设置搜索引擎显示图标
	 */
	public void setSearchView(int resId) {
		mSearchView.setImageResource(resId);
	}

	/**
	 * 获取搜索框对象
	 */
	public AutoCompleteTextView getSearchTextView() {
		return mSearchTextView;
	}

	/**
	 * 获取WebView对象
	 */
	public WebView getWebView() {
		return ((WebView) mFrameLayout.getChildAt(0));
	}

	public ProgressBar getProgressBar() {
		return mProgressBar;
	}

	public HistoryDatabase getHistoryDatabase() {
		return mHistoryDatabase;
	}

	public FrameLayout getFrameLayout() {
		return mFrameLayout;
	}

	public ArrayList<SmallWebView> getListSmallWebView() {
		return mListSmallWebView;
	}

	public void setGrayId(int grayId) {
		mGrayId = grayId;
	}

	public void updatemHomeString() {

	}

	// 显示提示是否关闭的dialog 由于使用AlertDialog.Builder是无法在系统级浮窗上显示，所以使用系统级别的dialog
	public void showCloseDialog() {
//		 AlertDialog.Builder builder = new
//		 AlertDialog.Builder(MainActivity.this);
//		 builder.setTitle("确定要退出small浏览器？");
//		 builder.setMessage("nihao");
//		 builder.setPositiveButton("确定", null);
//		 builder.show();

		final Dialog dialog = new Dialog(this);
		dialog.getWindow()
				.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		dialog.setTitle("是否退出SmallWeb浏览器");
		LayoutInflater inflater = LayoutInflater.from(this);
		View view = inflater.inflate(R.layout.close_dialog, null);

		view.findViewById(R.id.decideButton).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						dialog.dismiss();
						finish();
						//退出应用
						android.os.Process.killProcess(android.os.Process.myPid());
					}
				});

		view.findViewById(R.id.cancelButton).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				});

		dialog.setContentView(view);
		dialog.show();
	}

}