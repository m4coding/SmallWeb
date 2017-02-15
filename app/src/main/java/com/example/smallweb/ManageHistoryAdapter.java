package com.example.smallweb;

import java.util.ArrayList;
import java.util.HashMap;

import com.example.searchhint.HistoryDatabase;
import com.example.searchhint.HistoryItem;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class ManageHistoryAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<HistoryItem> mHistoryList;
	private HistoryDatabase mHistoryDb;
	private Button mDeleButton = null;
	private HashMap<Integer, Boolean> mCheckMap;

	public ManageHistoryAdapter(Context context, HistoryDatabase hd) {
		// TODO Auto-generated constructor stub
		mHistoryList = (ArrayList<HistoryItem>) hd.getAllHistoryItems();
		mContext = context;
		mHistoryDb = hd;
	}

	public ManageHistoryAdapter(Context context, HistoryDatabase hd, Button bt) {
		// TODO Auto-generated constructor stub
		mHistoryList = (ArrayList<HistoryItem>) hd.getAllHistoryItems();
		mContext = context;
		mDeleButton = bt;
		mCheckMap = new HashMap<Integer, Boolean>();
		mHistoryDb = hd;
	}

	public HashMap<Integer, Boolean> getCheckMap() {
		return mCheckMap;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if (mHistoryList != null) {
			return mHistoryList.size();
		} else {
			return 0;
		}
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		if (mHistoryList != null) {
			return mHistoryList.get(position);
		} else {
			return null;
		}
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	private int temPosition;

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ManageHistoryHolder holder = null;
		temPosition = position;
		if (null == convertView) {
			LayoutInflater inflater = LayoutInflater.from(mContext);
			convertView = inflater.inflate(R.layout.manage_history_item, null);
			holder = new ManageHistoryHolder();
			holder.mTitle = (TextView) convertView
					.findViewById(R.id.manageHistoryTitleTextView);
			holder.mUrl = (TextView) convertView
					.findViewById(R.id.manageHistoryUrlTextView);
			holder.mCheckBox = (CheckBox) convertView
					.findViewById(R.id.historyCheckBox);
			holder.mCheckBox
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							// TODO Auto-generated method stub
							if (isChecked) {
								// mCheckMap中保存的是被选中的复选框的
								mCheckMap.put(buttonView.getId(), isChecked);
							} else {
								// 不被选中，则移除
								mCheckMap.remove(buttonView.getId());
							}
							// 进行判断map中是否有值，从而使deleteButton是否可以被点击
							if (mCheckMap.size() > 0) {
								mDeleButton.setClickable(true);
								mDeleButton.setTextColor(Color.BLACK);
							} else {
								mDeleButton.setClickable(false);
								mDeleButton.setTextColor(Color.GRAY);
							}
						}
					});

			mDeleButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					ArrayList<Integer> list = new ArrayList<Integer>();
					// 先遍历key，将历史列表中需要删除的内容删除掉
					for (Integer key : mCheckMap.keySet()) {
						mHistoryDb.deleteHistoryItem(mHistoryList.get(key)
								.getUrl());
						// mCheckMap.remove(key);
						list.add(key);
						// holder.mCheckBox.setChecked(false);
					}
					// 然后再对mCheckMap进行移除操作
					for (Integer key : list) {
						mCheckMap.remove(key);
					}
					// 删除后重新获取新的数据
					mHistoryList = (ArrayList<HistoryItem>) mHistoryDb
							.getAllHistoryItems();
					// 进行判断map中是否有值，从而使deleteButton是否可以被点击
					if (mCheckMap.size() > 0) {
						mDeleButton.setClickable(true);
						mDeleButton.setTextColor(Color.BLACK);
					} else {
						mDeleButton.setClickable(false);
						mDeleButton.setTextColor(Color.GRAY);
					}
					// 通知适配器发生变化，listView进行刷新显示
					notifyDataSetChanged();
				}
			});

			convertView.setTag(holder);
		} else {
			holder = (ManageHistoryHolder) convertView.getTag();
		}
		holder.mTitle.setText(mHistoryList.get(position).getTitle());
		holder.mUrl.setText(mHistoryList.get(position).getUrl());
		// 为每个复选框设置对应的位置的id，以便后面的复选框的勾选操作
		holder.mCheckBox.setId(position);
		// 当找不到时即表示当前的checkBox没有被选中
		if (mCheckMap.get(position) == null) {
			holder.mCheckBox.setChecked(false);
		}
		return convertView;
	}

	class ManageHistoryHolder {

		TextView mTitle;

		TextView mUrl;

		CheckBox mCheckBox;
	}

}