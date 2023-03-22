package com.jiangtai.count.ui.signIn.viewpager;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * <p>文件描述：<p>
 * <p>作者：陈吉庆<p>
 * <p>创建时间：2019/12/11<p>
 * <p>更改时间：2019/12/11<p>
 */

public abstract class BaseFragment  extends Fragment {


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(initLayout(), null);

        initView(inflate);
        initData(inflate);
        return inflate;
    }

    protected abstract void initData(View inflate);

    protected abstract void initView(View inflate);



    protected abstract int initLayout();

    @Override
    public void onDestroy() {

        super.onDestroy();
    }




}
