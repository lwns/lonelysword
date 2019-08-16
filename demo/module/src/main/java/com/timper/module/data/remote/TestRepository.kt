package com.timper.module.data.remote

import io.reactivex.Flowable

/**
 * User: tangpeng.yang
 * Date: 2019/3/20
 * Description:
 * FIXME
 */
interface TestRepository {

    fun getUseCase(): Flowable<String>

}
