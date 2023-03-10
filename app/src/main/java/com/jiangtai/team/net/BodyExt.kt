package com.jiangtai.team.net

import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

/**
 * Created by hecuncun on 2020/12/12
 */

fun Any.toJsonBody(map: Map<String, Any?>): RequestBody {
    val json = Gson().toJson(map)
    return json.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
}

fun Any.toJsonBody(maps: List<Map<String, Any?>>): RequestBody {
    val json = Gson().toJson(maps)
    return json.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
}


