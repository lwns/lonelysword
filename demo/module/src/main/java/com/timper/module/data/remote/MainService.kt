package com.timper.module.data.remote

import com.timper.lonelysword.annotations.apt.UseCase
import com.timper.module.data.bean.Article
import com.timper.module.data.bean.BaseList
import io.reactivex.Flowable
import retrofit2.http.GET

/**
 * User: tangpeng.yang
 * Date: 2019-08-20
 * Description:
 * FIXME
 */
@UseCase(ignore = BaseResponse::class, transformer = ErrorTransformer::class)
interface MainService {

    @GET("/article/list/0/json")
    fun getArticles() : Flowable<BaseResponse<BaseList<Article>>>


    @GET("/article/list/0/json?cid=60")
    fun getTops() : Flowable<BaseResponse<BaseList<Article>>>
}
