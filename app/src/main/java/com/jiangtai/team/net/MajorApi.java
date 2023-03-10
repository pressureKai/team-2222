package com.jiangtai.team.net;

import com.jiangtai.team.bean.ResponseData;
import com.jiangtai.team.bean.ResponseData2;
import com.jiangtai.team.bean.ReturnIsBean;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;


/**
 * Created by hecuncun on 2019/5/13
 */
public interface MajorApi {

    /**
     * 专项计划成绩上传
     */
    @POST("practice/insertPractice")
    Observable<ResponseData2> uploadMajorScore(@Body RequestBody requestBody);

    @GET("dss/platform/中电太极/insert")
    Observable<ReturnIsBean> uploadRetuenList(@Query("taskId") String taskId, @Query("taskStartTm") String taskStartTm, @Query("taskEndTm") String taskEndTm, @Query("subjectId") String subjectId, @Query("subjectStartTm") String subjectStartTm, @Query("subjectEndTm") String subjectEndTm, @Query("personId") String personId, @Query("score") String score, @Query("startTm") String startTm, @Query("finishTm") String finishTm,
                                              @Query("StartHr") String StartHr,@Query("EndHr") String EndHr, @Query("StartLong") String StartLong,@Query("EndLong") String EndLong,
                                              @Query("StartLat") String StartLat,@Query("EndLat") String EndLat,
                                              @Query("access_token") String access_token);


    /**
     * 位置上报
     */
    @POST("big/dipper/add")
    Observable<ResponseData> uploadMajorLocation(@Body RequestBody requestBody);
}