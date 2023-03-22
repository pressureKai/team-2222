package com.jiangtai.count.ui.majorpracticepeople

import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.jiangtai.count.R
import com.jiangtai.count.bean.Person

/**
 * @author xiezekai
 * @des 专项任务详情页 -> 按计划名单查看 -> 人员列表适配器
 * @create time 2021/8/5 11:45 上午
 */
class MajorPracticePeopleAdapter :
    BaseQuickAdapter<Person, BaseViewHolder>(R.layout.recycler_major_practice_people) {
    override fun convert(helper: BaseViewHolder, item: Person?) {
        val peopleId = helper.getView<TextView>(R.id.people_id)
        val peopleName = helper.getView<TextView>(R.id.people_name)
        helper.addOnClickListener(R.id.more)

        item?.let {
            peopleId.text = it.personId
            peopleName.text = it.name
        }
    }
}