package com.jiangtai.count.ui.data

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.blankj.utilcode.util.ToastUtils
import com.jiangtai.count.R
import com.jiangtai.count.base.BaseFragment
import com.jiangtai.count.base.NewBaseBean
import com.jiangtai.count.bean.CarFixBean
import com.jiangtai.count.bean.CountRecordBean
import com.jiangtai.count.bean.DeleteBean
import com.jiangtai.count.bean.FeelListBean
import com.jiangtai.count.bean.HelicopterOilInfoBean
import com.jiangtai.count.bean.OilInfoBean
import com.jiangtai.count.bean.WeatherInfoBean
import com.jiangtai.count.constant.Constant
import com.jiangtai.count.net.CallbackListObserver
import com.jiangtai.count.net.MyRetrofit
import com.jiangtai.count.net.ThreadSwitchTransformer
import com.jiangtai.count.ui.adapter.FeelListAdapter
import com.jiangtai.count.util.CommonUtil
import com.jiangtai.count.util.Preference
import kotlinx.android.synthetic.main.activity_feel_list.*
import kotlinx.android.synthetic.main.fragment_data.*
import kotlinx.android.synthetic.main.fragment_data.list
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
    private var loginUserId: String by Preference(Constant.LOGIN_USER_ID, "")

    private fun getCarFixListFromServer() {
        val arrayList = ArrayList<CountRecordBean>()
        val taskList = MyRetrofit.instance.api.deviceFixList(loginUserId)
        taskList.compose(ThreadSwitchTransformer())
            .subscribe(object : CallbackListObserver<NewBaseBean<List<CarFixBean>>>() {
                override fun onSucceed(data: NewBaseBean<List<CarFixBean>>) {
                       data.data.forEach {
                           val save = it.save(false)
                           arrayList.add(save)
                       }

                    requireActivity().runOnUiThread {
                        if (arrayList.size == 0) {
                            tv_empty.visibility = View.VISIBLE
                            list.visibility = View.GONE
                        } else {
                            list.visibility = View.VISIBLE
                            tv_empty.visibility = View.GONE
                            arrayList.reverse()
                            list.adapter = DataAdapter(requireContext(), arrayList, dataItemClickListener!!)
                        }
                    }
                }

                override fun onFailed() {

                }
            })
    }

    private fun getOilListFromServer() {
        val arrayList = ArrayList<CountRecordBean>()
        val taskList = MyRetrofit.instance.api.getOilList(loginUserId)
        taskList.compose(ThreadSwitchTransformer())
            .subscribe(object : CallbackListObserver<NewBaseBean<List<OilInfoBean>>>() {
                override fun onSucceed(data: NewBaseBean<List<OilInfoBean>>) {
                    data.data.forEach {
                        val save = it.save(false)
                        arrayList.add(save)
                    }

                    requireActivity().runOnUiThread {
                        if (arrayList.size == 0) {
                            tv_empty.visibility = View.VISIBLE
                            list.visibility = View.GONE
                        } else {
                            list.visibility = View.VISIBLE
                            tv_empty.visibility = View.GONE
                            arrayList.reverse()
                            list.adapter = DataAdapter(requireContext(), arrayList, dataItemClickListener!!)
                        }
                    }
                }

                override fun onFailed() {

                }
            })
    }

    private fun getWeatherListFromServer() {
        val arrayList = ArrayList<CountRecordBean>()
        val taskList = MyRetrofit.instance.api.getWeatherList(loginUserId)
        taskList.compose(ThreadSwitchTransformer())
            .subscribe(object : CallbackListObserver<NewBaseBean<List<WeatherInfoBean>>>() {
                override fun onSucceed(data: NewBaseBean<List<WeatherInfoBean>>) {
                    data.data.forEach {
                        val save = it.save(false)
                        arrayList.add(save)
                    }

                    requireActivity().runOnUiThread {
                        if (arrayList.size == 0) {
                            tv_empty.visibility = View.VISIBLE
                            list.visibility = View.GONE
                        } else {
                            list.visibility = View.VISIBLE
                            tv_empty.visibility = View.GONE
                            arrayList.reverse()
                            list.adapter = DataAdapter(requireContext(), arrayList, dataItemClickListener!!)
                        }
                    }
                }

                override fun onFailed() {

                }
            })
    }


    private fun getHelicopterListFromServer() {
        val arrayList = ArrayList<CountRecordBean>()
        val taskList = MyRetrofit.instance.api.getHelicopterList(loginUserId)
        taskList.compose(ThreadSwitchTransformer())
            .subscribe(object : CallbackListObserver<NewBaseBean<List<HelicopterOilInfoBean>>>() {
                override fun onSucceed(data: NewBaseBean<List<HelicopterOilInfoBean>>) {
                    data.data.forEach {
                        val save = it.save(false)
                        arrayList.add(save)
                    }

                    requireActivity().runOnUiThread {
                        if (arrayList.size == 0) {
                            tv_empty.visibility = View.VISIBLE
                            list.visibility = View.GONE
                        } else {
                            list.visibility = View.VISIBLE
                            tv_empty.visibility = View.GONE
                            arrayList.reverse()
                            list.adapter = DataAdapter(requireContext(), arrayList, dataItemClickListener!!)
                        }
                    }
                }

                override fun onFailed() {

                }
            })
    }


    private fun getData() {
        val find = ArrayList<CountRecordBean>()
        when (dataType) {
            CountRecordBean.CAR_FIX_TYPE -> {
              //  getCarFixListFromServer()
            }
            CountRecordBean.OIL_TYPE -> {
            //   getOilListFromServer()
            }
            CountRecordBean.WEATHER_TYPE -> {
            //    getWeatherListFromServer()
            }
            CountRecordBean.HELICOPTER_OIL_TYPE -> {
                getHelicopterListFromServer()
            }
            else -> {
                val find1 = LitePal.where(
                    "recordType = ? and loginId = ? ",
                    dataType.toString(),
                    CommonUtil.getLoginUserId()
                ).find(
                    CountRecordBean::class.java
                )
                find.addAll(find1)
            }
        }
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