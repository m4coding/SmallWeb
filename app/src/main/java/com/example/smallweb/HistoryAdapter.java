package com.example.smallweb;

import java.util.ArrayList;

import com.example.searchhint.HistoryDatabase;
import com.example.searchhint.HistoryItem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class HistoryAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<HistoryItem> mHistoryList;

	public HistoryAdapter(Context context, HistoryDatabase hd) {
		// TODO Auto-generated constructor stub
		mHistoryList = (ArrayList<HistoryItem>) hd.getAllHistoryItems();
		mContext = context;
	}

	public ArrayList<HistoryItem> getHistoryList() {
		return mHistoryList;
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		HistoryHolder holder = null;
		if (null == convertView) {
			LayoutInflater inflater = LayoutInflater.from(mContext);
			convertView = inflater.inflate(R.layout.history_item, null);
			holder = new HistoryHolder();
			holder.mTitle = (TextView) convertView
					.findViewById(R.id.titleTextView);
			holder.mUrl = (TextView) convertView.findViewById(R.id.urlTextView);
			convertView.setTag(holder);
		} else {
			holder = (HistoryHolder) convertView.getTag();
		}
		holder.mTitle.setText(mHistoryList.get(position).getTitle());
		holder.mUrl.setText(mHistoryList.get(position).getUrl());
		return convertView;
	}

	class HistoryHolder {

		TextView mTitle;

		TextView mUrl;
	}

}