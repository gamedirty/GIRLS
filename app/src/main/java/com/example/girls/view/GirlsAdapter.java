package com.example.girls.view;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.girls.R;
import com.example.girls.data.BaiduImageBean;

import java.util.ArrayList;

public class GirlsAdapter extends BaseAdapter {
    Context context;
    ArrayList<BaiduImageBean> data;
    private int width;

    public GirlsAdapter(Context context, ArrayList<BaiduImageBean> data) {
        this.context = context;
        this.data = data;
        DisplayMetrics dm = new DisplayMetrics();
        Activity act = (Activity) context;

        act.getWindowManager().getDefaultDisplay().getMetrics(dm);
        width = dm.widthPixels;
        width /= 3;
    }

    public void addData(ArrayList<BaiduImageBean> data) {
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View c, ViewGroup parent) {
        MyViewHolder viewHolder;
        if (c == null) {
            c = View.inflate(context, R.layout.imageitem, null);
            viewHolder = new MyViewHolder();
            viewHolder.iv = (ImageView) c.findViewById(R.id.imageview);
            c.setTag(R.id.imageview, viewHolder);
        } else {
            viewHolder = (MyViewHolder) c.getTag(R.id.imageview);
        }
        viewHolder.iv.setLayoutParams(new ViewGroup.LayoutParams(width, width));
        Glide.with(context).load(data.get(position).getThumbnailUrl()).diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().into(viewHolder.iv);

        return c;
    }

    public class MyViewHolder {
        ImageView iv;
    }

    public static class ViewHolder {
        @SuppressWarnings("unchecked")
        public static <T extends View> T get(View convertView, int id) {
            SparseArray<View> viewHolder = (SparseArray<View>) convertView.getTag();
            if (viewHolder == null) {
                viewHolder = new SparseArray<View>();
                convertView.setTag(viewHolder);
            }
            View childView = viewHolder.get(id);
            if (childView == null) {
                childView = convertView.findViewById(id);
                viewHolder.put(id, childView);
            }
            return (T) childView;
        }
    }

    public void clearData() {
        data.removeAll(data);
        notifyDataSetChanged();
    }

}
