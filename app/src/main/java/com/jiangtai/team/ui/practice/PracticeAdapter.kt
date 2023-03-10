package com.jiangtai.team.ui.practice

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.jiangtai.team.R
import com.jiangtai.team.bean.NormalPerson

/**
 * Created by heCunCun on 2021/3/9
 */
class PracticeAdapter:BaseQuickAdapter<NormalPerson,BaseViewHolder>(R.layout.item_practice) {
    override fun convert(helper: BaseViewHolder, item: NormalPerson?) {
        item?:return
        helper.setText(R.id.tv_name,item.name)
        helper.setText(R.id.tv_id,item.personId)
    }
}