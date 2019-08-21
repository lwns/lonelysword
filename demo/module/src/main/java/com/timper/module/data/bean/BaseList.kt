package com.timper.module.data.bean

/**
 * User: tangpeng.yang
 * Date: 2019-08-21
 * Description:
 * FIXME
 */

data class BaseList<T>(
    val curPage: Int?,
    val datas: List<T>?,
    val offset: Int?,
    val over: Boolean?,
    val pageCount: Int?,
    val size: Int?,
    val total: Int?
)