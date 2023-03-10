package com.jiangtai.team.util;

import android.text.TextUtils;

import okhttp3.HttpUrl;

/**
 * Created by hecuncun on 2021/7/3
 */

public class RegexUtil {
    public static boolean isURL(String str){
        //转换为小写
        if(TextUtils.isEmpty(str)){
            return false;
        }
        HttpUrl httpUrl = HttpUrl.parse(str);
        if (httpUrl == null) {
            return false;
        }else {
            return true;
        }
    }

}
