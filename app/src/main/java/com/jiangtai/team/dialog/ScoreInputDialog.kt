package com.jiangtai.team.dialog

import android.content.Context
import android.view.View
import com.blankj.utilcode.util.ToastUtils
import com.flyco.dialog.widget.base.BottomBaseDialog
import com.jiangtai.team.R
import kotlinx.android.synthetic.main.dialog_score_input.*

/**
 * Created by heCunCun on 2021/3/9
 */
class ScoreInputDialog(context: Context,var contentTitle:String,var unit:String) :BottomBaseDialog<ScoreInputDialog>(context){
    override fun onCreateView(): View {
        val inflater = View.inflate(context, R.layout.dialog_score_input, null)

        return inflater
    }
    private var isPass = true
    override fun setUiBeforShow() {
        iv_finish.setOnClickListener {
            dismiss()
        }
//        spanner.onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
//            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, id: Long) {
//                isPass = position ==0
//            }
//
//            override fun onNothingSelected(p0: AdapterView<*>?) {
//
//            }
//        }

        tv_content.text = contentTitle
        tv_unit.text = unit
        tv_confirm.setOnClickListener {
            val etScore = et_score.text.toString().trim()
            if (etScore.isNotEmpty()){
                onConfirmListener?.onConfirm(etScore)
            }else{
                ToastUtils.showShort("请输入成绩")
            }

        }

    }
    private var onConfirmListener:OnConfirmListener?=null
    interface OnConfirmListener{
        fun onConfirm(etScore:String)
    }
    fun setonConfirmListen(onClickListener:OnConfirmListener){
        onConfirmListener = onClickListener
    }
}