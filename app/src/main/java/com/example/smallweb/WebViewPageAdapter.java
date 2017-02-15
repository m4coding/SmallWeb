package com.example.smallweb;

import java.util.ArrayList;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

public class WebViewPageAdapter extends PagerAdapter {

	private ArrayList<WebView> mList;
	
	public WebViewPageAdapter(ArrayList<WebView> list) {
		mList = list;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if(mList != null) {
			return mList.size(); //返回页卡的数量
		} else {
			return 0;
		}
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		//官方推荐这样做
		return arg0 == arg1;
	}
	
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		// TODO Auto-generated method stub
		container.removeView(mList.get(position)); //删除页卡
	}
	
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		// TODO Auto-generated method stub
		container.addView(mList.get(position), 0); //添加页卡
		return mList.get(position);
	}
	
}