package com.jiangtai.count.net;


import com.jiangtai.count.base.BaseNoDataBean;
import com.jiangtai.count.base.NewBaseBean;
import com.jiangtai.count.bean.CarFixBean;
import com.jiangtai.count.bean.FeelListBean;
import com.jiangtai.count.bean.HelicopterOilInfoBean;
import com.jiangtai.count.bean.IsTaskBean;
import com.jiangtai.count.bean.NewLoginBean;
import com.jiangtai.count.bean.OilInfoBean;
import com.jiangtai.count.bean.WeatherInfoBean;
import com.jiangtai.count.response.TaskDetailModel;
import com.jiangtai.count.response.TaskListModel;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;


/**
 * Created by hecuncun on 2019/5/13
 */
public interface Api {
    /**
     * 获取任务列表
     */
    @GET("wear/wearable/getTaskList")
    Observable<TaskListModel> getTaskList(@Query("pageNo") String pageNo, @Query("pageSize") String pageSize);
    @GET("dss/platform/中电太极/task/scj")
    Observable<IsTaskBean> getIsTaskList(@Query("issue_userid") String issue_userid, @Query("access_token") String access_token);

    @GET("dss/platform/中电太极/task/wb")
    Observable<IsTaskBean> getwbList(@Query("issue_userid") String issue_userid, @Query("access_token") String access_token);

    /**
     *获取任务详情
     */
    @POST("wear/wearable/getTaskDetail")
    Observable<TaskDetailModel> getTaskDetail(@Body RequestBody requestBody);
    /**
     * 考核成绩上传
     */
    @POST("wear/wearable/pushPersonAssesInfo")
    Observable<BaseNoDataBean> uploadScoreList(@Body RequestBody requestBody);
    /**
     * 计划成绩上传
     *
     */
    @POST("wear/wearable/pushPersonTrainInfo")
    Observable<BaseNoDataBean> uploadNormalScoreList(@Body RequestBody requestBody);



    @POST("basic/loginSync")
    Observable<NewBaseBean<NewLoginBean>> login(@Body RequestBody requestBody);



    //装备状态数据上报接口
    @POST("api/WLW_CGXXJRFW/sendVehInfo")
    Observable<NewBaseBean<Object>> sendVehInfo(@Body RequestBody requestBody);


    //空投物资接收状态上报接口
    @POST("api/WLW_CGXXJRFW/sendMatRecvStatus")
    Observable<NewBaseBean<Object>> sendMatRecv(@Body RequestBody requestBody);


    // 油库量油数据上报接口
    @POST("api/WLW_CGXXJRFW/sendOilDepotMeasureInfo")
    Observable<NewBaseBean<Object>> sendOilDepot(@Body RequestBody requestBody);


    //  设施感知数据上报接口
    @POST("api/WLW_CGXXJRFW/sendFacInfo")
    Observable<NewBaseBean<Object>> sendFaceInfo(@Body RequestBody requestBody);


    //气象数据上报接口
    @POST("api/WLW_CGXXJRFW/sendEnvInfo")
    Observable<NewBaseBean<Object>> sendWeatherInfo(@Body RequestBody requestBody);


    //飞机加油数据上报接口
    @POST("api/WLW_CGXXJRFW/sendAddOilInfo")
    Observable<NewBaseBean<Object>> sendHelicopterData(@Body RequestBody requestBody);


    //飞机加油记录查询接口
    @GET("api/WLW_CGXXJRFW/getAddOilInfo")
    Observable<NewBaseBean<List<HelicopterOilInfoBean>>> getHelicopterList(@Query("JYRID") String GCRID);


    //气象数据上报接口
    @GET("api/WLW_CGXXJRFW/getEnvInfo")
    Observable<NewBaseBean<List<WeatherInfoBean>>> getWeatherList(@Query("GCRID") String GCRID);



    //装备维修记录查询接口
    @GET("api/WLW_CGXXJRFW/getVehRepairInfo")
    Observable<NewBaseBean<List<CarFixBean>>> deviceFixList(@Query("WXRY") String WXRY);


    //油库量油数据上报接口
    @GET("api/WLW_CGXXJRFW/getOilDepotMeasureInfo")
    Observable<NewBaseBean<List<OilInfoBean>>> getOilList(@Query("CLRID") String CLRID );


    //目标识别结果查询接口
    @GET("api/WLW_CGXXJRFW/getTargetList")
    Observable<NewBaseBean<List<FeelListBean>>> getTargetList(@Query("CLRID") String CLRID );

    //装备维修记录上报接口（人工录入）
    @POST("api/WLW_CGXXJRFW/sendVehRepairInfo")
    Observable<NewBaseBean<Object>> sendFixData(@Body RequestBody requestBody);

}