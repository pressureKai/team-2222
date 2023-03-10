package com.jiangtai.team.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jiangtai.team.R;


/**
 **/
public class LoadingDialog {
    private static Dialog mDialog;

    public static void show(Context context) {
        // 判断是否可以显示dialog
        if (checkCanShow(context)) {
            return;
        }

        if (mDialog != null && mDialog.isShowing()) {
            return;
        }

        mDialog = new Dialog(context, R.style.BaseDialog);
        View view = LayoutInflater.from(context)
                .inflate(R.layout.dialog_loading, null);
        mDialog.addContentView(view,
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();
    }

    public static void dismiss() {

        if (mDialog != null && mDialog.isShowing()) {
            try{
                mDialog.dismiss();
                mDialog = null;
            }catch (Exception e){

            }

        }
    }

    private static boolean checkCanShow(Context context) {
        Activity act;
        if (context instanceof Activity) {
            act = (Activity) context;
        } else {
            return true;
        }

        boolean isDestroyed  = act.isDestroyed();
        boolean isFinishing = act.isFinishing();
        return isDestroyed || isFinishing;
    }
}
