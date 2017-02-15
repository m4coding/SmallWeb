package com.example.smallweb;

import java.util.ArrayList;

import com.example.searchhint.HistoryDatabase;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class BookMarkAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<BookMarkItem> mBookMarkList;

	public BookMarkAdapter(Context context, HistoryDatabase hd) {
		// TODO Auto-generated constructor stub
		mBookMarkList = (ArrayList<BookMarkItem>) hd.getAllBookMarkItems();
		mContext = context;
	}

	public ArrayList<BookMarkItem> getBookMarkList() {
		return mBookMarkList;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if (mBookMarkList != null) {
			return mBookMarkList.size();
		} else {
			return 0;
		}
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		if (mBookMarkList != null) {
			return mBookMarkList.get(position);
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
		BookMarkHolder holder = null;
		if (null == convertView) {
			LayoutInflater inflater = LayoutInflater.from(mContext);
			convertView = inflater.inflate(R.layout.bookmark_item, null);
			holder = new BookMarkHolder();
			holder.mTitle = (TextView) convertView
					.findViewById(R.id.titleTextView);
			holder.mUrl = (TextView) convertView.findViewById(R.id.urlTextView);
			convertView.setTag(holder);
		} else {
			holder = (BookMarkHolder) convertView.getTag();
		}
		holder.mTitle.setText(mBookMarkList.get(position).getTitle());
		holder.mUrl.setText(mBookMarkList.get(position).getUrl());
		return convertView;
	}

	class BookMarkHolder {

		TextView mTitle;

		TextView mUrl;
	}

}