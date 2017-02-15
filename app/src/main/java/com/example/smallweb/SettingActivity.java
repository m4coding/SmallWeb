package com.example.smallweb;

import com.example.searchhint.HistoryDatabase;
import com.example.settingview.SettingFloatingView;
import com.example.smallweb.BookMarkAdapter.BookMarkHolder;
import com.example.smallweb.HistoryAdapter.HistoryHolder;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SettingActivity extends Activity {

	private static int API = android.os.Build.VERSION.SDK_INT;
	private TextView mHistoryTextView;
	private TextView mTabTextView;
	private TextView mSettingHome;
	private LinearLayout mSettingSearchEngine;
	private LinearLayout mSettingDownloader;
	private TextView mAdvanceSetting;
	private LinearLayout mCurrentVersion;
	private TextView mDisplaytSettingSearchEngine;
	private TextView mdefaultDownloadDirectoryTextView;

	// SharePrefences操作
	private SharedPreferences mSharePreferences;
	private SharedPreferences.Editor mEidtor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initSettingSave();
		initView();
		// 读取配置
		obtainSettingSave();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		finish();
		return super.onOptionsItemSelected(item);
	}

	private void initSettingSave() {
		// 生成SharedPreferences实例 保存在/data/data/包名/shared_prefs
		mSharePreferences = getApplicationContext().getSharedPreferences(
				PreferenceConstants.PREFERENCES, Context.MODE_PRIVATE);
		mEidtor = mSharePreferences.edit();
	}

	private void obtainSettingSave() {
		// 从配置文件中读取engine字段的内容
		String engine = mSharePreferences.getString("engine", "null");
		if (!engine.equals("null")) {
			// 当有engine字段的字符串时才进行对mDisplaytSettingSearchEngine赋值，否则使用默认值
			if (engine.equals(ConstantDefine.BAIDU_SEARCH)) {

				mDisplaytSettingSearchEngine.setText("百度搜索");
			} else if (engine.equals(ConstantDefine.GOOGLE_SEARCH)) {

				mDisplaytSettingSearchEngine.setText("谷歌搜索");
			} else if (engine.equals(ConstantDefine.BING_SEARCH)) {

				mDisplaytSettingSearchEngine.setText("必应搜索");
			}
		}

		// 从配置文件中读取download字段的内容
		String download = mSharePreferences.getString(
				PreferenceConstants.DOWNLOAD_DIRECTORY, "null");
		if (!download.equals("null")) {
			mdefaultDownloadDirectoryTextView
					.setText(ConstantDefine.EXTERNAL_STORAGE + "/" + download);
		} else {
			// 设置默认的下载的目录
			mdefaultDownloadDirectoryTextView
					.setText(ConstantDefine.EXTERNAL_STORAGE + "/"
							+ Environment.DIRECTORY_DOWNLOADS);
		}
	}

	@SuppressLint("NewApi")
	private void initView() {
		setContentView(R.layout.setting_activity);
		ActionBar actionBar = getActionBar();
		// 设置ActionBar的显示
		if (actionBar != null) {
			// 使ActionBar的标题可以像Button那样被点击
			actionBar.setHomeButtonEnabled(true);
			// 设置显示标题栏的左箭头
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setIcon(R.drawable.smallweb256x256);
		}

		mHistoryTextView = (TextView) findViewById(R.id.historyTextView);
		mTabTextView = (TextView) findViewById(R.id.tabTextView);
		mSettingHome = (TextView) findViewById(R.id.settingHome);
		mSettingSearchEngine = (LinearLayout) findViewById(R.id.settingSearchEngine);
		mDisplaytSettingSearchEngine = (TextView) findViewById(R.id.displaytSettingSearchEngine);
		mSettingDownloader = (LinearLayout) findViewById(R.id.settingDownloader);
		mdefaultDownloadDirectoryTextView = (TextView) findViewById(R.id.defaultDownloadDirectoryTextView);
		mAdvanceSetting = (TextView) findViewById(R.id.advanceSetting);
		mCurrentVersion = (LinearLayout) findViewById(R.id.currentVersion);

		mHistoryTextView.setOnClickListener(mOnClickListener);
		mTabTextView.setOnClickListener(mOnClickListener);
		mSettingHome.setOnClickListener(mOnClickListener);
		mSettingSearchEngine.setOnClickListener(mOnClickListener);
		mSettingDownloader.setOnClickListener(mOnClickListener);
		mAdvanceSetting.setOnClickListener(mOnClickListener);
		mCurrentVersion.setOnClickListener(mOnClickListener);

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			// Intent intent = new Intent(SettingActivity.this,
			// MainActivity.class);
			// startActivity(intent);
		}
		return super.onKeyDown(keyCode, event);
	}
	


	// 显示历史记录
	private void showHistoryDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater inflater = LayoutInflater.from(this);
		View view = inflater.inflate(R.layout.history_dialog, null);
		final ListView listView = (ListView) view
				.findViewById(R.id.historyListView);

		final Button manageButton = (Button) view
				.findViewById(R.id.manageHistoryButton);
		final Button deleteButton = (Button) view
				.findViewById(R.id.deleteHistoryButton);

		HistoryAdapter historyAdapter = new HistoryAdapter(this,
				new HistoryDatabase(this));
		final ManageHistoryAdapter manageHistoryAdapter = new ManageHistoryAdapter(
				this, new HistoryDatabase(this), deleteButton);
		// 根据是否存在书签记录，来是否高亮管理按钮
		if (historyAdapter.getHistoryList().size() <= 0) {
			manageButton.setClickable(false);
			manageButton.setTextColor(Color.GRAY);
		} else {
			manageButton.setClickable(true);
			manageButton.setTextColor(Color.BLACK);
			manageButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					// 点击管理按钮后就给listView更换适配器
					listView.setAdapter(manageHistoryAdapter);
					manageButton.setVisibility(View.GONE);
					deleteButton.setVisibility(View.VISIBLE);
				}
			});
		}

		listView.setAdapter(historyAdapter);

		// 点击Item，实现跳转
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				SettingFloatingView.getInstance(getApplication()).newTab(
						((HistoryHolder) view.getTag()).mUrl.getText()
								.toString());
				finish();
			}
		});

		// AlertDialog dialog = builder.setView(view).create();
		// //builder.show();
		// dialog.show();
		// dialog.getWindow().setLayout(300, 200);
		builder.setTitle("历史记录");
		builder.setView(view);
		builder.show();
		// builder.show().getWindow().setLayout(400, 300);

	}

	// 显示书签列表
	private void showBookMarkDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater inflater = LayoutInflater.from(this);
		View view = inflater.inflate(R.layout.bookmark_dialog, null);
		final ListView listView = (ListView) view
				.findViewById(R.id.bookmarkListView);

		final Button manageButton = (Button) view
				.findViewById(R.id.manageBookmarkButton);
		final Button deleteButton = (Button) view
				.findViewById(R.id.deleteBookmarkButton);

		BookMarkAdapter bookmarkAdapter = new BookMarkAdapter(this,
				new HistoryDatabase(this));
		final ManageBookMarkAdapter manageBookMarkAdapter = new ManageBookMarkAdapter(
				this, new HistoryDatabase(this), deleteButton);
		// 根据是否存在书签记录，来是否高亮管理按钮
		if (bookmarkAdapter.getBookMarkList().size() <= 0) {
			manageButton.setClickable(false);
			manageButton.setTextColor(Color.GRAY);
		} else {
			manageButton.setClickable(true);
			manageButton.setTextColor(Color.BLACK);
			manageButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					// 点击管理按钮后就给listView更换适配器
					listView.setAdapter(manageBookMarkAdapter);
					manageButton.setVisibility(View.GONE);
					deleteButton.setVisibility(View.VISIBLE);
				}
			});
		}

		listView.setAdapter(bookmarkAdapter);

		// 点击Item，实现跳转
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				SettingFloatingView.getInstance(getApplication()).newTab(
						((BookMarkHolder) view.getTag()).mUrl.getText()
								.toString());
				finish();
			}
		});

		// AlertDialog dialog = builder.setView(view).create();
		// //builder.show();
		// dialog.show();
		// dialog.getWindow().setLayout(300, 200);
		builder.setTitle("书签列表");
		builder.setView(view);
		builder.show();
		// builder.show().getWindow().setLayout(400, 300);

	}

	// 显示设置主页的对话框
	private void showHomeSettingDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater inflater = LayoutInflater.from(this);
		View view = inflater.inflate(R.layout.home_setting_dialog, null);
		LinearLayout baiduLayout = (LinearLayout) view
				.findViewById(R.id.baiduHome);
		LinearLayout _360Layout = (LinearLayout) view
				.findViewById(R.id._360Home);
		LinearLayout sogouLayout = (LinearLayout) view
				.findViewById(R.id.sogouHome);
		LinearLayout customLayout = (LinearLayout) view
				.findViewById(R.id.customHome);
		builder.setTitle("设置主页");
		builder.setView(view);
		final Dialog dialog = builder.show();// 在不关闭的情况下，连续show()两次是会发生错误的
		OnClickListener onClickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				switch (v.getId()) {
				case R.id.baiduHome: {
					ConstantDefine.HOME = ConstantDefine.BAIDU_HOME;
					mEidtor.putString("home", ConstantDefine.BAIDU_HOME);
					mEidtor.commit();
					dialog.dismiss();
					Toast.makeText(getApplicationContext(), "主页设置成功",
							Toast.LENGTH_SHORT).show();

				}
					break;
				case R.id._360Home: {
					ConstantDefine.HOME = ConstantDefine._360_HOME;
					mEidtor.putString("home", ConstantDefine._360_HOME);
					mEidtor.commit();
					dialog.dismiss();
					Toast.makeText(getApplicationContext(), "主页设置成功",
							Toast.LENGTH_SHORT).show();

				}
					break;
				case R.id.sogouHome: {
					ConstantDefine.HOME = ConstantDefine.SOGOU_HOME;
					mEidtor.putString("home", ConstantDefine.SOGOU_HOME);
					mEidtor.commit();
					dialog.dismiss();
					Toast.makeText(getApplicationContext(), "主页设置成功",
							Toast.LENGTH_SHORT).show();
				}
					break;
				case R.id.customHome: {
					dialog.dismiss();
					AlertDialog.Builder builder = new AlertDialog.Builder(
							SettingActivity.this);
					LayoutInflater inflater = LayoutInflater
							.from(SettingActivity.this);
					View view = inflater.inflate(R.layout.home_setting_custom,
							null);
					builder.setTitle("自定制主页");
					builder.setView(view);
					final Dialog customHomeDialog = builder.show();
					final EditText editText = (EditText) view
							.findViewById(R.id.customHomeEditText);
					// 显示当前的Home主页值
					editText.setText(ConstantDefine.HOME);
					Button okButton = (Button) view
							.findViewById(R.id.customHomeButton);
					okButton.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							// 不为空字符才进行写入操作
							if (!editText.getText().toString().equals("")) {
								mEidtor.putString("home", editText.getText()
										.toString());
								ConstantDefine.HOME = editText.getText()
										.toString();
								mEidtor.commit();
								customHomeDialog.dismiss();
								Toast.makeText(getApplicationContext(),
										"自定制主页成功", Toast.LENGTH_SHORT).show();
							} else {
								Toast.makeText(getApplicationContext(),
										"请输入主页网址", Toast.LENGTH_SHORT).show();
							}
						}
					});
				}
					break;
				}
			}
		};
		baiduLayout.setOnClickListener(onClickListener);
		_360Layout.setOnClickListener(onClickListener);
		sogouLayout.setOnClickListener(onClickListener);
		customLayout.setOnClickListener(onClickListener);
	}

	// 显示设置搜索引擎的对话框
	private void showSearchEngineDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater inflater = LayoutInflater.from(this);
		View view = inflater.inflate(R.layout.engine_setting_dialog, null);
		LinearLayout baiduLayout = (LinearLayout) view
				.findViewById(R.id.baiduEngine);
		LinearLayout googleLayout = (LinearLayout) view
				.findViewById(R.id.googleEngine);
		LinearLayout bingLayout = (LinearLayout) view
				.findViewById(R.id.bingEngine);
		builder.setTitle("设置搜索引擎");
		builder.setView(view);
		final Dialog dialog = builder.show();// 在不关闭的情况下，连续show()两次是会发生错误的
		OnClickListener onClickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				switch (v.getId()) {
				case R.id.baiduEngine: {
					mEidtor.putString("engine", ConstantDefine.BAIDU_SEARCH);
					mEidtor.commit();
					dialog.dismiss();
					// 实时更新显示
					mDisplaytSettingSearchEngine.setText("百度搜索");
					// 实时更新SearchView的图标显示
					((MyApplication) getApplication()).getMainActivity()
							.setSearchView(R.drawable.baidu48x48);
					// 实时更新搜索字符串
					((MyApplication) getApplication()).getMainActivity()
							.setSearchEngine(ConstantDefine.BAIDU_SEARCH);
					Toast.makeText(getApplicationContext(), "搜索引擎设置成功",
							Toast.LENGTH_SHORT).show();

				}
					break;
				case R.id.googleEngine: {
					mEidtor.putString("engine", ConstantDefine.GOOGLE_SEARCH);
					mEidtor.commit();
					dialog.dismiss();
					// 实时更新显示
					mDisplaytSettingSearchEngine.setText("谷歌搜索");
					// 实时更新SearchView的图标显示
					((MyApplication) getApplication()).getMainActivity()
							.setSearchView(R.drawable.google48x48);
					// 实时更新搜索字符串
					((MyApplication) getApplication()).getMainActivity()
							.setSearchEngine(ConstantDefine.GOOGLE_SEARCH);
					Toast.makeText(getApplicationContext(), "搜索引擎设置成功",
							Toast.LENGTH_SHORT).show();

				}
					break;
				case R.id.bingEngine: {
					mEidtor.putString("engine", ConstantDefine.BING_SEARCH);
					mEidtor.commit();
					dialog.dismiss();
					// 实时更新显示
					mDisplaytSettingSearchEngine.setText("必应搜索");
					// 实时更新SearchView的图标显示
					((MyApplication) getApplication()).getMainActivity()
							.setSearchView(R.drawable.bing48x48);
					// 实时更新搜索字符串
					((MyApplication) getApplication()).getMainActivity()
							.setSearchEngine(ConstantDefine.BING_SEARCH);
					Toast.makeText(getApplicationContext(), "搜索引擎设置成功",
							Toast.LENGTH_SHORT).show();
				}
					break;
				}
			}
		};
		baiduLayout.setOnClickListener(onClickListener);
		googleLayout.setOnClickListener(onClickListener);
		bingLayout.setOnClickListener(onClickListener);
	}

	public void showSettingDownloaderDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater inflater = LayoutInflater.from(this);
		View view = inflater.inflate(R.layout.download_setting_dialog, null);
		LinearLayout defaultLayout = (LinearLayout) view
				.findViewById(R.id.defaultDownloadDirectory);
		LinearLayout customLayout = (LinearLayout) view
				.findViewById(R.id.customDownloadDirectory);
		builder.setTitle("设置下载目录");
		builder.setView(view);
		final Dialog dialog = builder.show();// 在不关闭的情况下，连续show()两次是会发生错误的
		OnClickListener onClickListener = new OnClickListener() {
			@SuppressLint("NewApi")
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				switch (v.getId()) {
				case R.id.defaultDownloadDirectory: {
					mEidtor.putString(PreferenceConstants.DOWNLOAD_DIRECTORY,
							Environment.DIRECTORY_DOWNLOADS);
					mEidtor.commit();
					dialog.dismiss();
					// 实时更新显示
					mdefaultDownloadDirectoryTextView
							.setText(ConstantDefine.EXTERNAL_STORAGE + "/"
									+ Environment.DIRECTORY_DOWNLOADS);
					Toast.makeText(getApplicationContext(), "下载目录设置成功",
							Toast.LENGTH_SHORT).show();

				}
					break;
				case R.id.customDownloadDirectory: {
					mEidtor.putString("engine", ConstantDefine.GOOGLE_SEARCH);
					mEidtor.commit();
					dialog.dismiss();

					// 通过代码来创建dialog，界面也是用代码来实现的，并不是用xml
					final AlertDialog.Builder downLocationPicker = new AlertDialog.Builder(
							SettingActivity.this);
					LinearLayout layout = new LinearLayout(SettingActivity.this);
					downLocationPicker.setTitle("自定制下载目录");
					final EditText getDownload = new EditText(
							SettingActivity.this);
					getDownload.setBackgroundResource(0);
					String downloadLocation = mSharePreferences.getString(
							PreferenceConstants.DOWNLOAD_DIRECTORY,
							Environment.DIRECTORY_DOWNLOADS);
					int padding = convertToDensityPixels(
							getApplicationContext(), 10);

					LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
							LinearLayout.LayoutParams.MATCH_PARENT,
							LinearLayout.LayoutParams.MATCH_PARENT);

					getDownload.setLayoutParams(lparams);
					getDownload.setTextColor(Color.DKGRAY);
					getDownload.setText(downloadLocation);
					getDownload.setPadding(0, padding, padding, padding);

					TextView v1 = new TextView(SettingActivity.this);
					v1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
					v1.setTextColor(Color.DKGRAY);
					// 指定前面的textView显示/storage/sdcard/
					v1.setText(ConstantDefine.EXTERNAL_STORAGE + '/');
					v1.setPadding(padding, padding, 0, padding);
					layout.addView(v1);
					layout.addView(getDownload);
					if (API < 16) {
						layout.setBackgroundDrawable(getResources()
								.getDrawable(android.R.drawable.edit_text));
					} else {
						layout.setBackground(getResources().getDrawable(
								android.R.drawable.edit_text));
					}
					downLocationPicker.setView(layout);
					downLocationPicker.setPositiveButton(getResources()
							.getString(R.string.action_ok),
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									String text = getDownload.getText()
											.toString();
									mEidtor.putString(
											PreferenceConstants.DOWNLOAD_DIRECTORY,
											text);
									mEidtor.commit();
									mdefaultDownloadDirectoryTextView
											.setText(ConstantDefine.EXTERNAL_STORAGE
													+ '/' + text);
									Toast.makeText(getApplicationContext(),
											"自定制下载目录设置成功", Toast.LENGTH_SHORT)
											.show();
								}
							});
					downLocationPicker.show();
				}
					break;
				}
			}
		};

		defaultLayout.setOnClickListener(onClickListener);
		customLayout.setOnClickListener(onClickListener);
	}

	private OnClickListener mOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.historyTextView: {
				showHistoryDialog();

			}
				break;
			case R.id.tabTextView: {
				showBookMarkDialog();
			}
				break;
			case R.id.settingHome: {
				showHomeSettingDialog();
			}
				break;
			case R.id.settingSearchEngine: {
				showSearchEngineDialog();
			}
				break;
			case R.id.settingDownloader: {
				showSettingDownloaderDialog();
			}
				break;
			case R.id.advanceSetting: {

			}
				break;
			case R.id.currentVersion: {

			}
				break;
			}
		}
	};

	/**
	 * Returns the number of pixels corresponding to the passed density pixels
	 */
	private int convertToDensityPixels(Context context, int densityPixels) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (densityPixels * scale + 0.5f);
	}

}