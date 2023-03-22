package com.jiangtai.count.util;

import android.content.Context;
import android.util.Log;

import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.Gson;
import com.jiangtai.count.bean.IsTaskBean;
import com.jiangtai.count.bean.ReturnIsBean;
import com.jiangtai.count.login.LoginActivity;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RunnaUtil {
    public static String GetisUpdate = "http://192.168.1.13:5080/dss/platform/中电太极/update/task";
    public static String GetisUpdate1 = "http://192.168.1.13:5080/dss/platform/中电太极/insert/mp";
    public static String GetisUpdate2 = "http://192.168.1.13:5080/dcmp/operation/api/positionReceive";

    public static void RunnaUtil(Context context,IsTaskBean isTaskBean){
        for (int i = 0; i < isTaskBean.getData().size(); i++) {
            String task_code = isTaskBean.getData().get(i).getTask_code();
            isUpdate(task_code,LoginActivity.access_token);
        }
    }
    public static void isUpdate(String task_code,String token){
        OkHttpClient mClient = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(GetisUpdate+"?task_code="+task_code+"&access_token="+token)
                .get()
                .build();
        Call call1 = mClient.newCall(request);
        call1.enqueue(new Callback() {

            @Override
            public void onFailure(Call call1, IOException e) {
                ToastUtils.showShort(e.toString());
            }

            @Override
            public void onResponse(Call call1, Response response) throws IOException {
                String string = response.body().string();
                Gson gson = new Gson();
                ReturnIsBean returnIsBean = gson.fromJson(string, ReturnIsBean.class);
                if (returnIsBean.getCode().equals("00000")){
                    Log.e("update","成功");
                }

            }
        });
    }


    public static void isUpdatalist(String taskID,String slong,String lat,String peopleID,String hr,String get_time,String token){


        OkHttpClient mClient = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(GetisUpdate1+"?taskID="+taskID+"&slong="+slong+"&lat="+lat+"&peopleID="+peopleID+"&hr="+hr+"&get_time="+get_time+"&access_token="+token)
                .get()
                .build();
        Call call1 = mClient.newCall(request);
        call1.enqueue(new Callback() {



            @Override
            public void onFailure(Call call1, IOException e) {
                ToastUtils.showShort(e.toString());
                Log.e("update",e.toString());
            }

            @Override
            public void onResponse(Call call1, Response response) throws IOException {
                String string = response.body().string();
                Gson gson = new Gson();
                ReturnIsBean returnIsBean = gson.fromJson(string, ReturnIsBean.class);

                Log.e("update",string);


            }
        });
    }


    public static void isscjUpdatalist(String taskID,String latCenter,String lngCenter,String slong,String lat,String peopleID,String hr,String get_time,String token){


        OkHttpClient mClient = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(GetisUpdate2+"?taskID="+taskID+"&slong="+slong+"&lat="+lat+"&peopleID="+peopleID+"&hr="+hr+"&get_time="+get_time)
                .get()
                .build();
        Call call1 = mClient.newCall(request);
        call1.enqueue(new Callback() {



            @Override
            public void onFailure(Call call1, IOException e) {
                ToastUtils.showShort(e.toString());
                Log.e("update",e.toString());

            }

            @Override
            public void onResponse(Call call1, Response response) throws IOException {
                String string = response.body().string();
                Gson gson = new Gson();
                ReturnIsBean returnIsBean = gson.fromJson(string, ReturnIsBean.class);
                Log.e("update",string);


            }
        });
    }

}
