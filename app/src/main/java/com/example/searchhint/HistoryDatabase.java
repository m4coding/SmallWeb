package com.example.searchhint;

import java.util.ArrayList;
import java.util.List;

import com.example.smallweb.BookMarkItem;
import com.example.smallweb.R;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class HistoryDatabase extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;

	private static final String DATABASE_NAME = "history.db";

	private static final String HISTORY_TABLE = "searchHint";

	private static final String BOOKMARK_TABLE = "bookMark";

	private static final String KEY_ID = "id";

	private static final String KEY_URL = "url";

	private static final String KEY_TITLE = "title";

	private static SQLiteDatabase mDatabase;

	public HistoryDatabase(Context context) {
		// 一定要先执行父类的构造函数
		super(context.getApplicationContext(), DATABASE_NAME, null,
				DATABASE_VERSION);
		mDatabase = this.getWritableDatabase();
	}

	/**
	 * 注意的地方：在调getReadableDatabase或getWritableDatabase时，
	 * 会判断指定的数据库是否存在，不存在则调SQLiteDatabase.create创建， onCreate只在数据库第一次创建时才执行
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		// 创建表 searchHint 注意指令要写正确
		String createTable = "create table " + HISTORY_TABLE + "(" + KEY_ID
				+ " integer primary key," + KEY_URL + " text," + KEY_TITLE
				+ " text" + ")";
		// 注意书写正确
		db.execSQL(createTable);

		String createTable2 = "create table " + BOOKMARK_TABLE + "(" + KEY_ID
				+ " integer primary key," + KEY_URL + " text," + KEY_TITLE
				+ " text" + ")";
		// 注意书写正确
		db.execSQL(createTable2);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("drop table if exists" + HISTORY_TABLE);
		db.execSQL("drop table if exists" + BOOKMARK_TABLE);
		onCreate(db);
	}

	/**
	 * 判断数据库是否打开
	 * 
	 * @return true表示已打开，false表示没打开
	 */
	public boolean isOpen() {
		if (mDatabase != null) {
			return mDatabase.isOpen();
		} else {
			return false;
		}
	}

	/**
	 * 关闭数据库
	 */
	public synchronized void close() {
		if (mDatabase != null) {
			mDatabase.close();
		}
		super.close();
	}

	/**
	 * 往数据库中添加搜索的历史数据<br>
	 * <b>注意：</b> 先是删除掉以前的url对应的数据，然后再往数据库的表中插入数据
	 * 
	 * @param url
	 *            网址
	 * @param title
	 *            网页对应的标题
	 */
	public synchronized void addHistoryItem(String url, String title) {
		// 先删掉对应的url
		mDatabase.delete(HISTORY_TABLE, KEY_URL + " = ?", new String[] { url });
		// ContentValues为存放一系列值的对象
		ContentValues values = new ContentValues();
		values.put(KEY_URL, url);
		values.put(KEY_TITLE, title);
		// 往表中插入数据
		mDatabase.insert(HISTORY_TABLE, null, values);
	}

	public synchronized int addBookMarkItem(String url, String title) {
		// 先删掉对应的url
		mDatabase
				.delete(BOOKMARK_TABLE, KEY_URL + " = ?", new String[] { url });
		// ContentValues为存放一系列值的对象
		ContentValues values = new ContentValues();
		values.put(KEY_URL, url);
		values.put(KEY_TITLE, title);
		// 往表中插入数据
		return (int) mDatabase.insert(BOOKMARK_TABLE, null, values);
	}

	/**
	 * 根据搜索内容在表的url栏和title栏进行查找
	 * 
	 * @param search
	 *            搜索内容
	 * @return 返回在数据库中搜索到的对应项的List
	 */
	public synchronized List<HistoryItem> findHistoryItems(String search) {
		List<HistoryItem> itemList = new ArrayList<HistoryItem>();
		// SELECT 列名称 FROM 表名称 % 表示任意0个或多个字符
		// select * from history where title like '%search%' or url like
		// '%search%'
		String selectQuery = "select * from " + HISTORY_TABLE + " where "
				+ KEY_TITLE + " like '%" + search + "%' or " + KEY_URL
				+ " like '%" + search + "%'";
		Cursor cursor = mDatabase.rawQuery(selectQuery, null);

		int n = 0;
		// 定位到搜索到的数据最后
		if (cursor.moveToLast()) {
			do {
				HistoryItem item = new HistoryItem();
				item.setID(Integer.parseInt(cursor.getString(0)));
				item.setUrl(cursor.getString(1));
				item.setTitle(cursor.getString(2));
				item.setImageId(R.drawable.ic_history);
				// Adding item to list
				itemList.add(item);
				n++;
			} while (cursor.moveToPrevious() && n < 5);
		}
		cursor.close();
		// return item list
		return itemList;
	}

	public List<HistoryItem> getAllHistoryItems() {
		List<HistoryItem> itemList = new ArrayList<HistoryItem>();
		String selectQuery = "SELECT  * FROM " + HISTORY_TABLE;

		Cursor cursor = mDatabase.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) {
			do {
				HistoryItem item = new HistoryItem();
				item.setID(Integer.parseInt(cursor.getString(0)));
				item.setUrl(cursor.getString(1));
				item.setTitle(cursor.getString(2));
				item.setImageId(R.drawable.ic_history);
				itemList.add(item);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return itemList;
	}

	public List<BookMarkItem> getAllBookMarkItems() {
		List<BookMarkItem> itemList = new ArrayList<BookMarkItem>();
		String selectQuery = "SELECT  * FROM " + BOOKMARK_TABLE;

		Cursor cursor = mDatabase.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) {
			do {
				BookMarkItem item = new BookMarkItem();
				item.setID(Integer.parseInt(cursor.getString(0)));
				item.setUrl(cursor.getString(1));
				item.setTitle(cursor.getString(2));
				item.setImageId(R.drawable.ic_history);
				itemList.add(item);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return itemList;
	}

	public synchronized void deleteHistoryItem(String url) {
		mDatabase.delete(HISTORY_TABLE, KEY_URL + " = ?", new String[] { url });
	}

	public synchronized void deleteBookMarkItem(String url) {
		mDatabase
				.delete(BOOKMARK_TABLE, KEY_URL + " = ?", new String[] { url });
	}
}