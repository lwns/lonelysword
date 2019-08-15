package com.timper.lonelysword.app.data.remote;

import com.timper.lib.di.BaseResponse;
import io.reactivex.*;
import org.reactivestreams.Publisher;

/**
 * User: tangpeng.yang
 * Date: 2019-08-07
 * Description:
 * FIXME
 */
public class SigleErrorTransformer<T> implements SingleTransformer<BaseResponse<T>,T> {
    @Override
    public SingleSource<T> apply(Single<BaseResponse<T>> upstream) {
        return null;
    }
}
