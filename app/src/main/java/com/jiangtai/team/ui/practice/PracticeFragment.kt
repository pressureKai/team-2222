package com.jiangtai.team.ui.practice

import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.blankj.utilcode.util.ToastUtils
import com.jiangtai.team.R
import com.jiangtai.team.base.BaseFragment
import com.jiangtai.team.base.BaseNoDataBean
import com.jiangtai.team.bean.NormalPerson
import com.jiangtai.team.bean.NormalScore
import com.jiangtai.team.constant.Constant
import com.jiangtai.team.event.ClearEvent
import com.jiangtai.team.event.RefreshPracticeEvent
import com.jiangtai.team.net.CallbackListObserver
import com.jiangtai.team.net.MyRetrofit
import com.jiangtai.team.net.ThreadSwitchTransformer
import com.jiangtai.team.request.NormalScoreUploadModel
import com.jiangtai.team.util.Preference
import com.jiangtai.team.util.RegexUtil
import com.jiangtai.team.util.ToJsonUtil
import kotlinx.android.synthetic.main.fragment_practice.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.litepal.LitePal
import org.litepal.extension.findAll

/**
 * Created by heCunCun on 2021/3/9
 */
class PracticeFragment :BaseFragment(){
    private var serverIp: String by Preference(Constant.SERVER_IP, "")
    override fun useEventBus(): Boolean =true
    private var list = mutableListOf<NormalPerson>()
    private val practiceAdapter by lazy {
        PracticeAdapter()
    }
    override fun attachLayoutRes(): Int = R.layout.fragment_practice

    override fun initView(view: View) {
        rv_person.run {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = practiceAdapter
        }
        practiceAdapter.setOnItemClickListener { _, _, position ->
           scoreDetail(position)
        }

        tv_upload.setOnClickListener {
            uploadScore()
        }
    }

    /**
    * @des 成绩详情页面
    * @time 2021/8/3 8:16 下午
    */
    private fun scoreDetail(position:Int){
        val intent = Intent(requireContext(), PracticeScoreActivity::class.java)
        intent.putExtra("personId",list[position].personId)
        intent.putExtra("name",list[position].name)
        startActivity(intent)
    }

    /**
    * @des 上传计划成绩
    * @time 2021/8/3 8:15 下午
    */
    private fun uploadScore(){
        if (serverIp.isEmpty() ||!RegexUtil.isURL(serverIp)){
            ToastUtils.showShort("请先设置正确的服务器IP")
            return
        }
        //上传成绩
        val normalScoreUploadList = mutableListOf<NormalScoreUploadModel>()
        val normalScoreList = LitePal.findAll(NormalScore::class.java)
        if (normalScoreList.isNotEmpty()){
            for (normalScore in normalScoreList){
                val normalScoreUploadModel = NormalScoreUploadModel(normalScore.projectId,normalScore.personId,normalScore.score,normalScore.startTime,normalScore.uploadTime)
                normalScoreUploadList.add(normalScoreUploadModel)
            }

            //请求接口
            val requestBody =
                RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(),
                    ToJsonUtil.getInstance().toJson(normalScoreUploadList))
            val uploadCallback = MyRetrofit.instance.api.uploadNormalScoreList(requestBody)

            uploadCallback.compose(ThreadSwitchTransformer()).subscribe(object :
                CallbackListObserver<BaseNoDataBean>(){
                override fun onSucceed(t: BaseNoDataBean?) {
                    if(t?.error_code == Constant.SUCCESSED_CODE){
                        ToastUtils.showShort("上传成功")
                    }else{
                        ToastUtils.showShort("上传失败,msg =${t?.error_msg}")
                    }
                }

                override fun onFailed() {

                }
            })


        }
    }

    override fun lazyLoad() {
        refreshData()
    }
    private fun refreshData() {
        list = LitePal.findAll<NormalPerson>()
        if (list.isNotEmpty()){
            practiceAdapter.setNewData(list)
        }else{
            practiceAdapter.setEmptyView(R.layout.empty_view,rv_person)
        }

    }
    companion object{
        fun getInstance():PracticeFragment = PracticeFragment()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun refreshPractice(e: RefreshPracticeEvent){
        refreshData()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun clear(e:ClearEvent){
        list.clear()
        practiceAdapter.setNewData(list)
    }

}