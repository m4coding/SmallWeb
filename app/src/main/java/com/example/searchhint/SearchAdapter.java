package com.example.searchhint;

import com.example.smallweb.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

//适配器实现Filterable接口实现数据过滤功能
public class SearchAdapter extends BaseAdapter implements Filterable {

	// 用于存放历史内容
	private List<HistoryItem> mHistory;
	// 用于存放google搜索的提示内容
	private List<HistoryItem> mSuggestions;
	// 用于存放过滤后的内容
	private List<HistoryItem> mFilteredList;
	private HistoryDatabase mHistoryDatabase;
	// 目前google服务正常情况不可用，将其置为false先不使用google的suggestion功能，百度的搜索提示功能链接目前未找到
	private boolean mUseGoogle = false;
	private Context mContext;

	public SearchAdapter(Context context) {
		mHistoryDatabase = new HistoryDatabase(context);
		mFilteredList = new ArrayList<HistoryItem>();
		mHistory = new ArrayList<HistoryItem>();
		mSuggestions = new ArrayList<HistoryItem>();
		mContext = context;
	}

	public SearchAdapter(Context context, HistoryDatabase hd) {
		mHistoryDatabase = hd;
		mFilteredList = new ArrayList<HistoryItem>();
		mHistory = new ArrayList<HistoryItem>();
		mSuggestions = new ArrayList<HistoryItem>();
		mContext = context;
	}

	@Override
	public int getCount() {
		if (mFilteredList != null) {
			return mFilteredList.size();
		} else {
			return 0;
		}
	}

	@Override
	public Object getItem(int position) {
		return mFilteredList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		SuggestionHolder holder = null;

		if (row == null) {
			LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
			row = inflater.inflate(R.layout.search_hint, parent, false);

			holder = new SuggestionHolder();
			holder.mTitle = (TextView) row.findViewById(R.id.title);
			holder.mUrl = (TextView) row.findViewById(R.id.url);
			holder.mImage = (ImageView) row.findViewById(R.id.suggestionIcon);
			row.setTag(holder);
		} else {
			holder = (SuggestionHolder) row.getTag();
		}

		// 获取历史项目
		HistoryItem web = mFilteredList.get(position);
		holder.mTitle.setText(web.getTitle());
		holder.mUrl.setText(web.getUrl());

		int imageId = 0;
		// 根据id，显示对应的图标
		switch (web.getImageId()) {
		case R.drawable.ic_search: {
			imageId = R.drawable.ic_search;
			holder.mTitle.setTextColor(Color.GRAY);
			break;
		}
		case R.drawable.ic_history: {
			imageId = R.drawable.ic_history;
			holder.mTitle.setTextColor(Color.GRAY);
			break;
		}
		}

		holder.mImage.setImageDrawable(mContext.getResources().getDrawable(
				imageId));

		return row;
	}

	@Override
	public Filter getFilter() {
		return new SearchFilter();
	}

	// 继承Filter，自定义一个过滤器
	private class SearchFilter extends Filter {

		// 这个方法使用来实现数据过滤操作的
		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			FilterResults results = new FilterResults();
			if (constraint == null) {
				return results;
			}
			// 转化为小写 使用当地的配置
			String query = constraint.toString().toLowerCase(
					Locale.getDefault());
			if (query == null) {
				return results;
			}
			// 是否从google搜索中抓取结果显示
			if (mUseGoogle) {
				// 开启一个异步线程去抓取结果
				new RetrieveSearchSuggestions().execute(query);
				// new
				// RetrieveSearchSuggestions().executeOnExecutor(Executors.newCachedThreadPool(),
				// query);
			}

			// 定义一个存放HistoryItem的filter
			List<HistoryItem> filter = new ArrayList<HistoryItem>();

			// 如果数据的处理类没有进行初始化，那就进行初始化
			if (mHistoryDatabase == null || !mHistoryDatabase.isOpen()) {
				mHistoryDatabase = new HistoryDatabase(mContext);
			}
			// 从数据库中查找
			mHistory = mHistoryDatabase.findHistoryItems(constraint.toString());
			// 将查找的数据放入filter中，只放入前5条
			for (int n = 0; n < mHistory.size(); n++) {
				if (n >= 5) {
					break;
				}
				filter.add(mHistory.get(n));
			}

			// 从google搜索中抓取类似的前5条信息
			for (int n = 0; n < mSuggestions.size(); n++) {
				if (filter.size() >= 5) {
					break;
				}
				filter.add(mSuggestions.get(n));
			}

			// 将过滤后的数据保存到FilterResults中
			results.count = filter.size();
			results.values = filter;
			return results;
		}

		@Override
		public CharSequence convertResultToString(Object resultValue) {
			return ((HistoryItem) resultValue).getUrl();
		}

		// 这个方法是用来展现过滤后的数据，过滤后这个方法将被调用
		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {
			// 因为有一个异步任务在获取搜索提示，所以需要同步
			synchronized (mFilteredList) {
				mFilteredList = compositeFilterHint();
				// 通知数据已经改变，界面刷新
				notifyDataSetChanged();
			}
		}

	}

	private class SuggestionHolder {

		ImageView mImage;

		TextView mTitle;

		TextView mUrl;
	}

	// 异步任务，从google搜索中获取提示内容
	private class RetrieveSearchSuggestions extends
			AsyncTask<String, Void, List<HistoryItem>> {

		@Override
		protected List<HistoryItem> doInBackground(String... arg0) {
			// 若网络不连接成功，则返回空的搜索提示
			if (!isNetworkConnected(mContext)) {
				return new ArrayList<HistoryItem>();
			}
			List<HistoryItem> filter = new ArrayList<HistoryItem>();
			String query = arg0[0];
			try {
				query = query.replace(" ", "+");
				URLEncoder.encode(query, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			InputStream download = null;
			try {
				try {
					download = new java.net.URL(
							"http://google.com/complete/search?q=" + query
									+ "&output=toolbar&hl=en").openStream();
					// 解析xml
					XmlPullParserFactory factory = XmlPullParserFactory
							.newInstance();
					factory.setNamespaceAware(true);
					XmlPullParser xpp = factory.newPullParser();
					xpp.setInput(download, "iso-8859-1");
					int eventType = xpp.getEventType();
					int counter = 0;
					while (eventType != XmlPullParser.END_DOCUMENT) {
						if (eventType == XmlPullParser.START_TAG) {
							if ("suggestion".equals(xpp.getName())) {
								String suggestion = xpp.getAttributeValue(null,
										"data");
								filter.add(new HistoryItem(mContext
										.getString(R.string.suggestion)
										+ " \""
										+ suggestion + '"', suggestion,
										R.drawable.ic_search));
								counter++;
								if (counter >= 5) {
									break;
								}
							}
						}
						eventType = xpp.next();
					}
				} finally {
					if (download != null) {
						download.close();
					}
				}
			} catch (FileNotFoundException e) {
			} catch (MalformedURLException e) {
			} catch (IOException e) {
			} catch (XmlPullParserException e) {
			}
			return filter;
		}

		@Override
		protected void onPostExecute(List<HistoryItem> result) {
			synchronized (mFilteredList) {
				mSuggestions = result;
				// 综合提取提示信息
				mFilteredList = compositeFilterHint();
				// 数据变化，通知界面发生改变
				notifyDataSetChanged();
			}
		}

	}

	private boolean isNetworkConnected(Context context) {
		NetworkInfo networkInfo = getActiveNetworkInfo(context);
		return networkInfo != null && networkInfo.isConnected();
	}

	private NetworkInfo getActiveNetworkInfo(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return null;
		}
		return connectivity.getActiveNetworkInfo();
	}

	// 综合各种过滤后的提示信息，提取有数量限制的提示信息
	private List<HistoryItem> compositeFilterHint() {
		List<HistoryItem> filteredList = new ArrayList<HistoryItem>();

		int suggestionsSize = 0;
		int historySize = 0;

		if (mSuggestions != null) {
			suggestionsSize = mSuggestions.size();
		}
		if (mHistory != null) {
			historySize = mHistory.size();
		}

		// 设置各自提示的最大显示条数
		int maxSuggestions = 2;
		int maxHistory = 3;

		// 控制从历史内容获取到的提示不能超过maxHistory
		for (int n = 0; n < historySize; n++) {
			if (n >= maxHistory) {
				break;
			}
			filteredList.add(mHistory.get(n));
		}

		// 控制从搜索中获取到的提示不能超过maxHistory
		for (int n = 0; n < suggestionsSize; n++) {
			if (n >= maxSuggestions) {
				break;
			}
			filteredList.add(mSuggestions.get(n));
		}

		// 返回过滤后的提示信息
		return filteredList;
	}
}
