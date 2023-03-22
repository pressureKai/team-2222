package com.jiangtai.count.util

import android.graphics.Color
import com.amap.api.maps.AMap
import com.amap.api.maps.model.Circle
import com.amap.api.maps.model.CircleOptions
import com.amap.api.maps.model.LatLng

object CircleBuilder {
    //180, 3, 145, 255
    //10, 0, 0, 180
    val STROKE_COLOR = Color.argb(0, 3, 145, 200)
    val FILL_COLOR = Color.argb(10, 0, 0, 180)

    //224,236,237
    //233,241,242
    fun addCircle(latlng: LatLng?, radius: Double, aMap: AMap): Circle {
        val options = CircleOptions()
        options.strokeWidth(0f)
        options.fillColor(FILL_COLOR)
        options.strokeColor(STROKE_COLOR)
        options.center(latlng)
        options.radius(radius)
        return aMap.addCircle(options)
    }

    fun getStrokeColor(alpha: Int): Int {
        return Color.argb(alpha, 3, 145, 200)
    }

    fun getFillColor(alpha: Int): Int {
        return Color.argb(alpha, 204, 0, 0)
    }
}