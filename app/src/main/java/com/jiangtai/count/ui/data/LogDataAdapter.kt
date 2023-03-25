package com.jiangtai.count.ui.data

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.jiangtai.count.R
import com.jiangtai.count.bean.CountRecordBean
import com.jiangtai.count.bean.TransformInfo
import java.text.SimpleDateFormat
import java.util.*

class LogDataAdapter(private val context: Context?,
                     val data: List<TransformInfo>?,
                     private val itemClick:ItemClickListener) :
    RecyclerView.Adapter<LogDataAdapter.ViewHolder>() {
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

        return LogDataAdapter.ViewHolder(view)
    }

    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        data?.let {
            val get = data[position]
            val sDate = get.time
            val message = get.message
            holder.tvDes.text = message
            holder.tvTime.text = sDate
        }

    }


    interface ItemClickListener {
        fun itemClick(countBean: CountRecordBean)
    }
}