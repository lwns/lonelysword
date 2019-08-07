package com.timper.lonelysword.app.data.remote;

import com.timper.lib.di.BaseResponse;
import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import org.reactivestreams.Publisher;

/**
 * User: tangpeng.yang
 * Date: 2019-08-07
 * Description:
 * FIXME
 */
public class SigleErrorTransformer<T> implements FlowableTransformer<BaseResponse<T>,T> {
    @Override
    public Publisher<T> apply(Flowable<BaseResponse<T>> upstream) {
        return null;
    }
}
