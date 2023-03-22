package com.jiangtai.count.ui.data

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.jiangtai.count.R
import com.jiangtai.count.base.BaseFragment
import com.jiangtai.count.bean.CountRecordBean
import com.jiangtai.count.bean.DeleteBean
import com.jiangtai.count.util.CommonUtil
import kotlinx.android.synthetic.main.fragment_data.*
import org.litepal.LitePal

@SuppressLint("ValidFragment")
class DataFragment(val dataType: Int) : BaseFragment() {
    private var dataItemClickListener: DataAdapter.ItemClickListener? = null


    override fun attachLayoutRes(): Int {
        return R.layout.fragment_data
    }

    override fun initView(view: View) {

        dataItemClickListener = object : DataAdapter.ItemClickListener {
            override fun itemClick(countBean: CountRecordBean) {
                countBean.startActivity()
            }
        }
        list.layoutManager = LinearLayoutManager(requireContext())
        list.adapter = DataAdapter(requireContext(), ArrayList(), dataItemClickListener!!)
        getData()
    }

    override fun lazyLoad() {

    }


    private fun getData() {
        val find = LitePal.where(
            "recordType = ? and loginId = ? ",
            dataType.toString(),
            CommonUtil.getLoginUserId()
        ).find(
            CountRecordBean::class.java
        )

        if (find.size == 0) {
            tv_empty.visibility = View.VISIBLE
            list.visibility = View.GONE
        } else {
            list.visibility = View.VISIBLE
            tv_empty.visibility = View.GONE
            find.reverse()
            list.adapter = DataAdapter(requireContext(), find, dataItemClickListener!!)
        }
    }


    fun refreshData(deleteBean: DeleteBean) {
        val data = (list.adapter as DataAdapter).data
        val arrayList = ArrayList<CountRecordBean>()
        data?.let {
            arrayList.addAll(data)
            var beRemoveIndex = -1
            data.forEachIndexed { index, countRecordBean ->
                if (countRecordBean.recordID == deleteBean.beDeleteID) {
                    beRemoveIndex = index
                }
            }

            if (beRemoveIndex != -1) {
                arrayList.removeAt(beRemoveIndex)
            }

            Handler(Looper.getMainLooper()).post {
                list.adapter = DataAdapter(requireContext(), arrayList, dataItemClickListener!!)
                if (arrayList.size == 0) {
                    tv_empty.visibility = View.VISIBLE
                } else {
                    tv_empty.visibility = View.GONE
                }
            }
        }

    }

}