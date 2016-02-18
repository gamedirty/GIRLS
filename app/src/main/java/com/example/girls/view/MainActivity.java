package com.example.girls.view;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ScrollView;
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
import java.util.Arrays;
import java.util.List;

public class MainActivity extends Activity implements AdapterView.OnItemClickListener, View.OnTouchListener {
    private static final String LOADING = "加载中...";
    private static final String PRELOAD = "点击加载更多";
    private ArrayList<BaiduImageBean> data = new ArrayList<BaiduImageBean>();
    private ArrayList<BaiduImageBean> dataAdd;
    private View pd;
    private TextView tv;
    private GridView lv;
    private int page = 1;
    private GirlsAdapter adapter;
    private View moreView;
    private final int GET_MEINVINFO_SUCCESS = 1109;
    private final int GET_FAIL = 1110;

    /**
     * @description 当前显示美妞种类
     */
    private String cls = "";
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case GET_FAIL:
                    showNoMoreInfos();
                    break;
            }
            // 更新数据更新界面
            adapter.addData(dataAdd);
            resetMoreView();
            isLoading = false;
        }
    };

    private void showNoMoreInfos() {
        Toast.makeText(this, "没有更多的图片了", Toast.LENGTH_LONG).show();
    }

    private ScrollView sc;
    private boolean isLoading;

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
        sc = (ScrollView) findViewById(R.id.scroll);
        sc.setOnTouchListener(this);
        moreView = findViewById(R.id.more);
        moreView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                loadMore();
            }
        });
        pd = moreView.findViewById(R.id.progressBar1);
        tv = (TextView) moreView.findViewById(R.id.textView1);
        tv.setText(LOADING);
        lv = (GridView) findViewById(R.id.listView_main_girlsshow);
        lv.getNumColumns();
        adapter = new GirlsAdapter(this, data);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(this);
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
                //加入额外的种类
                List<String> extraClasses = Arrays.asList(getResources().getStringArray(R.array.extraclasses));
                tabs.addAll(extraClasses);
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
            }

            ;
        }.start();
    }

    /**
     * @param string 美妞品种
     * @Description show出美妞
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

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        BaiduImageBean img = data.get(i);
        Intent intent = new Intent(this, ImageShowActivity.class);
        Log.i("info", "图片路径:" + img.getObjUrl());
        intent.putExtra("obj", img);
        startActivity(intent);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_MOVE:
                int scrollY = view.getScrollY();
                int height = view.getHeight();
                int scrollViewMeasuredHeight = sc.getChildAt(0).getMeasuredHeight();
                if ((scrollY + height) == scrollViewMeasuredHeight) {
                    if (!isLoading) {
                        isLoading = true;
                        loadMore();
                    }
                }
                break;
            default:
                break;
        }
        return false;
    }

    /**
     * @author zhaojunhui
     * @Description: TODO
     * @date 2014年11月24日 下午11:26:24
     */
    class ObtainImageInfoThread extends Thread {
        int page;

        public ObtainImageInfoThread(int page) {
            this.page = page;
        }

        @Override
        public void run() {
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
                int len = arr.length();
                Log.i("info", "数组个数:" + len);
                if (len > 1) {
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject b = arr.getJSONObject(i);
                        image = new BaiduImageBean();
                        // image.setDate(b.getString("date"));
                        if (b.has("desc"))
                            image.setDesc(b.getString("desc"));
                        if (b.has("id"))
                            image.setId(b.getString("id"));
                        if (b.has("objUrl"))
                            image.setObjUrl(b.getString("objUrl"));
                        if (b.has("downloadUrl"))
                            image.setDownloadUrl(b.getString("downloadUrl"));
                        if (b.has("imageUrl"))
                            image.setImageUrl(b.getString("imageUrl"));
                        if (b.has("thumbnailUrl"))
                            image.setThumbnailUrl(b.getString("thumbnailUrl"));
                        if (b.has("thumbLargeUrl"))
                            image.setThumbLargeUrl(b.getString("thumbLargeUrl"));
                        if (b.has("thumbLargeTnUrl"))
                            image.setThumbLargeTnUrl(b.getString("thumbLargeTnUrl"));
                        if (b.has("title"))
                            image.setTitle(b.getString("title"));

                        if (!TextUtils.isEmpty(image.getObjUrl()))
                            dataAdd.add(image);
                    }
                    handler.sendEmptyMessage(GET_MEINVINFO_SUCCESS);
                } else {
                    handler.sendEmptyMessage(GET_FAIL);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
