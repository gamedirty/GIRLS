package com.example.girls;

import android.app.Application;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;

public class MeiNiuApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        File cacheDir = StorageUtils.getOwnCacheDirectory(getApplicationContext(), "infinityhg/caches");
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .memoryCacheExtraOptions(800, 1200)
                .threadPoolSize(3)
                // 线程池的数量
                .threadPriority(Thread.NORM_PRIORITY - 2)
                // 线程池的属性
                .denyCacheImageMultipleSizesInMemory().memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024)).memoryCacheSize(2 * 1024 * 1024)
                .diskCacheSize(100 * 1024 * 1024).tasksProcessingOrder(QueueProcessingType.LIFO).diskCacheFileCount(100).diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCache(new UnlimitedDiscCache(cacheDir)).defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                .imageDownloader(new BaseImageDownloader(getApplicationContext())).writeDebugLogs().build();

        ImageLoader.getInstance().init(config);
    }
}
