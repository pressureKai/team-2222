package com.jiangtai.count.net;


import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.blankj.utilcode.util.ToastUtils;
import com.jiangtai.count.base.BaseBean;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * api请求的结果回调
 * data 是一个bean对象的解析回调
 */

public abstract class CallbackObserver<T> implements Observer<BaseBean<T>> {


    @Override
    public void onSubscribe(@NonNull Disposable d) {
        onStart();
    }

    @Override
    public void onNext(BaseBean<T> tBaseResultBean) {
        try {

            if (tBaseResultBean.isSuccessed()) {
                T t = tBaseResultBean.getData();
                onSucceed(t,tBaseResultBean.getMessage());
            } else {

                if (!TextUtils.isEmpty(tBaseResultBean.getMessage())){
                    ToastUtils.showShort(tBaseResultBean.getMessage());
                    Log.e("Api",tBaseResultBean.getMessage());
                }
                onFailed();
            }

        } catch (Exception e) {
            Log.e("exception",e.getLocalizedMessage());
            ToastUtils.showShort("网络异常");
            onFailed();
        }

    }

    @Override
    public void onError(Throwable t) {
        ToastUtils.showShort("网络异常");
        onFailed();
    }

    @Override
    public void onComplete() {
    }

    /**
     * 请求开始
     */
    protected void onStart() {

    }

    /**
     * 请求成功
     */
    protected abstract void onSucceed(T t, String desc);


    /**
     * 请求异常
     */
    protected void onException(Throwable t) {

    }

    /**
     * 请求错误
     */
    protected abstract void onFailed();

}
