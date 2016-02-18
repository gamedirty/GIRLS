package com.example.girls.manager;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @author zhaojunhui
 * @Description: 美妞的地址
 * @date 2014年11月24日 下午10:25:55
 */
public class HotGirlUrlManager {
    /**
     * @param meiniuClass 美妞品种
     * @param page        页数
     * @return
     * @Description 得到美妞地址
     * @author zhaojunhui
     * @date 2014年11月24日 下午10:28:43
     */
    public static String getMeiNiuURL(String meiniuClass, int page) {
        try {
            meiniuClass = URLEncoder.encode(meiniuClass, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "http://image.baidu.com/data/imgs?col=%E7%BE%8E%E5%A5%B3&tag=" + meiniuClass + "&sort=0&tag3=&pn=" + (page - 1) * 60 + "&rn=60&p=channel&from=1";
    }

}
