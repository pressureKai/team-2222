package com.jiangtai.team.dialog

import android.content.Context
import android.view.View
import com.blankj.utilcode.util.ToastUtils
import com.flyco.dialog.widget.base.BottomBaseDialog
import com.jiangtai.team.R
import kotlinx.android.synthetic.main.dialog_score_input_2.*

/**
 * Created by heCunCun on 2021/3/9
 */
class ScoreInputDialog2(context: Context, var contentTitle:String, var unit1:String,var unit2:String) :BottomBaseDialog<ScoreInputDialog2>(context){
    override fun onCreateView(): View {
        val inflater = View.inflate(context, R.layout.dialog_score_input_2, null)

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
        tv_unit1.text = unit1
        tv_unit2.text = unit2
        tv_confirm.setOnClickListener {
            val etScore1= et_score_h.text.toString().trim()
            val etScore2 = et_score.text.toString().trim()
            if (etScore1.isNotEmpty() && etScore2.isNotEmpty()){
                onConfirmListener?.onConfirm(etScore1,etScore2)
            }else{
                ToastUtils.showShort("请输入成绩")
            }

        }

    }
    private var onConfirmListener:OnConfirmListener?=null
    interface OnConfirmListener{
        fun onConfirm(etScore1:String,etScore2:String)
    }
    fun setonConfirmListen(onClickListener:OnConfirmListener){
        onConfirmListener = onClickListener
    }
}