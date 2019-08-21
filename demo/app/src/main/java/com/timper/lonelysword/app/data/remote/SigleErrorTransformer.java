package com.timper.lonelysword.app.data.remote;

import io.reactivex.*;

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
