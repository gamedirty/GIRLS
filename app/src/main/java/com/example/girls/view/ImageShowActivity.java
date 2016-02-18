package com.example.girls.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

import com.bm.library.PhotoView;
import com.bumptech.glide.Glide;
import com.example.girls.R;
import com.example.girls.data.BaiduImageBean;

public class ImageShowActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_image_show);
        BaiduImageBean bib = (BaiduImageBean) getIntent().getSerializableExtra("obj");
        PhotoView iv = (PhotoView) findViewById(R.id.iv);
        Glide.with(this).load(bib.getDownloadUrl()).into(iv);
        iv.enable();
    }
}
