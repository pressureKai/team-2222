package com.jiangtai.count.dialog

import android.content.Context
import android.view.View
import com.flyco.dialog.widget.base.BottomBaseDialog
import com.jiangtai.count.R
import kotlinx.android.synthetic.main.dialog_confirm.*

/**
 * Created by heCunCun on 2021/3/8
 */
class SaveDialog(context: Context) :
    BottomBaseDialog<SaveDialog>(context) {
    override fun onCreateView(): View {
        val inflater = View.inflate(context, R.layout.dialog_save, null)

        return inflater
    }

    override fun setUiBeforShow() {
        tv_confirm.setOnClickListener{
            dismiss()
        }
    }
}