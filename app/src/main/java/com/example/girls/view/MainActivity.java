package com.example.girls.view;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.girls.R;
import com.example.girls.data.BaiduImageBean;
import com.example.girls.manager.HotGirlUrlManager;
import com.example.girls.util.HTTPUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends Activity {
	private static final String LOADING = "加载中...";
	private static final String PRELOAD = "点击加载更多";
	private ArrayList<BaiduImageBean> data = new ArrayList<BaiduImageBean>();
	private ArrayList<BaiduImageBean> dataAdd;
	private View pd;
	private TextView tv;
	private ListView lv;
	private int page = 1;
	private GirlsAdapter adapter;
	private View moreView;
	/** @description 当前显示美妞种类 */
	private String cls = "";
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			// 更新数据更新界面
			adapter.addData(dataAdd);
			resetMoreView();
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_main);
		setProgressBarIndeterminateVisibility(true);
		getTabs();
		initViews();
	}

	private void initViews() {
		moreView = LayoutInflater.from(this).inflate(R.layout.moreview, null);
		moreView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				loadMore();
			}
		});
		pd = moreView.findViewById(R.id.progressBar1);
		tv = (TextView) moreView.findViewById(R.id.textView1);
		tv.setText(LOADING);
		lv = (ListView) findViewById(R.id.listView_main_girlsshow);
		adapter = new GirlsAdapter(this, data);
		lv.addFooterView(moreView);
		lv.setAdapter(adapter);
	}

	protected void loadMore() {
		page++;
		initData(page);
	}

	private void initData(int page) {
		dataAdd = new ArrayList<BaiduImageBean>();
		setMoreViewLoading();
		new ObtainImageInfoThread(page).start();
	}

	private void setMoreViewLoading() {
		pd.setVisibility(View.VISIBLE);
		tv.setText(LOADING);
		moreView.setEnabled(false);

	}

	private ArrayList<String> tabs;

	/**
	 * @Description 得到美妞品种并show出来
	 * @author zhaojunhui
	 * @date 2014年11月24日 下午10:19:44
	 */
	private void getTabs() {
		new Thread() {

			public void run() {
				tabs = HTTPUtil.getTabs();
				if (tabs != null && tabs.size() > 0) {
					tabs.add(0, "品种选择(*^@^*)");
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_dropdown_item_1line, tabs);
							getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
							ActionBar.OnNavigationListener listener = new OnNavigationListener() {

								@Override
								public boolean onNavigationItemSelected(int itemPosition, long itemId) {
									Toast.makeText(MainActivity.this, "点击了:" + tabs.get(itemPosition), Toast.LENGTH_LONG).show();
									showGirls(tabs.get(itemPosition));
									return false;
								}

							};
							getActionBar().setListNavigationCallbacks(adapter, listener);
							setProgressBarIndeterminateVisibility(false);
						}
					});
				}
			};
		}.start();
	}

	/**
	 * @Description show出美妞
	 * @param string
	 *            美妞品种
	 * @author zhaojunhui
	 * @date 2014年11月24日 下午10:19:12
	 */
	private void showGirls(String string) {
		page = 1;
		cls = string;
		clearListView();
		initData(page);
	}

	protected void resetMoreView() {
		pd.setVisibility(View.GONE);
		tv.setText(PRELOAD);
		moreView.setEnabled(true);
	}

	/**
	 * @Description 清空当前品种美妞
	 * @author zhaojunhui
	 * @date 2014年11月24日 下午11:25:57
	 */
	public void clearListView() {
		adapter.clearData();
	}

	/**
	 * @Description: TODO
	 * @author zhaojunhui
	 * @date 2014年11月24日 下午11:26:24
	 */
	class ObtainImageInfoThread extends Thread {
		int page;

		public ObtainImageInfoThread(int page) {
			this.page = page;
		}

		@Override
		public void run() {
			Log.i("info", "");
			super.run();
			try {
				String jsonUrl = HotGirlUrlManager.getMeiNiuURL(cls, page);
				URL url = new URL(jsonUrl);
				Log.i("info", "jsonUrl:" + jsonUrl);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setConnectTimeout(20 * 1000);
				InputStreamReader inputStreamReader = new InputStreamReader(connection.getInputStream());
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				String buff = null;
				StringBuffer sb = new StringBuffer();
				while ((buff = bufferedReader.readLine()) != null) {
					sb.append(buff);
				}
				bufferedReader.close();
				connection.disconnect();
				String result = sb.toString();
				BaiduImageBean image = null;
				JSONObject obj = new JSONObject(result);
				JSONArray arr = obj.getJSONArray("imgs");
				for (int i = 0; i < arr.length(); i++) {
					JSONObject b = arr.getJSONObject(i);
					image = new BaiduImageBean();
					// image.setDate(b.getString("date"));
					if (b.has("desc"))
						image.setDesc(b.getString("desc"));
					if (b.has("imageHeight"))
						image.setHeight(b.getInt("imageHeight"));
					if (b.has("id"))
						image.setId(b.getString("id"));
					if (b.has("objUrl"))
						image.setObjUrl(b.getString("downloadUrl"));
					if (b.has("imageWidth"))
						image.setWidth(b.getInt("imageWidth"));
					dataAdd.add(image);
				}
				handler.sendEmptyMessage(0);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
