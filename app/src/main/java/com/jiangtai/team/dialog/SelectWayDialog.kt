package com.jiangtai.team.dialog

import android.content.Context
import android.view.View
import com.flyco.dialog.widget.base.BaseDialog
import com.jiangtai.team.R
import com.jiangtai.team.constant.Constant
import kotlinx.android.synthetic.main.dialog_select_way.*

/**
 * Created by heCunCun on 2021/3/26
 */
class SelectWayDialog(context: Context):BaseDialog<SelectWayDialog>(context) {
    override fun onCreateView(): View {
        return View.inflate(context, R.layout.dialog_select_way,null)
    }

    override fun setUiBeforShow() {
        widthScale(0.8f)
       tv_server.setOnClickListener {
           onConfirmListener?.onConfirm(Constant.SERVER)
       }
        tv_loc.setOnClickListener {
            onConfirmListener?.onConfirm(Constant.LOC)
        }
    }


    private var onConfirmListener:OnConfirmListener?=null
    interface OnConfirmListener{
        fun onConfirm(way:Int)
    }
    fun setonConfirmListen(onClickListener:OnConfirmListener){
        onConfirmListener = onClickListener
    }
}