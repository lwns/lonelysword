package com.timper.lonelysword.app.data.remote;

import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import org.reactivestreams.Publisher;

/**
 * User: tangpeng.yang
 * Date: 2019-08-20
 * Description:
 * FIXME
 */
public class ErrorTransformer<T> implements FlowableTransformer<BaseResponse<T>, T> {


    @Override
    public Publisher<T> apply(Flowable<BaseResponse<T>> upstream) {
        return null;
    }
}