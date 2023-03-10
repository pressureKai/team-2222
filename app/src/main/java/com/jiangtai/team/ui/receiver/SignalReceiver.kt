package com.jiangtai.team.ui.receiver

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.RcvData
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.jiangtai.team.application.App
import com.jiangtai.team.bean.*
import com.jiangtai.team.constant.Constant
import com.jiangtai.team.event.RefreshLocEvent
import com.jiangtai.team.event.RefreshPracticeEvent
import com.jiangtai.team.event.RefreshScoreStateEvent
import com.jiangtai.team.login.LoginActivity
import com.jiangtai.team.util.*
import org.greenrobot.eventbus.EventBus
import org.litepal.LitePal
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by heCunCun on 2021/3/12
 * 433  信息接收
 * 777579616e776569a1a2a3a4a5a6a7a8
 */
class SignalReceiver : BroadcastReceiver() {
    private var currentTaskId: String by Preference(Constant.CURRENT_TASK_ID, "")
    private var sosID: Int by Preference(Constant.SOS_ID, 0)
    private var phoneId: String by Preference(Constant.PHONE_ID, "")
    private var latCenter: Double by Preference(Constant.LAT_CENTER, 0.toDouble())
    private var lngCenter: Double by Preference(Constant.LNG_CENTER, 0.toDouble())

    @SuppressLint("WrongConstant")
    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let {
    //        ToastUtils.showShort("我收到消息了")
            try {
                val parser = it.getParcelableExtra<RcvData>("result")

                val content = parser.getrcvdata().toList()
                LogUtils.e("content is $content")
      //          ToastUtils.showShort("content is $content describeContents $describeContents")


                when (content[0]) {//命令字
                    0x51.toByte() -> {
                        //手持收到考核开始指令
                        //判断是否是可接受的手持机
                        if (currentTaskId == "") {
                        //  ToastUtils.showShort("先点击开始任务按钮")
                            return
                        }
                        val pId = HexUtil.bcd2Str(content.subList(4, 5).toByteArray())
                        Log.e("SignalReceiver", "考核开始 data phoneId=$pId,currentPhoneId =$phoneId")
                        if (pId != phoneId) {//不是目标手持不解析
                            if (phoneId == "") {
                                ToastUtils.showShort("先设置PhoneId")
                            } else {
                                //change from xiezekai
                                ToastUtils.showShort("不是目标手持机")
                            }
                            return
                        }
                        //WATCHID 表示当前考核人员ID。
                        //val watchId = HexUtil.bcd2Str(content.subList(5, 5 + 5).toByteArray())
                        val personId =
                            HexUtil.formatHexString(content.subList(5, 5 + 16).toByteArray())
                                .toUpperCase(
                                    Locale.ROOT
                                )
                        // Log.e("SignalReceiver", "watchId=$watchId")
                        Log.e("SignalReceiver", "personId=$personId")
                        //根据watchId  查人员表 改成考核中
                        val personList =
                            LitePal.where("personId = ?", personId).find(Person::class.java)
                        if (personList.isNotEmpty()) {
                            val person = personList[0]
                            //新建score记录
                            val scoreBean = Score()
                            scoreBean.personId = person.personId
                            //赋值考核项目名称
                            when (content[2]) {
                                0xE0.toByte() -> {
                                    when (content[3]) {
                                        0x11.toByte() -> {// 引体向上
                                            scoreBean.projectId = HexUtil.formatHexString(
                                                content.subList(2, 4).toByteArray(), false
                                            ).toUpperCase(Locale.ROOT)
                                            scoreBean.projectName = "引体向上"
                                        }
                                        0x12.toByte() -> {//仰卧起坐
                                            scoreBean.projectId = HexUtil.formatHexString(
                                                content.subList(2, 4).toByteArray(), false
                                            ).toUpperCase(Locale.ROOT)
                                            scoreBean.projectName = "仰卧起坐"
                                        }
                                        0x16.toByte() -> {//身高体重
                                            scoreBean.projectId = HexUtil.formatHexString(
                                                content.subList(2, 4).toByteArray(), false
                                            ).toUpperCase(Locale.ROOT)
                                            scoreBean.projectName = "身高体重"
                                        }
                                        0x27.toByte() -> {//蛇形跑
                                            scoreBean.projectId = HexUtil.formatHexString(
                                                content.subList(2, 4).toByteArray(), false
                                            ).toUpperCase(Locale.ROOT)
                                            scoreBean.projectName = "蛇形跑"
                                        }
                                        0x26.toByte() -> {//俯卧撑
                                            scoreBean.projectId = HexUtil.formatHexString(
                                                content.subList(2, 4).toByteArray(), false
                                            ).toUpperCase(Locale.ROOT)
                                            scoreBean.projectName = "俯卧撑"
                                        }
                                        else -> {
                                            //未知项目
                                        }
                                    }
                                }
                                0xE1.toByte() -> {
                                    when (content[3]) {
                                        0x00.toByte() -> {//3000M
                                            scoreBean.projectId = HexUtil.formatHexString(
                                                content.subList(2, 4).toByteArray(), false
                                            ).toUpperCase(Locale.ROOT)
                                            scoreBean.projectName = "3000M"
                                        }
                                        else -> {

                                        }
                                    }

                                }

                            }
                            //scoreBean.taskId = "1.0"
                            scoreBean.taskId = currentTaskId
                            scoreBean.state = Constant.STATE_RUNNING //考核中
                            scoreBean.startTime = System.currentTimeMillis()//开始时间

                            val find = LitePal.where(
                                "taskId like ? and projectId like ? and state like ? and personId like ?",
                                currentTaskId,
                                scoreBean.projectId,
                                Constant.STATE_RUNNING.toString(),
                                scoreBean.personId
                            ).find(Score::class.java)
                            if (find.isEmpty()) {
                                scoreBean.save()//保存数据表 刷新目标文件
                                EventBus.getDefault().post(RefreshScoreStateEvent())
                            } else {
                                Log.e("SignalReceiver", "该科目已开始考核,不再处理")
                            }


                        } else {
                            Log.e("SignalReceiver", "考核人员未录入表")
                        }


                    }
                    0x60.toByte()-> {//收到上报成绩
                        val pId = HexUtil.bcd2Str(content.subList(5, 6).toByteArray())
                        if (pId != phoneId) {
                            return
                        }

                        val projectId =
                            HexUtil.formatHexString(content.subList(3, 5).toByteArray(), false)
                                .toUpperCase(Locale.ROOT)
                        val personID =
                            HexUtil.formatHexString(content.subList(6, 6 + 16).toByteArray())
                                .toUpperCase(Locale.ROOT)

                        when (content[2]) {
                            0x01.toByte() -> {
                                //考核成绩  查询已开始的考核成绩记录
                                //根据watchId  查人员表 改成考核完成
                                val personList =
                                    LitePal.where("personId = ?", personID).find(Person::class.java)
                                if (personList.isNotEmpty()) {
                                    for (person in personList) {
                                        val personId = person.personId
                                        person.updateTime = System.currentTimeMillis()
                                        person.save()
                                        val scoreList = LitePal.where(
                                            "taskId=? and personId=? and projectId=?",
                                            currentTaskId,
                                            personId,
                                            projectId
                                        ).find(Score::class.java)
                                        if (scoreList.isNotEmpty()) {
                                            for (scoreBean in scoreList) {
                                                //     Toast.makeText(context,"scoreList size is ${scoreList.size}",Toast.LENGTH_SHORT).show()

                                                scoreBean.endTime = System.currentTimeMillis()
                                                when (projectId) {
                                                    "E016" -> {//身高体重
                                                        val height = HexUtil.bytes1ToInt2(
                                                            content.subList(6 + 16, 6 + 17)
                                                                .toByteArray(), 0
                                                        )
                                                        val weight = HexUtil.bytes1ToInt2(
                                                            content.subList(6 + 17, 6 + 18)
                                                                .toByteArray(), 0
                                                        )
                                                        scoreBean.score = "${height}cm,${weight}kg"
                                                    }
                                                    "E027" -> {
                                                        val second = HexUtil.bytes1ToInt2(
                                                            content.subList(6 + 16, 6 + 17)
                                                                .toByteArray(), 0
                                                        )
                                                        val mSecond = HexUtil.bytes1ToInt2(
                                                            content.subList(6 + 17, 6 + 18)
                                                                .toByteArray(), 0
                                                        )
                                                        scoreBean.score =
                                                            "${second}s,${mSecond * 10}ms"
                                                    }
                                                    else -> {
                                                        val value = HexUtil.bytes2ToInt2(
                                                            content.subList(6 + 16, 6 + 18)
                                                                .toByteArray(), 0
                                                        ).toString()
                                                        var unit = ""
                                                        //onReceive: >>>>>>>>>>>>>61 16 01 E1 00 02 77 75 79 61 6E 77 65 69 A1 A2 A3 A4 A5 A6 A7 B0 00 6B 10 EE
                                                        //onReceive: >>>>>>>>>>>>>61 16 01 E1 00 02 77 75 79 61 6E 77 65 69 A1 A2 A3 A4 A5 A6 A7 B0 00 F1 10 74
                                                        when (projectId) {
                                                            "E011", "E012", "E026" -> {
                                                                //change from xiezekai
                                                                var show = ""
                                                                when (projectId) {
                                                                    "E011" -> {
                                                                        //引体向上
                                                                        show = "引体向上项目"
                                                                    }
                                                                    "E012" -> {
                                                                        //仰卧起坐
                                                                        show = "仰卧起坐项目"
                                                                    }
                                                                    "E026" -> {
                                                                        //俯卧撑
                                                                        show = "俯卧撑项目"
                                                                    }
                                                                }
                                                                show += ",收到数据为 ${value}次"
                                                                //       Toast.makeText(context,show,Toast.LENGTH_SHORT).show()
                                                                scoreBean.score = "${value}次"
                                                            }
                                                            "E100" -> {
                                                                val min = value.toInt() / 60
                                                                val second = value.toInt() % 60
                                                                scoreBean.score =
                                                                    "${min}分${second}秒"
                                                            }
                                                            else -> {
                                                            }
                                                        }
                                                    }
                                                }
                                                scoreBean.state = Constant.STATE_FINISHED
                                                scoreBean.save()
                                                EventBus.getDefault().post(RefreshScoreStateEvent())
                                            }

                                        } else {
                                            Log.e("SignalReceiver", "成绩表查不到此考核成绩开始记录")
                                            ToastUtils.showShort("成绩表查不到${personList[0].name}考核成绩开始记录")
                                        }
                                    }
                                } else {
                                    Log.e("SignalReceiver", "watchId  查人员表不存在此人")
                                }

                            }
                            0x02.toByte() -> {
                                //计划成绩
                                val normalPersonList =
                                    LitePal.where("personId=?", personID.toUpperCase(Locale.ROOT))
                                        .find(NormalPerson::class.java)
                                if (normalPersonList.isNotEmpty()) {
                                    val personId = normalPersonList[0].personId
                                    val normalScoreList =
                                        LitePal.where(
                                            "personId=? and projectId=?",
                                            personId,
                                            projectId
                                        )
                                            .find(NormalScore::class.java)
                                    if (normalScoreList.isNotEmpty()) {
                                        val normalScoreBean = normalScoreList.last()
                                        normalScoreBean.uploadTime =
                                            System.currentTimeMillis()//当前结束时间
                                        when (projectId) {
                                            "E016" -> {//身高体重
                                                val height = HexUtil.bytes1ToInt2(
                                                    content.subList(6 + 16, 6 + 17).toByteArray(), 0
                                                )
                                                val weight = HexUtil.bytes1ToInt2(
                                                    content.subList(6 + 17, 6 + 18).toByteArray(), 0
                                                )
                                                normalScoreBean.score = "${height}cm,${weight}kg"
                                            }
                                            "E027" -> {
                                                val second = HexUtil.bytes1ToInt2(
                                                    content.subList(6 + 16, 6 + 17).toByteArray(), 0
                                                )
                                                val mSecond = HexUtil.bytes1ToInt2(
                                                    content.subList(6 + 17, 6 + 18).toByteArray(), 0
                                                )
                                                normalScoreBean.score =
                                                    "${second}s,${mSecond * 10}ms"
                                            }
                                            else -> {
                                                val value = HexUtil.bytes2ToInt2(
                                                    content.subList(6 + 16, 6 + 18).toByteArray(), 0
                                                ).toString()

                                                when (projectId) {
                                                    "E011", "E012", "E026" -> {
                                                        normalScoreBean.score = "${value}次"
                                                    }
                                                    "E100" -> {
                                                        val min = value.toInt() / 60
                                                        val second = value.toInt() % 60
                                                        normalScoreBean.score = "${min}分${second}秒"
                                                    }
                                                    else -> {
                                                    }
                                                }
                                            }
                                        }
                                        normalScoreBean.save()
                                        //刷新计划人员表和成绩表
                                        EventBus.getDefault().post(RefreshPracticeEvent())
                                    } else {
                                        Log.e("SignalReceiver", "计划表  查不到此人开始计划成绩记录")
                                    }
                                } else {
                                    Log.e("SignalReceiver", "计划表  查不到此人")
                                }

                            }
                            else -> {
                            }
                        }

                    }
                    0x61.toByte() -> {
                        if (content.size == 25) {
                            // 射击次数上报
                            LogUtils.e("0x61 原始数据为${content}")
                            // pId 在原始数据的第4位
                            val pId = HexUtil.bcd2Str(content.subList(2, 3).toByteArray())
                            if (pId != phoneId) {
                                //                         ToastUtils.showShort("射击次数上报回调，手持机ID不符 $pId")
                                return
                            } else {
                                // 获取装备信标 5 ~ (5 + 16)
                                val userId =
                                    HexUtil.formatHexString(content.subList(3, 19).toByteArray())
                                        .toUpperCase()
                                LogUtils.e("获取到信标ID为 $userId")

                                // Num 射击数量
                                val number =
                                    HexUtil.bytes1ToInt2(content.subList(19, 21).toByteArray(), 0)
                                LogUtils.e("射击数量为 $number")

                                // Sum 累计射击次数
                                val sum =
                                    HexUtil.bytes1ToInt2(content.subList(21, 23).toByteArray(), 0)
                                LogUtils.e("累计射击次数为 $sum")
                            }
                        } else {
                            val pId = HexUtil.bcd2Str(content.subList(5, 6).toByteArray())
                            if (pId != phoneId) {
                                return
                            }

                            val projectId =
                                HexUtil.formatHexString(content.subList(3, 5).toByteArray(), false)
                                    .toUpperCase(Locale.ROOT)
                            val personID =
                                HexUtil.formatHexString(content.subList(6, 6 + 16).toByteArray())
                                    .toUpperCase(Locale.ROOT)

                            when (content[2]) {
                                0x01.toByte() -> {
                                    //考核成绩  查询已开始的考核成绩记录
                                    //根据watchId  查人员表 改成考核完成
                                    val personList =
                                        LitePal.where("personId = ?", personID)
                                            .find(Person::class.java)
                                    if (personList.isNotEmpty()) {
                                        for (person in personList) {
                                            val personId = person.personId
                                            person.updateTime = System.currentTimeMillis()
                                            person.save()
                                            val scoreList = LitePal.where(
                                                "taskId=? and personId=? and projectId=?",
                                                currentTaskId,
                                                personId,
                                                projectId
                                            ).find(Score::class.java)
                                            if (scoreList.isNotEmpty()) {
                                                for (scoreBean in scoreList) {
                                                    //     Toast.makeText(context,"scoreList size is ${scoreList.size}",Toast.LENGTH_SHORT).show()

                                                    scoreBean.endTime = System.currentTimeMillis()
                                                    when (projectId) {
                                                        "E016" -> {//身高体重
                                                            val height = HexUtil.bytes1ToInt2(
                                                                content.subList(6 + 16, 6 + 17)
                                                                    .toByteArray(), 0
                                                            )
                                                            val weight = HexUtil.bytes1ToInt2(
                                                                content.subList(6 + 17, 6 + 18)
                                                                    .toByteArray(), 0
                                                            )
                                                            scoreBean.score =
                                                                "${height}cm,${weight}kg"
                                                        }
                                                        "E027" -> {
                                                            val second = HexUtil.bytes1ToInt2(
                                                                content.subList(6 + 16, 6 + 17)
                                                                    .toByteArray(), 0
                                                            )
                                                            val mSecond = HexUtil.bytes1ToInt2(
                                                                content.subList(6 + 17, 6 + 18)
                                                                    .toByteArray(), 0
                                                            )
                                                            scoreBean.score =
                                                                "${second}s,${mSecond * 10}ms"
                                                        }
                                                        else -> {
                                                            val value = HexUtil.bytes2ToInt2(
                                                                content.subList(6 + 16, 6 + 18)
                                                                    .toByteArray(), 0
                                                            ).toString()
                                                            var unit = ""
                                                            //onReceive: >>>>>>>>>>>>>61 16 01 E1 00 02 77 75 79 61 6E 77 65 69 A1 A2 A3 A4 A5 A6 A7 B0 00 6B 10 EE
                                                            //onReceive: >>>>>>>>>>>>>61 16 01 E1 00 02 77 75 79 61 6E 77 65 69 A1 A2 A3 A4 A5 A6 A7 B0 00 F1 10 74
                                                            when (projectId) {
                                                                "E011", "E012", "E026" -> {
                                                                    //change from xiezekai
                                                                    var show = ""
                                                                    when (projectId) {
                                                                        "E011" -> {
                                                                            //引体向上
                                                                            show = "引体向上项目"
                                                                        }
                                                                        "E012" -> {
                                                                            //仰卧起坐
                                                                            show = "仰卧起坐项目"
                                                                        }
                                                                        "E026" -> {
                                                                            //俯卧撑
                                                                            show = "俯卧撑项目"
                                                                        }
                                                                    }
                                                                    show += ",收到数据为 ${value}次"
                                                                    //       Toast.makeText(context,show,Toast.LENGTH_SHORT).show()
                                                                    scoreBean.score = "${value}次"
                                                                }
                                                                "E100" -> {
                                                                    val min = value.toInt() / 60
                                                                    val second = value.toInt() % 60
                                                                    scoreBean.score =
                                                                        "${min}分${second}秒"
                                                                }
                                                                else -> {
                                                                }
                                                            }
                                                        }
                                                    }
                                                    scoreBean.state = Constant.STATE_FINISHED
                                                    scoreBean.save()
                                                    EventBus.getDefault()
                                                        .post(RefreshScoreStateEvent())
                                                }

                                            } else {
                                                Log.e("SignalReceiver", "成绩表查不到此考核成绩开始记录")
                                                ToastUtils.showShort("成绩表查不到${personList[0].name}考核成绩开始记录")
                                            }
                                        }
                                    } else {
                                        Log.e("SignalReceiver", "watchId  查人员表不存在此人")
                                    }

                                }
                                0x02.toByte() -> {
                                    //训练成绩
                                    val normalPersonList =
                                        LitePal.where(
                                            "personId=?",
                                            personID.toUpperCase(Locale.ROOT)
                                        )
                                            .find(NormalPerson::class.java)
                                    if (normalPersonList.isNotEmpty()) {
                                        val personId = normalPersonList[0].personId
                                        val normalScoreList =
                                            LitePal.where(
                                                "personId=? and projectId=?",
                                                personId,
                                                projectId
                                            )
                                                .find(NormalScore::class.java)
                                        if (normalScoreList.isNotEmpty()) {
                                            val normalScoreBean = normalScoreList.last()
                                            normalScoreBean.uploadTime =
                                                System.currentTimeMillis()//当前结束时间
                                            when (projectId) {
                                                "E016" -> {//身高体重
                                                    val height = HexUtil.bytes1ToInt2(
                                                        content.subList(6 + 16, 6 + 17)
                                                            .toByteArray(), 0
                                                    )
                                                    val weight = HexUtil.bytes1ToInt2(
                                                        content.subList(6 + 17, 6 + 18)
                                                            .toByteArray(), 0
                                                    )
                                                    normalScoreBean.score =
                                                        "${height}cm,${weight}kg"
                                                }
                                                "E027" -> {
                                                    val second = HexUtil.bytes1ToInt2(
                                                        content.subList(6 + 16, 6 + 17)
                                                            .toByteArray(), 0
                                                    )
                                                    val mSecond = HexUtil.bytes1ToInt2(
                                                        content.subList(6 + 17, 6 + 18)
                                                            .toByteArray(), 0
                                                    )
                                                    normalScoreBean.score =
                                                        "${second}s,${mSecond * 10}ms"
                                                }
                                                else -> {
                                                    val value = HexUtil.bytes2ToInt2(
                                                        content.subList(6 + 16, 6 + 18)
                                                            .toByteArray(), 0
                                                    ).toString()

                                                    when (projectId) {
                                                        "E011", "E012", "E026" -> {
                                                            normalScoreBean.score = "${value}次"
                                                        }
                                                        "E100" -> {
                                                            val min = value.toInt() / 60
                                                            val second = value.toInt() % 60
                                                            normalScoreBean.score =
                                                                "${min}分${second}秒"
                                                        }
                                                        else -> {
                                                        }
                                                    }
                                                }
                                            }
                                            normalScoreBean.save()
                                            //刷新训练人员表和成绩表
                                            EventBus.getDefault().post(RefreshPracticeEvent())
                                        } else {
                                            Log.e("SignalReceiver", "训练表  查不到此人开始训练成绩记录")
                                        }
                                    } else {
                                        Log.e("SignalReceiver", "训练表  查不到此人")
                                    }

                                }
                                else -> {
                                }
                            }

                        }


                    }
                    0x52.toByte() -> {//
                        val pId = HexUtil.bcd2Str(content.subList(5, 6).toByteArray())
                        if (pId != phoneId) {//不是目标手持不解析
                            return
                        }
                        when (content[2]) {
                            0x01.toByte() -> {
                                //考核开始
                            }
                            0x02.toByte() -> {//计划开始l
                                val normalPerson: NormalPerson
                                val projectId =
                                    HexUtil.formatHexString(
                                        content.subList(3, 5).toByteArray(),
                                        false
                                    )
                                        .toUpperCase(Locale.ROOT)
                                val userId = HexUtil.formatHexString(
                                    content.subList(6, 6 + 16).toByteArray(), false
                                ).toUpperCase(Locale.ROOT)
                                val normalPersonList =
                                    LitePal.where("personId=?", userId)
                                        .find(NormalPerson::class.java)
                                if (normalPersonList.isEmpty()) {
                                    //WATCHID 表示当前考核人员ID。
                                    val name = String(
                                        content.subList(6 + 16, 6 + 16 + 11).toByteArray(),
                                        Charset.forName("GB2312")
                                    )
                                    normalPerson = NormalPerson(name, userId)
                                    normalPerson.save()
                                } else {
                                    normalPerson = normalPersonList[0]
                                }

                                //新建计划记录
                                val normalScore = NormalScore()
                                normalScore.personId = normalPerson.personId
                                normalScore.projectId = projectId
                                var projectName = ""
                                when (projectId) {
                                    "E011" -> {
                                        projectName = "引体向上"
                                    }
                                    "E012" -> {
                                        projectName = "仰卧起坐"
                                    }
                                    "E016" -> {
                                        projectName = "身高体重"
                                    }
                                    "E027" -> {
                                        projectName = "蛇形跑"
                                    }
                                    "E026" -> {
                                        projectName = "俯卧撑"
                                    }
                                    "E100" -> {
                                        projectName = "3000M"
                                    }
                                    else -> {
                                    }
                                }
                                normalScore.projectName = projectName
                                normalScore.startTime = System.currentTimeMillis()
                                normalScore.save()
                                //刷新计划人员表和成绩表
                                EventBus.getDefault().post(RefreshPracticeEvent())
                            }
                            else -> {
                            }
                        }

                    }
                    0x55.toByte() -> {//腕表3公里考核上报定位信息
                        val pId = HexUtil.bcd2Str(content.subList(4, 5).toByteArray())
                        Log.e(
                            "SignalReceiver",
                            "定位信息上报开始 data phoneId=$pId,currentPhoneId =$phoneId"
                        )
                        if (pId != phoneId) {//不是目标手持不解析
                            return
                        }
                        val projectId = HexUtil.formatHexString(content.subList(2, 4).toByteArray())
                            .toUpperCase(Locale.ROOT)
                        val personID =
                            HexUtil.formatHexString(content.subList(5, 5 + 16).toByteArray())
                                .toUpperCase(Locale.ROOT)
                        //根据personID 经纬度表
                        val heartRate =
                            HexUtil.bytes1ToInt2(content.subList(5 + 16, 5 + 17).toByteArray(), 0)
                        val lng =
                            HexUtil.bytesToInt2(
                                content.subList(5 + 17, 5 + 17 + 4).toByteArray(),
                                0
                            )
                        val lat = HexUtil.bytesToInt2(
                            content.subList(5 + 17 + 4, 5 + 17 + 4 + 4).toByteArray(), 0
                        )
                        //经\纬度：长度4字节，小数点左移7位可得到真实值，正数表示东经，北纬；负数表示西经
                        Log.e(
                            "SignalReceiver",
                            "personID=$personID,heartRate=$heartRate,lat = ${lat / 10000000f},lng =${lng / 10000000f}"
                        )

                        val d = lat / 10000000.0
                        val d1 = lng / 10000000.0
                        if (d != 0.toDouble() && d1 != 0.toDouble()) {
                            val locationInfoBean = LocationInfoBean(
                                d,
                                d1,
                                heartRate,
                                personID,
                                projectId,
                                System.currentTimeMillis(),
                                currentTaskId
                            )
                            locationInfoBean.save()
                        }
                        EventBus.getDefault().post(RefreshLocEvent())


                    }
//                    0x1A.toByte() -> {
////                    App.getMineContext()?.let {
////                        NotificationHelper(it).showNotification(5,"sos")
////                    }
//                    }
                    0x56.toByte() -> {
                        Log.e("SignalReceiver", "原始数据为${content}")
                        val pId = HexUtil.bcd2Str(content.subList(4, 5).toByteArray())
                        Log.e(
                            "SignalReceiver",
                            "收到sos信息 data phoneId=$pId,currentPhoneId =$phoneId"
                        )
                        if (pId != phoneId) {//不是目标手持不解析
                            return
                        }
                        val personID =
                            HexUtil.formatHexString(content.subList(5, 5 + 16).toByteArray())
                                .toUpperCase(Locale.ROOT)
                        //根据personID 经纬度表
                        val heartRate =
                            HexUtil.bytes1ToInt2(content.subList(5 + 16, 5 + 17).toByteArray(), 0)
                        val lng =
                            HexUtil.bytesToInt2(
                                content.subList(5 + 17, 5 + 17 + 4).toByteArray(),
                                0
                            )
                        val lat = HexUtil.bytesToInt2(
                            content.subList(5 + 17 + 4, 5 + 17 + 4 + 4).toByteArray(), 0
                        )
                        //经\纬度：长度4字节，小数点左移7位可得到真实值，正数表示东经，北纬；负数表示西经
                        Log.e(
                            "SignalReceiver sos",
                            "personID=$personID,heartRate=$heartRate,lat = ${lat / 10000000f},lng =${lng / 10000000f}"
                        )


                        val sosBean = SosBean(
                            personID,
                            (lat / 10000000f).toString(),
                            (lng / 10000000f).toString()
                        )
                        sosBean.save()
                        val find = LitePal.where("personId = ?", personID).find(Person::class.java)

                        if (find.isNotEmpty()) {
                            try {
                                Handler(Looper.getMainLooper()).post {
                                    try {
                                        App.getMineContext()?.let {
                                            NotificationHelper(it).showNotification(
                                                sosID,
                                                find[0].name,
                                                (lat / 10000000f).toString(),
                                                (lng / 10000000f).toString()
                                            )
                                        }


                                        //从列表排序，传数值下标

                                        val intent1 = Intent("android.intent.action.BDSEND")
                                        intent1.putExtra("lat", (lat / 10000000f).toDouble())//纬度
                                        intent1.putExtra("lon", (lng / 10000000f).toDouble())//经度
                                        intent1.putExtra("offset", 1) //腕表唯一标识码对应的数组下标没有传输暂传1
                                        intent1.putExtra("time", System.currentTimeMillis())//当前时间
                                        intent1.putExtra(
                                            "handsetlat",
                                            (lat / 10000000f).toDouble()
                                        )//纬度
                                        intent1.putExtra(
                                            "handsetlon",
                                            (lng / 10000000f).toDouble()
                                        )//经度
                                        intent1.putExtra(
                                            "handsetTime",
                                            System.currentTimeMillis()
                                        )//当前时间
                                        intent1.putExtra("num", "0322773")//北斗号

                                        if (Build.VERSION.SDK_INT >= 26) {
                                            intent1.addFlags(0x01000000)
                                        }
                                        App.getMineContext()?.sendBroadcast(intent1)
                                    } catch (e: Exception) {

                                    }
                                }
                            } catch (e: Exception) {

                            }

                            sosID += 1

                        } else {
                            return
                        }

                    }

                    0x62.toByte() -> {
                        if(content.size == 35){
                            //专项计划和出车回场，成绩上报
                            LogUtils.e("0x62 原始数据为${content}")

                            var isTraining = false
                            val packageName = context?.packageName
                            packageName?.let { packageName ->
                                isTraining = packageName.contains("training")
                            }
                            val message = if (isTraining) "专项计划" else "出车回场"
                            val pId = HexUtil.bcd2Str(content.subList(2, 3).toByteArray())
                            if (pId != phoneId) {
                                return
                            } else {
                                //用户ID
                                val userId =
                                    HexUtil.formatHexString(content.subList(3, 19).toByteArray())
                                        .toUpperCase()
                                LogUtils.e("${message}用户ID为 $userId")


                                //taskID (计划ID)
                                val taskID =
                                    HexUtil.bytes1ToInt2(content.subList(19, 20).toByteArray(), 0)
                                LogUtils.e("${message}计划ID为 $taskID")

                                //任务ID
                                val projectID =
                                    HexUtil.bytes1ToInt2(content.subList(20, 21).toByteArray(), 0)
                                LogUtils.e("${message}任务ID为 $projectID")

                                //startTime
                                val subList = content.subList(
                                    21, 27
                                )
                                val timeByCharArray =
                                    CommonUtil.getTimeByCharArray(subList)
                                LogUtils.e("${message}开始时间为 $timeByCharArray")

                                //endTime

                                val timeByteArray = content.subList(27, 33)
                                val timeByCharArray1 =
                                    CommonUtil.getTimeByCharArray(timeByteArray)
                                LogUtils.e("${message}结束时间为 $timeByCharArray1")




                                val uploadMajorScoreData = UploadMajorScoreData()
                                uploadMajorScoreData.startTime = timeByCharArray.toString()
                                uploadMajorScoreData.endTime = timeByCharArray1.toString()
                                uploadMajorScoreData.personId = userId
                                uploadMajorScoreData.projectId = projectID.toString()
                                uploadMajorScoreData.taskId = taskID.toString()
                                uploadMajorScoreData.save()

                                EventBus.getDefault().postSticky(uploadMajorScoreData)

                            }
                        } else {
                            val pId = HexUtil.bcd2Str(content.subList(5, 6).toByteArray())
                            if (pId != phoneId) {
                                return
                            }

                            //获取项目Id
                            val projectId =
                                HexUtil.formatHexString(content.subList(3, 5).toByteArray(), false)
                                    .toUpperCase(Locale.ROOT)


                            val size = content.size

                            val count = (size - 7) / 18


                            for (value in 0.until(count)) {
                                val startIndex = 7 + (value * 18)

                                val personID = HexUtil.formatHexString(
                                    content.subList(startIndex, startIndex + 16).toByteArray()
                                )
                                    .toUpperCase(Locale.ROOT)



                                when (content[2]) {
                                    0x01.toByte() -> {
                                        val personList =
                                            LitePal.where("personId = ?", personID)
                                                .find(Person::class.java)


                                        if (personList.isNotEmpty()) {
                                            for (person in personList) {
                                                val personId = person.personId
                                                person.updateTime = System.currentTimeMillis()
                                                person.save()
                                                val scoreList = LitePal.where(
                                                    "taskId=? and personId=? and projectId=?",
                                                    currentTaskId,
                                                    personId,
                                                    projectId
                                                ).find(Score::class.java)


                                                if (scoreList.isNotEmpty()) {
                                                    for (scoreBean in scoreList) {
                                                        scoreBean.endTime = System.currentTimeMillis()
                                                        when (projectId) {
                                                            "E016" -> {//身高体重
                                                                val height = HexUtil.bytes1ToInt2(
                                                                    content.subList(
                                                                        startIndex + 16,
                                                                        startIndex + 17
                                                                    )
                                                                        .toByteArray(), 0
                                                                )
                                                                val weight = HexUtil.bytes1ToInt2(
                                                                    content.subList(
                                                                        startIndex + 17,
                                                                        startIndex + 18
                                                                    )
                                                                        .toByteArray(), 0
                                                                )
                                                                scoreBean.score =
                                                                    "${height}cm,${weight}kg"
                                                            }
                                                            "E027" -> {
                                                                val second = HexUtil.bytes1ToInt2(
                                                                    content.subList(
                                                                        startIndex + 16,
                                                                        startIndex + 17
                                                                    )
                                                                        .toByteArray(), 0
                                                                )
                                                                val mSecond = HexUtil.bytes1ToInt2(
                                                                    content.subList(
                                                                        startIndex + 17,
                                                                        startIndex + 18
                                                                    )
                                                                        .toByteArray(), 0
                                                                )
                                                                scoreBean.score =
                                                                    "${second}s,${mSecond * 10}ms"
                                                            }
                                                            else -> {
                                                                val value = HexUtil.bytes2ToInt2(
                                                                    content.subList(
                                                                        startIndex + 16,
                                                                        startIndex + 18
                                                                    )
                                                                        .toByteArray(), 0
                                                                ).toString()
                                                                when (projectId) {
                                                                    "E011", "E012", "E026" -> {
                                                                        //change from xiezekai
                                                                        var show = ""
                                                                        when (projectId) {
                                                                            "E011" -> {
                                                                                //引体向上
                                                                                show = "引体向上项目"
                                                                            }
                                                                            "E012" -> {
                                                                                //仰卧起坐
                                                                                show = "仰卧起坐项目"
                                                                            }
                                                                            "E026" -> {
                                                                                //俯卧撑
                                                                                show = "俯卧撑项目"
                                                                            }
                                                                        }
                                                                        show += ",收到数据为 ${value}次"
                                                                        scoreBean.score = "${value}次"
                                                                    }
                                                                    "E100" -> {
                                                                        val min = value.toInt() / 60
                                                                        val second = value.toInt() % 60
                                                                        scoreBean.score =
                                                                            "${min}分${second}秒"
                                                                    }
                                                                    else -> {

                                                                    }
                                                                }
                                                            }
                                                        }
                                                        scoreBean.state = Constant.STATE_FINISHED
                                                        scoreBean.save()
                                                        EventBus.getDefault()
                                                            .post(RefreshScoreStateEvent())
                                                    }
                                                } else {
                                                    Log.e("SignalReceiver", "成绩表查不到此考核成绩开始记录")
                                                    ToastUtils.showShort("成绩表查不到${personList[0].name}考核成绩开始记录")
                                                }
                                            }
                                        } else {
                                            Log.e("SignalReceiver", "watchId  查人员表不存在此人")
                                        }

                                    }
                                    0x02.toByte() -> {
                                        //计划成绩
                                        val normalPersonList =
                                            LitePal.where(
                                                "personId=?",
                                                personID.toUpperCase(Locale.ROOT)
                                            )
                                                .find(NormalPerson::class.java)
                                        if (normalPersonList.isNotEmpty()) {
                                            val personId = normalPersonList[0].personId
                                            val normalScoreList =
                                                LitePal.where(
                                                    "personId=? and projectId=?",
                                                    personId,
                                                    projectId
                                                )
                                                    .find(NormalScore::class.java)
                                            if (normalScoreList.isNotEmpty()) {
                                                val normalScoreBean = normalScoreList.last()
                                                normalScoreBean.uploadTime =
                                                    System.currentTimeMillis()//当前结束时间
                                                when (projectId) {
                                                    "E016" -> {//身高体重
                                                        val height = HexUtil.bytes1ToInt2(
                                                            content.subList(6 + 16, 6 + 17)
                                                                .toByteArray(), 0
                                                        )
                                                        val weight = HexUtil.bytes1ToInt2(
                                                            content.subList(6 + 17, 6 + 18)
                                                                .toByteArray(), 0
                                                        )
                                                        normalScoreBean.score =
                                                            "${height}cm,${weight}kg"
                                                    }
                                                    "E027" -> {
                                                        val second = HexUtil.bytes1ToInt2(
                                                            content.subList(6 + 16, 6 + 17)
                                                                .toByteArray(), 0
                                                        )
                                                        val mSecond = HexUtil.bytes1ToInt2(
                                                            content.subList(6 + 17, 6 + 18)
                                                                .toByteArray(), 0
                                                        )
                                                        normalScoreBean.score =
                                                            "${second}s,${mSecond * 10}ms"
                                                    }
                                                    else -> {
                                                        val value = HexUtil.bytes2ToInt2(
                                                            content.subList(6 + 16, 6 + 18)
                                                                .toByteArray(), 0
                                                        ).toString()

                                                        when (projectId) {
                                                            "E011", "E012", "E026" -> {
                                                                normalScoreBean.score = "${value}次"
                                                            }
                                                            "E100" -> {
                                                                val min = value.toInt() / 60
                                                                val second = value.toInt() % 60
                                                                normalScoreBean.score =
                                                                    "${min}分${second}秒"
                                                            }
                                                            else -> {
                                                            }
                                                        }
                                                    }
                                                }
                                                normalScoreBean.save()
                                                //刷新计划人员表和成绩表
                                                EventBus.getDefault().post(RefreshPracticeEvent())
                                            } else {
                                                Log.e("SignalReceiver", "计划表  查不到此人开始计划成绩记录")
                                            }
                                        } else {
                                            Log.e("SignalReceiver", "计划表  查不到此人")
                                        }

                                    }
                                    else -> {
                                    }
                                }
                            }
                        }


                    }


                    0x67.toByte() ->{
                        LogUtils.e("0x67 content is  $content")
                        val pId = HexUtil.bcd2Str(content.subList(2, 3).toByteArray())
                        LogUtils.e("0x67 pId is  $pId  phoneID $phoneId")
                        if (pId == phoneId) {
                            //不是目标手持不解析
                            val taskId = HexUtil.bcd2Str(content.subList(3, 4).toByteArray())
                            val personId = HexUtil.formatHexString(
                                content.subList(4, 4 + 16).toByteArray()
                            ).toUpperCase(
                                    Locale.ROOT
                                )
                            LogUtils.e("0x67 taskId $taskId personId ${personId}")


                            val signInEntity = SignInEntity()
                            signInEntity.personId = personId
                            signInEntity.taskId = taskId
                            EventBus.getDefault().postSticky(signInEntity)
                        } else {
                            LogUtils.e("0x67 不是目标手持不解析")
                        }
                    }

                    0x57.toByte() -> {
                        Log.e("SingleReceiver", "content is $content")
                        //手持收到考核开始指令
                        //判断是否是可接受的手持机
                        if (currentTaskId == "") {
                        //    ToastUtils.showShort("先点击开始任务按钮")
                            return
                        }

                        try {
                            //判断phoneId  相比之前不变
                            val pId = HexUtil.bcd2Str(content.subList(4, 5).toByteArray())
                            Log.e(
                                "SignalReceiver",
                                "考核开始 data phoneId=$pId,currentPhoneId =$phoneId"
                            )
                            if (pId != phoneId) {//不是目标手持不解析
                                if (phoneId == "") {
                                    ToastUtils.showShort("先设置PhoneId")
                                } else {
                                    ToastUtils.showShort("不是目标手持机")
                                }
                                return
                            }


                            val i = (content.size - 6) / 16


                            for (value in 0.until(i)) {
                                val startIndex = 6 + (value * 16)
                                val personId = HexUtil.formatHexString(
                                    content.subList(startIndex, startIndex + 16).toByteArray()
                                )
                                    .toUpperCase(
                                        Locale.ROOT
                                    )
                                //根据watchId  查人员表 改成考核中
                                val personList =
                                    LitePal.where("personId = ?", personId).find(Person::class.java)
                                if (personList.isNotEmpty()) {
                                    val person = personList[0]
                                    //新建score记录
                                    val scoreBean = Score()
                                    scoreBean.personId = person.personId
                                    //赋值考核项目名称
                                    when (content[2]) {
                                        0xE0.toByte() -> {
                                            when (content[3]) {
                                                0x11.toByte() -> {// 引体向上
                                                    scoreBean.projectId = HexUtil.formatHexString(
                                                        content.subList(2, 4).toByteArray(), false
                                                    ).toUpperCase(Locale.ROOT)
                                                    scoreBean.projectName = "引体向上"
                                                }
                                                0x12.toByte() -> {//仰卧起坐
                                                    scoreBean.projectId = HexUtil.formatHexString(
                                                        content.subList(2, 4).toByteArray(), false
                                                    ).toUpperCase(Locale.ROOT)
                                                    scoreBean.projectName = "仰卧起坐"
                                                }
                                                0x16.toByte() -> {//身高体重
                                                    scoreBean.projectId = HexUtil.formatHexString(
                                                        content.subList(2, 4).toByteArray(), false
                                                    ).toUpperCase(Locale.ROOT)
                                                    scoreBean.projectName = "身高体重"
                                                }
                                                0x27.toByte() -> {//蛇形跑
                                                    scoreBean.projectId = HexUtil.formatHexString(
                                                        content.subList(2, 4).toByteArray(), false
                                                    ).toUpperCase(Locale.ROOT)
                                                    scoreBean.projectName = "蛇形跑"
                                                }
                                                0x26.toByte() -> {//俯卧撑
                                                    scoreBean.projectId = HexUtil.formatHexString(
                                                        content.subList(2, 4).toByteArray(), false
                                                    ).toUpperCase(Locale.ROOT)
                                                    scoreBean.projectName = "俯卧撑"
                                                }
                                                else -> {
                                                    //未知项目
                                                }
                                            }
                                        }
                                        0xE1.toByte() -> {
                                            when (content[3]) {
                                                0x00.toByte() -> {//3000M
                                                    scoreBean.projectId = HexUtil.formatHexString(
                                                        content.subList(2, 4).toByteArray(), false
                                                    ).toUpperCase(Locale.ROOT)
                                                    scoreBean.projectName = "3000M"
                                                }
                                                else -> {

                                                }
                                            }
                                        }

                                    }
                                    //scoreBean.taskId = "1.0"
                                    scoreBean.taskId = currentTaskId
                                    scoreBean.state = Constant.STATE_RUNNING //考核中
                                    scoreBean.startTime = System.currentTimeMillis()//开始时间

                                    val find = LitePal.where(
                                        "taskId like ? and projectId like ? and state like ? and personId like ?",
                                        currentTaskId,
                                        scoreBean.projectId,
                                        Constant.STATE_RUNNING.toString(),
                                        scoreBean.personId
                                    ).find(Score::class.java)
                                    if (find.isEmpty()) {
                                        scoreBean.save()//保存数据表 刷新目标文件
                                        EventBus.getDefault().post(RefreshScoreStateEvent())
                                    } else {
                                        Log.e("SignalReceiver", "该科目已开始考核,不再处理")
                                    }
                                } else {
                                    Log.e("SignalReceiver", "考核人员未录入表")
                                }

                            }

                        } catch (e: Exception) {
//                        Handler(Looper.getMainLooper()).post {
//                            Log.e("SingleReceiver","错误是$e")
//                            ToastUtils.showShort("错误是$e")
//                        }
                        }
                    }




                    0x64.toByte() -> {
                        //搜救指令上报
                        val message = "搜救指令上报"
                        LogUtils.e("0x64 原始数据为${content}")
                        val pId = HexUtil.bcd2Str(content.subList(2, 3).toByteArray())
                        LogUtils.e("0x64 原始数据为 pId${pId} phoneId${phoneId}")
                        if (pId != phoneId) {
                            //   ToastUtils.showShort("${message}回调手持机ID不符 $pId")
                            return
                        } else {
                            //用户ID
                            val userId =
                                HexUtil.formatHexString(content.subList(3, 19).toByteArray())
                                    .toUpperCase()

                            LogUtils.e("${message}用户ID为 $userId")
    //                        ToastUtils.showShort("${message}用户ID为 $userId")

                            //响应类型
                            val responseType =
                                HexUtil.formatHexString(content.subList(19, 20).toByteArray())
                                    .toUpperCase()

                            if (responseType == "0") {
                                //主动发送
  //                              ToastUtils.showShort("${message}响应类型为 $responseType 主动发送")
                            } else {
                                //被动发送
    //                            ToastUtils.showShort("${message}响应类型为 $responseType 被动发送")
                            }


                            //经度
                            val lng =
                                HexUtil.bytesToInt2(content.subList(20, 24).toByteArray(), 0)
    //                        ToastUtils.showShort("${message}经度为 $lng")
                            LogUtils.e("${message}经度为 $lng")

                            //纬度
                            val lat = HexUtil.bytesToInt2(
                                content.subList(24, 28).toByteArray(), 0
                            )

                            //心率
                            val heartRate =
                                HexUtil.bytes1ToInt2(content.subList(28, 29).toByteArray(), 0)


                            val locationReceiver = LocationReceiver()
                            locationReceiver.userId = userId
                            val wgs84togcj03 = CommonUtil.wgs84_To_Gcj02(
                                lat / 10000000.toDouble(),
                                lng / 10000000.toDouble()
                            )
                            wgs84togcj03?.let {
                                locationReceiver.lng =
                                    wgs84togcj03.longitude.toString()
                                locationReceiver.lat =
                                    wgs84togcj03.latitude.toString()
                            }

                            locationReceiver.isSOS = true
                            locationReceiver.projectId = currentTaskId
                            locationReceiver.sosType = responseType
                            locationReceiver.heartRate = heartRate.toString()
                            locationReceiver.time = System.currentTimeMillis().toString()

                            val sosBean = SosBean()
                            wgs84togcj03?.let {

                                sosBean.lng =
                                    wgs84togcj03.longitude.toString()
                                sosBean.lat =
                                    wgs84togcj03.latitude.toString()
                            }

                            sosBean.heartRate = heartRate.toString()
                            sosBean.userId = userId
                            EventBus.getDefault().postSticky(sosBean)

                            locationReceiver.save()
                            EventBus.getDefault().postSticky(locationReceiver)



                            val uploadDataBean = UploadDataBean()
                            uploadDataBean.locationReceiver = locationReceiver
                            EventBus.getDefault().postSticky(uploadDataBean)

                            val beiDouServiceBean = BeiDouServiceBean()
                            beiDouServiceBean.locationReceiver = locationReceiver
                            EventBus.getDefault().postSticky(beiDouServiceBean)
                        }


                    }


                    0x66.toByte() -> {
                        //位置信息上报
                        val message = "位置信息上报"
                        LogUtils.e("0x66 原始数据为${content}")
                        val pId = HexUtil.bcd2Str(content.subList(2, 3).toByteArray())
                        val taskId =
                            HexUtil.bcd2Str(content.subList(3, 4).toByteArray())

                        val toInt = content.subList(3, 4)[0].toByte().toInt()

                        if (pId == phoneId || (toInt == -1 && pId == "0")) {
                            //taskId

                            LogUtils.e("${message}taskId为 $taskId")


                            //用户ID
                            val userId =
                                HexUtil.formatHexString(content.subList(4, 20).toByteArray())
                                    .toUpperCase()
                            LogUtils.e("${message}用户ID为 $userId")


                            //经度
                            val lng =
                                HexUtil.bytesToInt2(content.subList(20, 24).toByteArray(), 0)
                            //
                            LogUtils.e("${message}经度为 $lng")

                            //纬度
                            val lat = HexUtil.bytesToInt2(
                                content.subList(24, 28).toByteArray(), 0
                            )
                            val heartRate =
                                HexUtil.bytes1ToInt2(content.subList(28, 29).toByteArray(), 0)
                            LogUtils.e("${message}纬度为 $lat")


                            val locationReceiver = LocationReceiver()
                            locationReceiver.userId = userId
                            val wgs84togcj03 = CommonUtil.wgs84_To_Gcj02(
                                lat / 10000000.toDouble(),
                                lng / 10000000.toDouble()
                            )
                            wgs84togcj03?.let {
                                locationReceiver.lng  =
                                    wgs84togcj03.longitude.toString()
                                locationReceiver.lat =
                                    wgs84togcj03.latitude.toString()
                            }

                            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                            val date = Date(System.currentTimeMillis())


                            RunnaUtil.isUpdatalist(taskId,(lng / 10000000.toDouble()).toString(),(lat / 10000000.toDouble()).toString(),userId,heartRate.toString(),System.currentTimeMillis().toString(),LoginActivity.access_token)
                            RunnaUtil.isscjUpdatalist(taskId,latCenter.toString(),lngCenter.toString(),(lng / 10000000.toDouble()).toString(),(lat / 10000000.toDouble()).toString(),userId,heartRate.toString(),sdf.format(date),LoginActivity.access_token)

                            locationReceiver.isSOS = false
                            locationReceiver.phoneId = phoneId
                            locationReceiver.projectId = toInt.toString()
                            locationReceiver.time = System.currentTimeMillis().toString()
                            locationReceiver.heartRate = heartRate.toString()
                            locationReceiver.save()
                            EventBus.getDefault().postSticky(locationReceiver)


                            val uploadDataBean = UploadDataBean()
                            uploadDataBean.locationReceiver = locationReceiver
                            EventBus.getDefault().postSticky(uploadDataBean)


                            val beiDouServiceBean = BeiDouServiceBean()
                            beiDouServiceBean.locationReceiver = locationReceiver
                            EventBus.getDefault().postSticky(beiDouServiceBean)
                        }  else {

                        }
                    }

                    else -> {
                    }
                }


            } catch (e: Exception) {
    //            ToastUtils.showShort("解析错误了 $e")
            }

        }
    }

}
