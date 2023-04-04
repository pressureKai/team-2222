package com.jiangtai.count.ui.main

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.LoRaManager
import android.os.Handler
import android.os.Looper
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.view.MotionEvent
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.google.gson.Gson
import com.jiangtai.count.R
import com.jiangtai.count.application.App
import com.jiangtai.count.base.BaseActivity
import com.jiangtai.count.bean.*
import com.jiangtai.count.constant.Constant
import com.jiangtai.count.login.LoginActivity
import com.jiangtai.count.net.CallbackListObserver
import com.jiangtai.count.net.MajorRetrofit
import com.jiangtai.count.net.MyRetrofit
import com.jiangtai.count.net.ThreadSwitchTransformer
import com.jiangtai.count.ui.majorpractice.MajorPracticeAdapter
import com.jiangtai.count.ui.map.MapFragment
import com.jiangtai.count.ui.practiceManager.PracticeManagerFragment
import com.jiangtai.count.ui.receiver.BeiDouService
import com.jiangtai.count.ui.receiver.CheckDistanceService
import com.jiangtai.count.ui.setting.SettingFragment
import com.jiangtai.count.ui.signIn.SignInFragment
import com.jiangtai.count.ui.task.TaskFragment
import com.jiangtai.count.util.*
import com.jinyx.mqtt.*
import com.lhzw.libcc.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_major_practice_detail.*
import kotlinx.android.synthetic.main.activity_major_practice_detail.list
import kotlinx.android.synthetic.main.fragment_major_practice.*
import kotlinx.android.synthetic.main.notification_sos.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.litepal.LitePal
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.fixedRateTimer
import kotlin.math.abs


/**
 * Created by heCunCun on 2021/3/9
 */
class MainActivity : BaseActivity() {
    private var eleDistance: Int by Preference(Constant.ELE_DISTANCE, 0)
    private var practiceManagerFragment: PracticeManagerFragment? = null
    private var signInFragment: SignInFragment? = null
    private var taskFragment: TaskFragment? = null
    private var mapFragment: MapFragment? = null
    private var settingFragment: SettingFragment? = null
    private var currentFragment: Fragment? = null
    private var index = 1
    private var channelNum: Int by Preference(Constant.CHANNEL_NUM, -1)
    private var mqttID: Int by Preference(Constant.MQTT_ID, -1)
    private var mqttUrl: String by Preference(Constant.MQTT_URL, "")
    private lateinit var mqttHelper: MqttHelper
    private var phoneId: String by Preference(Constant.PHONE_ID, "")
    private var mCurrentMajorProject: String by Preference(Constant.CURRENT_MAJOR_PROJECT, "")
    private var eleMode: Boolean by Preference(Constant.ELE_MODE, false)
    private var eleTime: Int by Preference(Constant.ELE_TIME, 0)
    private var eleStartTime: Long by Preference(Constant.ELE_START_TIME, 0L)
    private var latCenter: Double by Preference(Constant.LAT_CENTER, 0.toDouble())
    private var lngCenter: Double by Preference(Constant.LNG_CENTER, 0.toDouble())
    lateinit var timer: Timer
    private var serverIp: String by Preference(Constant.SERVER_IP, "")

    private var currentIndex = 1
    override fun attachLayoutRes(): Int = R.layout.activity_main
    override fun initData() {
        // RunnaUtil.RunnaUtil(MainActivity.this)
        // startTimer()
    }

    private fun startTimer() {
        timer = fixedRateTimer("", false, 0, 5000) {
            getWBFromServer()
        }

    }

    fun getWBFromServer() {
        val networkAvailable = NetWork.isNetworkAvailable(applicationContext);
        if (networkAvailable) {
            if (serverIp.isNotEmpty()) {
                val taskList = MyRetrofit.instance.api.getwbList(
                    LoginActivity.users_userid,
                    LoginActivity.access_token
                )
                taskList.compose(ThreadSwitchTransformer())
                    .subscribe(object : CallbackListObserver<IsTaskBean>() {
                        override fun onSucceed(isTaskBean: IsTaskBean) {
                            if (isTaskBean.data.size == 0) {

                            } else {
                                val pancode =
                                    isTaskBean.data.get(0).issue_userid.subSequence(0, 2).toString()
                                val taskBean = WbTaskBean()
                                try {
                                    val taskId =
                                        ((list.adapter as MajorPracticeAdapter).data[0] as WbTaskBean).taskId
                                    ((list.adapter as MajorPracticeAdapter).data[0] as WbTaskBean).delete()
                                    val findAll = LitePal.findAll(Project::class.java)
                                    for (project in findAll) {
                                        val projectId = project.projectId
                                        if (project.taskId == taskId) {
                                            project.delete()
                                        }


                                        try {
                                            val find =
                                                LitePal.where("projectId=?", projectId)
                                                    .find(LocationReceiver::class.java)

                                            for (value in find) {
                                                value.delete()
                                            }
                                        } catch (e: Exception) {

                                        }

                                    }

                                    val scores = LitePal.findAll(Score::class.java)
                                    for (score in scores) {
                                        if (score.taskId == taskId) {
                                            score.delete()
                                        }
                                    }


                                    val persons = LitePal.findAll(WbPerson::class.java)
                                    for (value in persons) {
                                        if (value.taskId == taskId) {
                                            value.delete()
                                        }
                                    }


                                    val realTaskId =
                                        if (taskId.contains(".0")) taskId.replace(".0", "")
                                            .trim() else taskId

                                    val scoreList =
                                        LitePal.where("taskId=?", realTaskId)
                                            .find(UploadMajorScoreData::class.java)
                                    for (value in scoreList) {
                                        value.delete()
                                    }

                                    val locationList = LitePal.findAll(LocationReceiver::class.java)
                                    for (value in locationList) {
                                        if (value.projectId.isEmpty()) {
                                            value.delete()
                                        }
                                    }
                                } catch (e: Exception) {
                                    LogUtils.e("MajorPracticeFragment", "delete error is $e")
                                }
                                try {
                                    taskBean.startTime = CommonUtil.stringToLong(
                                        isTaskBean.data.first().s_time, "yyyy-MM-dd HH:mm:ss"
                                    )
                                    taskBean.endTime = CommonUtil.stringToLong(
                                        isTaskBean.data.first().e_time, "yyyy-MM-dd HH:mm:ss"
                                    )
                                } catch (e: Exception) {
                                    // LogUtils.e(" error is $e")
                                }
                                taskBean.taskName = isTaskBean.data.first().plan_name
                                taskBean.taskId = pancode
                                taskBean.iswb = true
                                val projectList = ArrayList<WbProject>()
                                val personList = ArrayList<WbPerson>()
                                for (value in isTaskBean.data) {
                                    val project = WbProject()
                                    project.projectName = value.task_name
                                    project.projectId = value.id
                                    project.taskId = pancode
                                    project.projectContent =
                                        value.remark + ";任务计划：" + value.plan_name
                                    project.peopleId = value.issue_userid
                                    project.startTime = CommonUtil.stringToLong(
                                        value.s_time, "yyyy-MM-dd HH:mm:ss"
                                    )
                                    project.endTime = CommonUtil.stringToLong(
                                        value.e_time, "yyyy-MM-dd HH:mm:ss"
                                    )
                                    projectList.add(project)
                                    project.save()
                                    val issureUser = value.issue_user
                                    val issureUserId = value.issue_userid
                                    if (issureUser.isNotEmpty()) {
                                        val users = issureUser.split(",")
                                        val usersId = issureUserId.split(",")
                                        for ((index, user) in users.withIndex()) {
                                            val person = WbPerson()
                                            person.name = user
                                            person.personId = usersId[index]
                                            person.taskId = pancode
                                            person.takeDevice = value.task_device
                                            person.deviceListLocal = value.device_code
                                            person.projectId = value.id
                                            personList.add(person)
                                            person.save()
                                        }
                                    }
                                }
                                taskBean.projects = projectList
                                taskBean.persons = personList
                                taskBean.save()
                                //  RunnaUtil.RunnaUtil(this@MainActivity,isTaskBean)
                            }


                        }

                        override fun onFailed() {


                        }
                    })

            }
        } else {
            ToastUtils.showShort("请检查网络")
        }

    }

    override fun useEventBus(): Boolean {
        return true
    }


    @SuppressLint("WrongConstant")
    override fun initView() {
        initImmersionBar(fitSystem = false)
        //切换手持为0信道
        if (channelNum != 0) {
            val systemService = getSystemService(Context.LORA_SERVICE)
            systemService?.let {
                val mLoraManager = it as LoRaManager
                mLoraManager.changeWatchType(0)
                channelNum = 0
            }
        }


        Handler().postDelayed({
            showFragment(index)
        }, 100)


//        initMqtt(true)
//        sendSMS()


//        CheckDistanceService.startCurrentThread(this)


//        send_sms.setOnClickListener {
//            val sendEntity = SendEntity()
//            sendEntity.userId = "777579616e776569a1a2a3a4a5a6a7a4"
//            sendEntity.phoneId = "0"
//            sendEntity.content = "1"
//            sendEntity.sendType = SendEntity.WATCH
//            val loRaManager = getSystemService(Context.LORA_SERVICE) as LoRaManager
//            Manager433.send(sendEntity,loRaManager,object :SendListener{
//                override fun sendFinish(message: String?) {
//                    ToastUtils.showShort("发送成功")
//                }
//            })
//        }


//        send_command.setOnClickListener {
//            val sendEntity = SendEntity()
//            sendEntity.phoneId = "1"
//            sendEntity.content = "测试"
//            sendEntity.taskEntity.taskId = "56"
//            sendEntity.taskEntity.projectId = "10"
//            sendEntity.sendType = SendEntity.COMMAND
//            val loRaManager = getSystemService(Context.LORA_SERVICE) as LoRaManager
//            Manager433.send(sendEntity,loRaManager,object :SendListener{
//                override fun sendFinish(message: String?) {
//                    ToastUtils.showShort("发送成功")
//                }
//            })
//        }


//        request_location.setOnClickListener {
//            val sendEntity = SendEntity()
//            sendEntity.phoneId = "1"
//            sendEntity.taskEntity.projectId = "11"
//            sendEntity.sendType = SendEntity.LOCATION
//            Manager433.send(sendEntity,this,object : SendListener {
//                override fun sendFinish(message: String?) {
//                    ToastUtils.showShort("发送成功")
//                }
//            })
//        }

//        sos.setOnClickListener {
//            val sendEntity = SendEntity()
//            sendEntity.userId = "777579616e776569a1a2a3a4a5a6a7a4"
//            sendEntity.phoneId = "1"
//            sendEntity.sendType = SendEntity.SOS
//            val loRaManager = getSystemService(Context.LORA_SERVICE) as LoRaManager
//            Manager433.send(sendEntity,loRaManager,object :SendListener{
//                override fun sendFinish(message: String?) {
//                    ToastUtils.showShort("发送成功")
//                }
//            })
//        }

//        CommandUtils.majorTraining(
//            phoneId.toInt(),
//            parseToInt(item.taskId),
//            find.first().taskName,
//            item.projectId,
//            item.projectName,
//            item.projectContent,
//            (find.first().startTime).toString(),
//            (find.first().endTime).toString(),
//            true,
//            this@MajorPracticeDetailActivity
//        )

//        task.setOnClickListener {
//            val sendEntity = SendEntity()
//            sendEntity.sendType = SendEntity.TASK
//            sendEntity.phoneId = "1"
//            sendEntity.taskEntity.taskId = "66"
//            sendEntity.taskEntity.taskName = "测试计划"
//            sendEntity.taskEntity.projectId = "11"
//            sendEntity.taskEntity.projectName = "测试任务"
//            sendEntity.taskEntity.projectContent = "测试任务内容"
//            sendEntity.taskEntity.startTime = System.currentTimeMillis().toString()
//            sendEntity.taskEntity.endTime = System.currentTimeMillis().toString() + 60*10000
//            sendEntity.taskEntity.isLocation = true
//            Manager433.send(sendEntity,this,object :SendListener{
//                override fun sendFinish(message: String?) {
//                    ToastUtils.showShort("发送成功")
//                }
//            })
//        }


//        Manager433.initService(this,object : ReceiverListener {
//            override fun onLocationReceiver(locationReceiver: ServiceLocationReceiver) {
//                LogUtils.e("Service433Receiver service be receiver")
//            }
//        })


//        ManagerBeiDou.initService(this,object :OnServiceBeiDouReceiverListener{
//            override fun onServiceBeiDouReceiverListener(content: String) {
//
//            }
//        })
        testMqtt()
    }


    private fun testMqtt() {
        val mqttBean = MqttBean()


        val mqttItemBeans = ArrayList<MqttBean.MqttItemBean>()
        for (value in 1..2) {
            val mqttItemBean = MqttBean.MqttItemBean()
            mqttItemBean.name = "text"


            val bean = MqttBean.bean()
            //目前只用到了 2
            bean.type = "2"
            //1代表开饭，2代表集合，3代表休息，20代表自定义
            bean.values = "20"
            bean.content = "作战"


            val toJson = Gson().toJson(bean)
            mqttItemBean.value = toJson


            mqttItemBeans.add(mqttItemBean)
        }
        mqttBean.inputs = mqttItemBeans

        val toJson = Gson().toJson(mqttBean)
        LogUtils.e("Test", toJson)
    }


    private fun sendSMS() {
        val intent = Intent()
        intent.setClass(this, BeiDouService::class.java)
        startService(intent)
        Thread {
            while (true) {
                if (!isDestroyed) {
                    if (!isPause) {
                        if (eleMode) {
                            if (eleTime != 0) {
                                val l = System.currentTimeMillis() - eleStartTime
                                if (l > (eleTime * 1000 * 60)) {
                                    eleStartTime = System.currentTimeMillis()
                                    val personList = getPersonList()
                                    if (personList.isNotEmpty()) {

                                        Handler(Looper.getMainLooper()).postDelayed({
                                            CommandUtils.inquiryDeviceCommand2(
                                                this@MainActivity,
                                                phoneId.toInt(),
                                                mCurrentMajorProject.toInt()
                                            )
                                        }, 500)
//                                    Thread{
//                                        for (value in personList) {
//                                            Thread.sleep(3000)
//                                        }
//                                    }.start()
                                    }
                                    if (System.currentTimeMillis() - eleStartTime > 59 * 1000) {
                                        uploadLocation()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }.start()
    }


    private fun uploadLocation() {
        val personList = getPersonList()
        val arrayList = ArrayList<LocationReceiver>()
        for (value in personList) {
            val find = LitePal.where(
                "userId = ? and isSOS = ?",
                value.personId.toUpperCase(), "0"
            ).find(LocationReceiver::class.java)
            if (find.size > 0) {
                arrayList.add(find.last())
            }
        }

        if (arrayList.size > 0 && serverIp.isNotEmpty()) {
            val dipperBig = DipperBig()
            dipperBig.handsetnumber = CommonUtil.getSNCode().toString()
            dipperBig.cmd = "personlocationindication"
            dipperBig.lat = latCenter.toString()
            dipperBig.lng = lngCenter.toString()
            dipperBig.time = CommonUtil.formatTime(System.currentTimeMillis().toString())


            val taskpersonloclist = dipperBig.taskpersonloclist
            for (value in arrayList) {
                val bigDipperPersonVi = BigDipperPersonVi()
                bigDipperPersonVi.sostype = value.sosType
                bigDipperPersonVi.taskid = value.taskId
                bigDipperPersonVi.shortid = "1"
                bigDipperPersonVi.status = "normal"
                bigDipperPersonVi.heartrate = value.heartRate
                bigDipperPersonVi.time =
                    CommonUtil.formatTime(System.currentTimeMillis().toString())
                bigDipperPersonVi.lat = value.lat
                bigDipperPersonVi.lng = value.lng
                bigDipperPersonVi.deviceid = value.userId
                taskpersonloclist.add(bigDipperPersonVi)
            }


            val toJson = ToJsonUtil.getInstance().toJson(dipperBig)
            val requestBody =
                RequestBody.create(
                    "application/json; charset=utf-8".toMediaTypeOrNull(),
                    toJson
                )

            LogUtils.e("toJson $toJson")
            val uploadMajorScore = MajorRetrofit.instance.api.uploadMajorLocation(requestBody)
            uploadMajorScore.compose(ThreadSwitchTransformer()).subscribe(object :
                CallbackListObserver<ResponseData>() {
                override fun onSucceed(t: ResponseData?) {
                    ToastUtils.showShort("上报成功")
                }

                override fun onFailed() {

                }
            })
        }
    }

    /**
     * @des 初始化Mqtt
     * @time 2021/8/12 1:42 下午
     */
    private fun initMqtt(showToast: Boolean? = true) {
        //  mqttUrl = "tcp://221.226.11.218:22883"
        mqttUrl = "tcp://169.254.28.223:1883"
        val onMqttMsgListener = object : OnMqttMsgListener {
            override fun onPubMessage(payload: ByteArray) {
                //发送消息回调
                ToastUtils.showShort("发送成功 ${String(payload)}")
            }

            override fun onSubMessage(topic: String, payload: ByteArray) {
                //接收消息
                val jsonStr = String(payload)
                // ToastUtils.showShort("response json is $jsonStr")

                val beiDouReceiveBean = BeiDouReceiveBean()
                beiDouReceiveBean.content = jsonStr
                beiDouReceiveBean.time = System.currentTimeMillis().toString()
                beiDouReceiveBean.save()
                EventBus.getDefault().postSticky(beiDouReceiveBean)
                parseMqtt(jsonStr)
            }
        }


        val onMqttStatusChangeListener = object : OnMqttStatusChangeListener {
            override fun onChange(state: MqttStatus, throwable: Throwable?) {
                /**
                 *   MQTT 连接状态改变:
                 *  [MqttStatus.SUCCESS]// 连接成功
                 *  [MqttStatus.FAILURE]// 连接失败
                 *  [MqttStatus.LOST]   // 连接中断
                 */
                when (state) {
                    MqttStatus.SUCCESS -> {
                        Handler().postDelayed({
                            if (showToast!!) {
                                ToastUtils.showShort("连接成功")
                            }
                        }, 100)
                    }
                    MqttStatus.FAILURE -> {
                        Handler().postDelayed({
                            if (showToast!!) {
                                // ToastUtils.showShort("连接失败 ${throwable.toString()}")
                            }
                        }, 100)
                    }
                    else -> {
                        Handler().postDelayed({
                            if (showToast!!) {
                                ToastUtils.showShort("连接断开 ${throwable.toString()}")
                            }
                        }, 100)
                    }
                }
            }

        }

        try {

            val snCode = CommonUtil.getSNCode()
            LogUtils.e("sn code $snCode")
            val options = MqttOptions(
                serviceUrl = mqttUrl,   // MQTT 服务
                username = "$snCode|${System.currentTimeMillis()}",
                password = MD5Utils.stringToMD5("$snCode|${System.currentTimeMillis()}|$snCode"),
                clientId = snCode!!, // MQTT 客户端ID， 唯一标识，如果存在多个 MQTT 对象使用同一个 clientId，会导致相互之间不断被挤掉再重连
                willTopic = "will/android",     // 遗嘱 Topic，不能存在通配符 # 和 +，可用于监听对方是否掉线
                willMsg = "I'm Died - $mqttID"      // 遗嘱消息，当客户端掉线，MQTT 服务发送pingreq包，客户端不回复 pingresp 包，MQTT 发送遗嘱消息到 订阅 willTopic 的客户端
            )
            mqttHelper = MqttHelper(this, options)
            mqttHelper.addOnMsgListener(onMqttMsgListener)
            mqttHelper.addOnStatusChangeListener(onMqttStatusChangeListener)
            mqttHelper.connect()
        } catch (e: Exception) {
            LogUtils.e("MainActivity error is $e")
        }

    }


    private fun parseMqtt(jsonStr: String) {
        val mqttBean = ToJsonUtil.fromJson(jsonStr, MqttBean::class.java)
        for (value in mqttBean.inputs) {
            try {
                if (value.name == "text") {
                    val json = value.value
                    val bean = ToJsonUtil.fromJson(json, MqttBean.bean::class.java)

                    if (bean.type == "2") {
                        when (bean.values.toInt()) {
                            //1代表开饭，2代表集合，3代表休息，20代表自定义
                            20 -> {
                                ToastUtils.showShort("自定义")
                                //自定义
                                if (phoneId.isNotEmpty()) {
                                    if (mCurrentMajorProject.isNotEmpty()) {
                                        var custom = ""
                                        if (bean.content.isNotEmpty()) {
                                            custom = bean.content
                                            ToastUtils.showShort(custom)
                                            CommandUtils.actionCommand(
                                                this@MainActivity,
                                                phoneId.toInt(),
                                                getCurrentTaskId(),
                                                mCurrentMajorProject,
                                                custom
                                            )
                                        }
                                    } else {
                                        ToastUtils.showShort("请选择计划任务")
                                    }
                                } else {
                                    ToastUtils.showShort("请设置手持机ID")
                                }
                            }
                            2 -> {
                                ToastUtils.showShort("开饭")
                                //开饭
                                if (phoneId.isNotEmpty()) {
                                    if (mCurrentMajorProject.isNotEmpty()) {
                                        CommandUtils.actionCommand(
                                            this@MainActivity,
                                            phoneId.toInt(),
                                            getCurrentTaskId(),
                                            mCurrentMajorProject,
                                            "开饭"
                                        )
                                    } else {
                                        ToastUtils.showShort("请选择计划任务")
                                    }
                                } else {
                                    ToastUtils.showShort("请设置手持机ID")
                                }
                            }
                            1 -> {

                                ToastUtils.showShort("集合")
                                //集合
                                if (phoneId.isNotEmpty()) {
                                    if (mCurrentMajorProject.isNotEmpty()) {
                                        CommandUtils.actionCommand(
                                            this@MainActivity,
                                            phoneId.toInt(),
                                            getCurrentTaskId(),
                                            mCurrentMajorProject,
                                            "集合"
                                        )
                                    } else {
                                        ToastUtils.showShort("请选择计划任务")
                                    }
                                } else {
                                    ToastUtils.showShort("请设置手持机ID")
                                }
                            }
                            3 -> {
                                //休息
                                ToastUtils.showShort("休息")
                                if (phoneId.isNotEmpty()) {
                                    if (mCurrentMajorProject.isNotEmpty()) {
                                        CommandUtils.actionCommand(
                                            this@MainActivity,
                                            phoneId.toInt(),
                                            getCurrentTaskId(),
                                            mCurrentMajorProject,
                                            "休息"
                                        )
                                    } else {
                                        ToastUtils.showShort("请选择计划任务")
                                    }
                                } else {
                                    ToastUtils.showShort("请设置手持机ID")
                                }
                            }
                        }
                    } else {
                        eleMode = true
                        try {
                            eleDistance = bean.values.toInt()
                            eleStartTime = System.currentTimeMillis()
                            EventBus.getDefault().postSticky(EleBean())
                        } catch (e: Exception) {

                        }
                    }
                }
            } catch (e: Exception) {

            }
        }
    }


    private fun showFragment(index: Int) {
        val transaction = supportFragmentManager.beginTransaction()
        currentIndex = index
        when (index) {
            1 -> {
                if (practiceManagerFragment == null) {
                    practiceManagerFragment = PracticeManagerFragment.getInstance()
                    transaction.add(R.id.container, practiceManagerFragment, "practice")
                }
                try {
                    if (currentFragment != null) {
                        transaction.hide(currentFragment).show(practiceManagerFragment)
                        currentFragment = practiceManagerFragment
                        transaction.commit()
                    } else {
                        transaction.hide(practiceManagerFragment).show(practiceManagerFragment)
                        currentFragment = practiceManagerFragment
                        transaction.commit()
                    }
                } catch (e: Exception) {

                }

            }
            2 -> {
                if (taskFragment == null) {
                    taskFragment = TaskFragment.getInstance()
                    transaction.add(R.id.container, taskFragment, "task")
                }
                if (currentFragment == null) {
                    currentFragment = taskFragment
                }
                Handler(Looper.getMainLooper()).postDelayed({
                    transaction.hide(currentFragment).show(taskFragment)
                    currentFragment = taskFragment
                    transaction.commit()
                }, 0)
            }

            3 -> {
                if (mapFragment == null) {
                    mapFragment = MapFragment.getInstance()
                    transaction.add(R.id.container, mapFragment, "map")
                }
                if (currentFragment == null) {
                    currentFragment = mapFragment
                }
                Handler(Looper.getMainLooper()).postDelayed({
                    transaction.hide(currentFragment).show(mapFragment)
                    currentFragment = mapFragment
                    transaction.commit()
                }, 0)

            }

            4 -> {
                if (settingFragment == null) {
                    settingFragment = SettingFragment.getInstance()
                    transaction.add(R.id.container, settingFragment, "setting")
                }
                if (currentFragment == null) {
                    currentFragment = settingFragment
                }

                Handler(Looper.getMainLooper()).postDelayed({
                    transaction.hide(currentFragment).show(settingFragment)
                    currentFragment = settingFragment
                    transaction.commit()
                }, 0)

            }

            5 -> {
                if (signInFragment == null) {
                    signInFragment = SignInFragment.getInstance()
                    transaction.add(R.id.container, signInFragment, "signIn")
                }
                if (currentFragment == null) {
                    currentFragment = signInFragment
                }

                Handler(Looper.getMainLooper()).postDelayed({
                    transaction.hide(currentFragment).show(signInFragment)
                    currentFragment = signInFragment
                    transaction.commit()
                }, 0)
            }
        }

    }

    override fun initListener() {
        ll_practice.setOnClickListener {
            if (index == 1) {
                return@setOnClickListener
            }
            iv_practice.setImageResource(R.mipmap.my_but1)
            tv_practice.setTextColor(resources.getColor(R.color.color_blue_2979FF))
            iv_task.setImageResource(R.mipmap.ic_task_normal)
            tv_task.setTextColor(resources.getColor(R.color.color_gray_FAFAFA))
            iv_map.setImageResource(R.mipmap.my_rz)
            tv_map.setTextColor(resources.getColor(R.color.color_gray_FAFAFA))
            iv_sign.setImageResource(R.mipmap.my_wg)
            tv_sign.setTextColor(resources.getColor(R.color.color_gray_FAFAFA))
            iv_setting.setImageResource(R.mipmap.my_setting)
            tv_setting.setTextColor(resources.getColor(R.color.color_gray_FAFAFA))
            index = 1
            showFragment(1)
        }


        ll_task.setOnClickListener {
            if (index == 2) {
                return@setOnClickListener
            }
            iv_practice.setImageResource(R.mipmap.my_but1)
            tv_practice.setTextColor(resources.getColor(R.color.color_gray_FAFAFA))
            iv_task.setImageResource(R.mipmap.ic_task_selected)
            tv_task.setTextColor(resources.getColor(R.color.color_blue_2979FF))
            iv_map.setImageResource(R.mipmap.my_rz)
            tv_map.setTextColor(resources.getColor(R.color.color_gray_FAFAFA))
            iv_setting.setImageResource(R.mipmap.my_setting)
            tv_setting.setTextColor(resources.getColor(R.color.color_gray_FAFAFA))
            index = 2
            showFragment(2)
        }

        ll_map.setOnClickListener {
            if (index == 3) {
                return@setOnClickListener
            }
            iv_practice.setImageResource(R.mipmap.my_but1)
            tv_practice.setTextColor(resources.getColor(R.color.color_gray_FAFAFA))
            iv_task.setImageResource(R.mipmap.ic_task_normal)
            tv_task.setTextColor(resources.getColor(R.color.color_gray_FAFAFA))
            iv_map.setImageResource(R.mipmap.my_rz)
            tv_map.setTextColor(resources.getColor(R.color.color_blue_2979FF))
            iv_setting.setImageResource(R.mipmap.my_setting)
            tv_setting.setTextColor(resources.getColor(R.color.color_gray_FAFAFA))
            iv_sign.setImageResource(R.mipmap.my_wg)
            tv_sign.setTextColor(resources.getColor(R.color.color_gray_FAFAFA))
            window.statusBarColor = getColor(R.color.black)
            index = 3
            showFragment(3)
        }

        ll_setting.setOnClickListener {
            if (index == 4) {
                return@setOnClickListener
            }
            iv_practice.setImageResource(R.mipmap.my_but1)
            tv_practice.setTextColor(resources.getColor(R.color.color_gray_FAFAFA))
            iv_task.setImageResource(R.mipmap.ic_task_normal)
            tv_task.setTextColor(resources.getColor(R.color.color_gray_FAFAFA))
            iv_map.setImageResource(R.mipmap.my_rz)
            tv_map.setTextColor(resources.getColor(R.color.color_gray_FAFAFA))
            iv_sign.setImageResource(R.mipmap.my_wg)
            tv_sign.setTextColor(resources.getColor(R.color.color_gray_FAFAFA))
            iv_setting.setImageResource(R.mipmap.my_setting)
            tv_setting.setTextColor(resources.getColor(R.color.color_blue_2979FF))
            index = 4
            showFragment(4)
        }


        ll_sign.setOnClickListener {
            if (index == 5) {
                return@setOnClickListener
            }
            iv_practice.setImageResource(R.mipmap.my_but1)
            tv_practice.setTextColor(resources.getColor(R.color.color_gray_FAFAFA))
            iv_task.setImageResource(R.mipmap.ic_task_normal)
            tv_task.setTextColor(resources.getColor(R.color.color_gray_FAFAFA))
            iv_sign.setImageResource(R.mipmap.my_wg)
            tv_sign.setTextColor(resources.getColor(R.color.color_blue_2979FF))
            iv_map.setImageResource(R.mipmap.my_rz)
            tv_map.setTextColor(resources.getColor(R.color.color_gray_FAFAFA))
            iv_setting.setImageResource(R.mipmap.my_setting)
            tv_setting.setTextColor(resources.getColor(R.color.color_gray_FAFAFA))
            window.statusBarColor = getColor(R.color.black)
            index = 5
            showFragment(5)
        }

    }

    var firstTouchX = 0f
    var firstTouchY = 0f

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        return if (index == 2) {
            var dispatch = true
            ev?.let {
                when (it.action) {
                    MotionEvent.ACTION_DOWN -> {
                        firstTouchX = it.rawX
                        firstTouchY = it.rawY
                    }
                    MotionEvent.ACTION_MOVE -> {
                        dispatch = abs(it.rawY - firstTouchY) <= abs(it.rawX - firstTouchX)
                    }
                    else -> {

                    }
                }
            }

            if (!dispatch) {
                taskFragment?.mRecyclerView?.dispatchTouchEvent(ev)
                false
            } else {
                super.dispatchTouchEvent(ev)
            }
        } else {
            super.dispatchTouchEvent(ev)
        }

    }


    override fun onDestroy() {
        super.onDestroy()
//        mqttHelper.disConnect()
        CheckDistanceService.stopCurrentThread()
//        Manager433.unRegisterReceiver(this)
//        ManagerBeiDou.unRegisterReceiver(this)
    }


    private fun getCurrentTaskId(): String {
        var taskId = ""
        val find = LitePal.where("projectId = ?", mCurrentMajorProject).find(Project::class.java)
        if (find.size > 0) {
            taskId = find.first().taskId
        }
        return taskId
    }


    private var isPause = false

    override fun onResume() {
        try {
            super.onResume()
//            initMqtt(false)
            isPause = false
//            LogUtils.e("MainActivity","onResume")
            showFragment(currentIndex)
        } catch (e: Exception) {

        }

    }

    override fun onPause() {
        super.onPause()
        isPause = true
    }


    @Subscribe(threadMode = ThreadMode.ASYNC, sticky = true)
    fun showSOSDialog(sosBean: SosBean) {
        //  ToastUtils.showShort("显示SOS消息")
        EventBus.getDefault().removeStickyEvent(sosBean)
        runOnUiThread {
            if ((index != 3 && !isPause) || isPause) {
                App.getMineContext()?.let {
                    CommonUtil.getTopActivityInstance()?.let { activity ->
                        DialogHelper.instance?.showSOSDialog(
                            activity as FragmentActivity,
                            sosBean,
                            object : DialogHelper.RemindDialogClickListener {
                                override fun onRemindDialogClickListener(
                                    positive: Boolean,
                                    message: String
                                ) {
                                    if (positive) {
                                        val intent = Intent(activity, MainActivity::class.java)
                                        startActivity(intent)
                                        Handler(Looper.getMainLooper()).postDelayed({
                                            iv_practice.setImageResource(R.mipmap.my_but1)
                                            tv_practice.setTextColor(resources.getColor(R.color.color_gray_FAFAFA))
                                            iv_task.setImageResource(R.mipmap.ic_task_normal)
                                            tv_task.setTextColor(resources.getColor(R.color.color_gray_FAFAFA))
                                            iv_map.setImageResource(R.mipmap.my_rz)
                                            tv_map.setTextColor(resources.getColor(R.color.color_blue_2979FF))
                                            iv_setting.setImageResource(R.mipmap.my_setting)
                                            tv_setting.setTextColor(resources.getColor(R.color.color_gray_FAFAFA))
                                            window.statusBarColor = getColor(R.color.black)
                                            index = 3
                                            showFragment(3)
                                        }, 200)
                                    }
                                    DialogHelper.instance?.hintSOSDialog()
                                }
                            })
                    }

                }

            }
        }
    }


    private fun getPersonList(): ArrayList<Person> {
        val find = LitePal.where("projectId = ?", mCurrentMajorProject)
            .find(Project::class.java)
        val arrayList = ArrayList<Person>()
        if (find.size > 0 && find.first().peopleId.isNotEmpty()) {
            val peopleId = find.first().peopleId
            val split = peopleId.split(",")
            for (value in split) {
                LogUtils.e("value is ${value.toUpperCase()}")
                val find1 =
                    LitePal.where("personId = ?", value.toUpperCase()).find(Person::class.java)
                arrayList.addAll(find1)
            }
        }
        return arrayList
    }
}