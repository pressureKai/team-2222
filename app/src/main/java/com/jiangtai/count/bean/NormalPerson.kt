package com.jiangtai.count.bean

import org.litepal.crud.LitePalSupport

/**
 * Created by heCunCun on 2021/3/12
 * 日常计划表
 */
data class NormalPerson(
    val name: String? = "",
    val personId: String? = "",
) : LitePalSupport() {
    val id: Long = 1

    override fun toString(): String {
        return "name is $name \n " +
                "personId is $personId \n"
    }
}
