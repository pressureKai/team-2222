package com.jiangtai.team.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.Gson;
import com.jiangtai.team.R;
import com.jiangtai.team.application.App;
import com.jiangtai.team.bean.CollectInformationBean;
import com.jiangtai.team.bean.TokenBean;
import com.jiangtai.team.ui.main.MainActivity;
import com.jiangtai.team.util.NetWork;
import com.jiangtai.team.util.SharedPreferanceUtils;


import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText login_phone;
    private EditText login_pwd;
    private Button login_login;
    private SharedPreferences.Editor editor;
    private String jsonStr;
    private CollectInformationBean collectInformationBean;
    private boolean logSuccess = false;
    public static String access_token;
    public static String users_userid;
    private boolean isLogin = false;
    public String GetToken = "http://192.168.1.13:5080/cac/oauth/token";
    public String GetLonin = "http://192.168.1.13:5080/dss/platform/中电太极/users";
    private ProgressDialog show;
    private Date date;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        login_phone = findViewById(R.id.login_phone);
        login_pwd = findViewById(R.id.login_pwd);
        login_login = findViewById(R.id.login_login);
        login_login.setOnClickListener(this);
        getWindow().setStatusBarColor((Color.TRANSPARENT));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        jsonStr = (String) SharedPreferanceUtils.get(App.Companion.getContext(), "loginlist", "");
        if (jsonStr.equals(null) && jsonStr.equals("")) {
            isgetToken();
        } else {
            boolean networkAvailable = NetWork.isNetworkAvailable(LoginActivity.this);
            if (networkAvailable) {
                isgetToken();
            } else {

                if (jsonStr == null) {
                    ToastUtils.showShort("请检查网络");
                }
            }

        }

        SharedPreferences pre = getSharedPreferences("logindata", MODE_PRIVATE);
        if (pre.equals(null)) {
            isgetToken();
        } else {
            String name = pre.getString("name", "");
            String pwd = pre.getString("pwd", "");
            if(!"".equals(name)||name.length()>0)
            login_phone.setText(name);
            if(!"".equals(pwd)||pwd.length()>0)
            login_pwd.setText(pwd);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //明文密文显示
            //登陆按钮
            case R.id.login_login:
                jsonStr = (String) SharedPreferanceUtils.get(App.Companion.getContext(), "loginlist", "");
                Gson gson = new Gson();
                collectInformationBean = gson.fromJson(jsonStr, CollectInformationBean.class);

                String phone = login_phone.getText().toString().trim();
                String pwd = login_pwd.getText().toString().trim();

                if ("".equals(phone) || phone.length() == 0) {
                    ToastUtils.showShort("请输入账号");
                } else if ("".equals(pwd) || pwd.length() == 0) {
                    ToastUtils.showShort("请输入密码");
                } else if (collectInformationBean == null || collectInformationBean.getCode() == null|| "".equals(collectInformationBean.getCode())) {
                    boolean networkAvailable = NetWork.isNetworkAvailable(LoginActivity.this);
                    if (networkAvailable) {
                        isgetToken();
                    } else {
                        ToastUtils.showShort("请检查网络");
                    }
                } else if (collectInformationBean.getCode().equals("00000")) {
                    for (int i = 0; i < collectInformationBean.getData().size(); i++) {
                        if (collectInformationBean.getData().get(i).getUsers_login_uid().equals(phone) && collectInformationBean.getData().get(i).getUsers_tenant_id().equals(pwd)) {
                            editor = getSharedPreferences("logindata", MODE_PRIVATE).edit();
                            editor.clear();
                            editor.putString("name", phone);
                            editor.putString("pwd", pwd);
                            editor.commit();
                            logSuccess = true;
                            users_userid = String.valueOf(collectInformationBean.getData().get(i).getUsers_uuid());
                            ToastUtils.showShort("登录成功");
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                            if (show == null) {
                                show = ProgressDialog.show(LoginActivity.this, "正在登录", "请稍等", true, false);
                            } else {
                                show.setTitle("正在登录");
                                show.setMessage("请稍等");
                            }
                            show.show();
                        }

                    }
                    if (!logSuccess) {
                        ToastUtils.showShort("账号或者密码错误");
                    }

                } else {
                    ToastUtils.showShort("登录失败");
                }
                break;


        }
    }

    public void isgetToken() {
        OkHttpClient mClient1 = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();
        RequestBody formBody1 = null;
        formBody1 = new FormBody.Builder()
                .add("client_id", "test")
                .add("client_secret", "cosmotest")
                .add("grant_type", "password")
                .add("username", "admin")
                .add("password", "Cosmo_admin1")
                .build();

        Request request1 = new Request.Builder()
                .url(GetToken)
                .post(formBody1)
                .build();
        Call call1 = mClient1.newCall(request1);
        call1.enqueue(new Callback() {


            @Override
            public void onFailure(Call call1, IOException e) {
                ToastUtils.showShort("请求失败");
            }

            @Override
            public void onResponse(Call call1, Response response) throws IOException {
                String string = response.body().string();
                Gson gson = new Gson();
                TokenBean tokenBean = gson.fromJson(string, TokenBean.class);
                access_token = tokenBean.getAccess_token();

                isiLogin(access_token);
            }
        });
    }

    public void isiLogin(String token) {
        OkHttpClient mClient = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(GetLonin + "?access_token=" + token)
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
                Log.e("chenjjjejeje", string + "");
                collectInformationBean = gson.fromJson(string, CollectInformationBean.class);
                SharedPreferanceUtils.put(LoginActivity.this, "loginlist", string);

                if (isLogin) {
                    if (LoginActivity.this.collectInformationBean.getCode().equals("00000")) {
                        String phone = login_phone.getText().toString().trim();
                        String pwd = login_pwd.getText().toString().trim();
                        for (int i = 0; i < LoginActivity.this.collectInformationBean.getData().size(); i++) {
                            if (LoginActivity.this.collectInformationBean.getData().get(i).getUsers_login_uid().equals(phone) && LoginActivity.this.collectInformationBean.getData().get(i).getUsers_tenant_id().equals(pwd)) {
                                editor = getSharedPreferences("logindata", MODE_PRIVATE).edit();
                                editor.clear();
                                editor.putString("name", phone);
                                editor.putString("pwd", pwd);
                                editor.commit();
                                logSuccess = true;
                                isLogin = false;
                                users_userid = collectInformationBean.getData().get(i).getUsers_uuid();
                                ToastUtils.showShort("登录成功");
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                                if (show == null) {
                                    show = ProgressDialog.show(LoginActivity.this, "正在登录", "请稍等", true, false);
                                } else {
                                    show.setTitle("正在登录");
                                    show.setMessage("请稍等");
                                }
                                show.show();
                            }

                        }
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        logSuccess = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (show != null && show.isShowing()) {
            show.dismiss();
        }
    }
}