/*
 * Copyright 2014 A.C.R. Development
 */

//参考来android浏览器的源码

package com.example.smallweb;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.WindowManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;

public class SmallWebDownloadListener implements DownloadListener {

	private Activity mActivity;

	SmallWebDownloadListener(Activity activity) {
		mActivity = activity;
	}

	@Override
	public void onDownloadStart(final String url, final String userAgent,
			final String contentDisposition, final String mimetype,
			long contentLength) {

		String fileName = URLUtil.guessFileName(url, contentDisposition,
				mimetype);

		Log.i("download", fileName);

		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					DownloadHandler.onDownloadStart(mActivity, url, userAgent,
							contentDisposition, mimetype, false);
					break;

				case DialogInterface.BUTTON_NEGATIVE:
					break;
				}
			}
		};

		AlertDialog.Builder builder = new AlertDialog.Builder(mActivity); // dialog
		builder.setTitle(fileName)
				.setMessage(
						mActivity.getResources().getString(
								R.string.dialog_download))
				.setPositiveButton(
						mActivity.getResources().getString(
								R.string.action_download), dialogClickListener)
				.setNegativeButton(
						mActivity.getResources().getString(
								R.string.action_cancel), dialogClickListener);

		// 由于使用了设置浮窗，所以正常的dialog是显示不了的；将builder创造出来的dialog设置WindowManager.LayoutParams.TYPE_SYSTEM_ALERT属性就可以实现dialog在浮窗之上显示
		Dialog dialog = builder.create();
		dialog.getWindow()
				.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		dialog.show();

	}
}
