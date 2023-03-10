package com.jiangtai.team.util

import android.graphics.drawable.BitmapDrawable
import com.cld.mapapi.map.MarkerOptions
import com.cld.mapapi.model.LatLng
import com.cld.nv.map.overlay.impl.FrameStorage
import com.jiangtai.team.R
import com.jiangtai.team.application.App

class MineMarkerOptions(val index:Int) : MarkerOptions() {

    private val drawableList  = ArrayList<FrameStorage>()
    init {
        drawableList.clear()
        drawableList.addAll(getResourceList())
    }
    override fun position(latLng: LatLng?): MarkerOptions {
        this.imageDesc.frames = drawableList
        this.imageDesc.imageData = this.imageDesc.frames[index].bitmapDrawable
        return super.position(latLng)
    }



    private fun getResourceList():ArrayList<FrameStorage>{
        val arrayList = ArrayList<FrameStorage>()
        App.getMineContext()?.let {
            val frameStorage = FrameStorage()
            frameStorage.bitmapDrawable =
                it.resources.getDrawable(R.mipmap.location_big_blue) as BitmapDrawable
            val frameStorage2 = FrameStorage()
            frameStorage2.bitmapDrawable =
                it.resources.getDrawable(R.mipmap.location_big) as BitmapDrawable
            arrayList.add(frameStorage)
            arrayList.add(frameStorage2)
        }

        return arrayList
    }
}