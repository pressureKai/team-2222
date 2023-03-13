package com.jiangtai.team.ui.data

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.jiangtai.team.R
import com.jiangtai.team.bean.CountRecordBean
import com.jiangtai.team.bean.Person
import java.text.SimpleDateFormat
import java.util.*

class DataAdapter(private val context: Context?,
                  private val data: List<CountRecordBean>?,
                  private val itemClick:ItemClickListener) :
    RecyclerView.Adapter<DataAdapter.ViewHolder>() {
    override fun getItemCount(): Int {
        return data!!.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDes: TextView
        val tvTime: TextView
        val llData: LinearLayout

        init {
            tvDes = itemView.findViewById(R.id.tv_des)
            tvTime = itemView.findViewById(R.id.tv_time)
            llData = itemView.findViewById(R.id.ll_data)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_data, parent, false)

        return DataAdapter.ViewHolder(view)
    }

    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        data?.let {
            val get = data[position]
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd  hh:mm")
            val sDate = simpleDateFormat.format(Date(get.recordTime.toLong()))
            val message = when (get.recordType) {
                CountRecordBean.DEVICE_TYPE -> {
                    "$sDate${"车辆日常"}记录单"
                }
                CountRecordBean.CAR_FIX_TYPE -> {
                    "$sDate${"车辆维修"}记录单"
                }
                CountRecordBean.DEVICE_FILL_TYPE -> {
                    "$sDate${"设施监控"}记录单"
                }
                CountRecordBean.OIL_TYPE -> {
                    "$sDate${"YL日清"}记录单"
                }
                CountRecordBean.WEATHER_TYPE -> {
                    "$sDate${"气象信息"}记录单"
                }
                CountRecordBean.HELICOPTER_OIL_TYPE -> {
                    "$sDate${"直升机加油"}记录单"
                }
                CountRecordBean.AIR_TYPE -> {
                    "$sDate${"空投物质采集"}记录单"
                }

                else -> {
                    "$sDate${"未知"}记录单"
                }
            }
            holder.tvDes.text = message
            holder.tvTime.text = sDate
            holder.llData.setOnClickListener {
                itemClick.itemClick(get)
            }
        }

    }


    interface ItemClickListener {
        fun itemClick(countBean: CountRecordBean)
    }
}