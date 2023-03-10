package com.jiangtai.team.util

import android.annotation.SuppressLint
import android.text.TextUtils
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by heCunCun on 2020/12/15
 */
object DateUtil {
    //获取当前时间yyyy-MM-dd
   private const val sdfPattern ="yyyy-MM-dd"
    @SuppressLint("SimpleDateFormat")
    fun getCurrentTime(): String {
        val sdf = SimpleDateFormat(sdfPattern)
        val date = Date()
        return sdf.format(date)
    }

    //Long转成  yyyy-MM-dd HH:mm
    @SuppressLint("SimpleDateFormat")
    fun changeLongToDate(millisecond: Long): String {
        if (millisecond==0L){
            return "--"
        }
        val format = SimpleDateFormat("yyyy-MM-dd\nHH:mm:ss")
        return format.format(Date(millisecond))
    }

    //“yyyy-MM-dd” str日期转Long
    fun changeStrToLong(str: String): Long {
        val date = changeStrToDate(str)
        return changeDateToLong(date)
    }

    //Date 转 Long
    fun changeDateToLong(date: Date): Long {
        return date.time
    }

    @SuppressLint("SimpleDateFormat")
    fun changeDateToString(date: Date):String{
        val format = SimpleDateFormat(sdfPattern)
        return format.format(date)
    }

    //string类型转换为date类型
    @SuppressLint("SimpleDateFormat")
    fun changeStrToDate(str: String): Date {
        val format = SimpleDateFormat(sdfPattern)
        return format.parse(str)
    }

    //获取指定日期所在周  周一的时间
    @SuppressLint("SimpleDateFormat")
    fun getMondayByDate(dateStr:String):String{
        val format =SimpleDateFormat(sdfPattern)
        val calendar = Calendar.getInstance()
        val date =format.parse(dateStr)
        calendar.time=date!!
        val dayWeek =calendar.get(Calendar.DAY_OF_WEEK)//获得当前日期是一个星期的第几天
        //判断要计算的日期是否是周日，如果是则减一天计算周六的，否则会出问题，计算到下一周去了
        if (1 == dayWeek){
          calendar.add(Calendar.DAY_OF_WEEK,-1)
        }
        calendar.firstDayOfWeek=Calendar.MONDAY//设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
        val day = calendar.get(Calendar.DAY_OF_WEEK)
        calendar.add(Calendar.DATE,calendar.firstDayOfWeek-day)//根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
        return format.format(calendar.time)

    }

    //获取指定日期所在周  周日的时间
    @SuppressLint("SimpleDateFormat")
    fun getSunDayByDate(dateStr:String):String{
        val format =SimpleDateFormat(sdfPattern)
        val calendar = Calendar.getInstance()
        val date =format.parse(dateStr)
        calendar.time=date!!
        val dayWeek =calendar.get(Calendar.DAY_OF_WEEK)//获得当前日期是一个星期的第几天
        //判断要计算的日期是否是周日，如果是则减一天计算周六的，否则会出问题，计算到下一周去了
        if (1 == dayWeek){
            calendar.add(Calendar.DAY_OF_WEEK,-1)
        }
        calendar.firstDayOfWeek=Calendar.MONDAY//设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
        val day = calendar.get(Calendar.DAY_OF_WEEK)
        calendar.add(Calendar.DATE,calendar.firstDayOfWeek-day+6)//根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
        return format.format(calendar.time)
    }
    //获取指定日期所在月  1号的时间
    @SuppressLint("SimpleDateFormat")
    fun getFirstDayOfMonthByDate(dateStr:String):String{
        val calendar = Calendar.getInstance()
        val formatter  = SimpleDateFormat(sdfPattern)
        calendar.time = formatter.parse(dateStr)!!
        calendar.add(Calendar.MONTH,0)
        calendar.set(Calendar.DAY_OF_MONTH,1)
        return formatter.format(calendar.time)
    }
    //获取指定日期所在月  最后一天的时间
    @SuppressLint("SimpleDateFormat")
    fun getLastDayOfMonthByDate(dateStr:String):String{
        val calendar = Calendar.getInstance()
        val formatter  = SimpleDateFormat(sdfPattern)
        calendar.time = formatter.parse(dateStr)!!
        calendar.add(Calendar.MONTH,1)
        calendar.set(Calendar.DAY_OF_MONTH,0)
        return formatter.format(calendar.time)
    }

    @SuppressLint("SimpleDateFormat")
    fun getStartOfDate(dateStr:String):Long{
        val calendar = Calendar.getInstance()
        val formatter  = SimpleDateFormat(sdfPattern)
        calendar.time = formatter.parse(dateStr)!!
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        return calendar.time.time/1000
    }

    @SuppressLint("SimpleDateFormat")
    fun getEndOfDate(dateStr:String):Long{
        val calendar = Calendar.getInstance()
        val formatter  = SimpleDateFormat(sdfPattern)
        calendar.time = formatter.parse(dateStr)!!
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        return calendar.time.time/1000
    }

    /**
     * 获取当前日期前7天的日期集合
     */
    fun getDateListOfCurrentWeek():ArrayList<String>{
        val dateList = arrayListOf<String>()
        val time = System.currentTimeMillis()-7*24*3600000
        for (i in 1..7){
             val date = Date()
             date.time = (time+(i*24*3600000))
          dateList.add(changeDateToString(date))
        }
        return  dateList
    }



    /**
     * 生日年月日转为年龄
     */
    fun birthDateToAge(birthDate: String?): Int {
        if (birthDate == null || TextUtils.isEmpty(birthDate)){
            return 0
        }
        val calendar = Calendar.getInstance()
        val nowYear = calendar.get(Calendar.YEAR)
        val nowMonth = calendar.get(Calendar.MONTH) + 1
        val nowDay = calendar.get(Calendar.DAY_OF_MONTH)

        calendar.time = Date(changeStrToLong(birthDate))
        val bornYear = calendar.get(Calendar.YEAR)
        val bornMonth = calendar.get(Calendar.MONTH) + 1
        val bornDay = calendar.get(Calendar.DAY_OF_MONTH)
        var age = nowYear - bornYear
        if (nowMonth < bornMonth){
            age--
        }else if(nowMonth == bornMonth){
            if (nowDay < bornDay){
                age--
            }
        }

        return age
    }
}