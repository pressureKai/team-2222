package com.jiangtai.team.dialog

import android.content.Context
import android.view.View
import com.flyco.dialog.widget.base.BottomBaseDialog
import com.jiangtai.team.R
import kotlinx.android.synthetic.main.dialog_confirm.*

/**
 * Created by heCunCun on 2021/3/8
 */
class ConfirmDialog(context: Context, val title: String, val content: String, val okText: String) :
    BottomBaseDialog<ConfirmDialog>(context) {
    override fun onCreateView(): View {
        return View.inflate(context, R.layout.dialog_confirm, null)
    }

    override fun setUiBeforShow() {
        iv_finish.setOnClickListener {
            dismiss()
        }
        tv_title.text = title
        tv_content.text = content
        tv_confirm.text = okText
        tv_confirm.setOnClickListener{
            onConfirmListener?.onConfirm()
        }
    }
    private var onConfirmListener:OnConfirmListener?=null
    interface OnConfirmListener{
        fun onConfirm()
    }
    fun setonConfirmListen(onClickListener:OnConfirmListener){
        onConfirmListener = onClickListener
    }
}