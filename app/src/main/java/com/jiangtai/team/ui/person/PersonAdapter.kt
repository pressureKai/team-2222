package com.jiangtai.team.ui.person

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.jiangtai.team.R
import com.jiangtai.team.bean.Person
import com.jiangtai.team.util.DateUtil

/**
 * Created by heCunCun on 2021/3/8
 */
class PersonAdapter:BaseQuickAdapter<Person,BaseViewHolder>(R.layout.item_person_list) {
    override fun convert(helper: BaseViewHolder, item: Person?) {
        item?:return
        helper.addOnClickListener(R.id.iv_more)
        helper.setText(R.id.tv_name,item.name)
        helper.setText(R.id.tv_time, DateUtil.changeLongToDate(item.updateTime))
        helper.setText(R.id.tv_id,item.personId)
    }
}