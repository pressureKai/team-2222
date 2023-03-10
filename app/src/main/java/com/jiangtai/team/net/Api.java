package com.jiangtai.team.net;


import com.jiangtai.team.base.BaseNoDataBean;
import com.jiangtai.team.base.NewBaseBean;
import com.jiangtai.team.bean.IsTaskBean;
import com.jiangtai.team.bean.NewLoginBean;
import com.jiangtai.team.response.TaskDetailModel;
import com.jiangtai.team.response.TaskListModel;

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

}