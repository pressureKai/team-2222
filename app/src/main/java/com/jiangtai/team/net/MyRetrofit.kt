package com.jiangtai.team.net

import com.google.gson.GsonBuilder
import com.jiangtai.team.constant.Constant
import com.jiangtai.team.util.Preference
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.jvm.Throws

/**
 * Created by hecuncun on 2019/5/13
 */
class MyRetrofit private constructor() {
    private var serverIp: String by Preference(Constant.SERVER_IP, "")
    val api: Api
    private fun genericClient(): OkHttpClient {
        val loggingInterceptor =
            HttpLoggingInterceptor() //该拦截器用于记录应用中的网络请求的信息
        /**
         * 可以通过 setLevel 改变日志级别
         * 共包含四个级别：NONE、BASIC、HEADER、BODY
         *
         * NONE 不记录
         *
         * BASIC 请求/响应行
         * --> POST /greeting HTTP/1.1 (3-byte body)
         * <-- HTTP/1.1 200 OK (22ms, 6-byte body)
         *
         * HEADER 请求/响应行 + 头
         *
         * --> Host: example.com
         * Content-Type: plain/text
         * Content-Length: 3
         *
         * <-- HTTP/1.1 200 OK (22ms)
         * Content-Type: plain/text
         * Content-Length: 6
         *
         * BODY 请求/响应行 + 头 + 体
         */
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        return OkHttpClient.Builder()
            .connectTimeout(
                DEFAULT_TIME_OUT.toLong(),
                TimeUnit.SECONDS
            )
            .writeTimeout(
                DEFAULT_READ_TIME_OUT.toLong(),
                TimeUnit.SECONDS
            )
            .readTimeout(
                DEFAULT_READ_TIME_OUT.toLong(),
                TimeUnit.SECONDS
            )
            .retryOnConnectionFailure(true) //失败后，是否重新连接，
            //启用Log日志
            .addInterceptor(loggingInterceptor) //添加拦截器  拦截器拿到了request之后，可以对request进行重写，可以添加，移除，替换请求头，也能对response的header进行重写，改变response的body
            .addInterceptor(object : Interceptor {
                @Throws(IOException::class)
                override fun intercept(chain: Interceptor.Chain): Response {
//                        CacheControl.Builder builder = new CacheControl.Builder().maxAge(10, TimeUnit.MINUTES);
                    val request = chain.request()
                        .newBuilder() //                                .header("Cache-Control", builder.build().toString())
                        //                                .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                        .addHeader(
                            "Content-Type",
                            "application/json; charset=UTF-8"
                        ) //                                .addHeader("Accept-Encoding", "gzip, deflate")
                        //                                .addHeader("Accept-Encoding", "gzip,sdch")
                        .addHeader("Connection", "keep-alive")
                        .addHeader(
                            "Accept",
                            "*/*"
                        ) //.addHeader("x-access-token", SpUtils.getString(Constant.HTTP_TOOKEN,""))
                        // .addHeader("Cookie", cookie)
                        //.addHeader("Authorization","APPCODE " + Constant.OCR_APP_CODE)
                        .build()
                    return chain.proceed(request)
                }
            })
            .build()
    }

    companion object {
        private var mService: MyRetrofit? = null
        private const val DEFAULT_TIME_OUT = 10 //超时时间
        private const val DEFAULT_READ_TIME_OUT = 10
        val instance: MyRetrofit
            get() {
                if (mService == null) {
                    synchronized(MyRetrofit::class.java) {
                        if (mService == null) {
                            mService = MyRetrofit()
                        }
                    }
                }
                return mService!!
            }

        fun resetInstance() {
            mService = null
        }
    }

    init {
        val gson = GsonBuilder().create()
        val retrofit = Retrofit.Builder()
            .baseUrl(serverIp)
            .client(genericClient())
            .addConverterFactory(GsonConverterFactory.create(gson)) //数据的处理时，只有请求成功后，才能需要解析data的数据,其他时候我们直接抛异常处理 json解析
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) //支持RxJava
            .build()
        api = retrofit.create(Api::class.java)
    }
}