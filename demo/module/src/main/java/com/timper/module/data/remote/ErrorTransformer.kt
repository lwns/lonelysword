package com.timper.module.data.remote

import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableOnSubscribe
import io.reactivex.FlowableTransformer
import java.lang.RuntimeException

/**
 * @author op
 * @version 1.0
 * @description 对response数据进行拦截处理
 * @createDate 2016/3/24
 */
class ErrorTransformer<T> : FlowableTransformer<BaseResponse<T>, T> {

    override fun apply(upstream: Flowable<BaseResponse<T>>): Flowable<T> {
        return upstream.flatMap { it -> flatResponse(it) }
    }

    private fun flatResponse(response: BaseResponse<T>): Flowable<T> {
        return Flowable.create(FlowableOnSubscribe { subscriber ->
            if (!subscriber.isCancelled) {
                when (response.errorCode) {
                    0 -> {
                        response.data?.let {
                            subscriber.onNext(it)
                        }
                        subscriber.onComplete()
                    }
                    else -> {
                        subscriber.onError(RuntimeException(response.errorMsg ?: "加载失败"))
                        return@FlowableOnSubscribe
                    }
                }
            }
        }, BackpressureStrategy.BUFFER)
    }
}
