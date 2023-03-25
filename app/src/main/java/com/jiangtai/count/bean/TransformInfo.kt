package com.jiangtai.count.bean

import org.litepal.crud.LitePalSupport

class TransformInfo : LitePalSupport() {
     var message = ""
     var time = ""
     var type = ""

     companion object{
          const val LOG_DATA = 0x99
     }
}