package com.jiangtai.count.ui.adapter

import android.view.View
import android.widget.TextView
import com.blankj.utilcode.util.LogUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.jiangtai.count.R
import com.jiangtai.count.bean.Person

class DialogPeopleListAdapter :
    BaseQuickAdapter<Person, BaseViewHolder>(R.layout.recycler_people_list) {
    override fun convert(helper: BaseViewHolder, item: Person?) {
        val personName = helper.getView<TextView>(R.id.person_name)
        val divider = helper.getView<View>(R.id.divider)

        item?.let {
            personName.text = it.name
            LogUtils.e("personName ${it.name}")
            if (helper.position == data.size - 1) {
                divider.visibility = View.INVISIBLE
            } else {
                divider.visibility = View.VISIBLE
            }
        }
    }
}