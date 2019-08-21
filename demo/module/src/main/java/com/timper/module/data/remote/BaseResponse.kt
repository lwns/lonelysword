package com.timper.module.data.remote

/**
 * User: tangpeng.yang
 * Date: 2019-08-06
 * Description:
 * FIXME
 */

data class BaseResponse<T>(
    val data: T,
    val errorCode: Int?,
    val errorMsg: String?
)
