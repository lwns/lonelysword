package com.timper.lonelysword.app.data;

import com.timper.lonelysword.annotations.apt.UseCase;
import com.timper.lonelysword.app.data.remote.BaseResponse;
import com.timper.lonelysword.app.data.remote.ErrorTransformer;
import com.timper.lonelysword.app.data.remote.SigleErrorTransformer;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

/**
 * User: tangpeng.yang
 * Date: 31/05/2018
 * Description:
 * FIXME
 */
@UseCase(ignore = BaseResponse.class,transformer = ErrorTransformer.class)
public interface MainRepository {

    Flowable<BaseResponse<String>> getUser(String hellow);

    Completable getUsers(String hellow);


//    Observable<String> getUsers1(String hellow);
//
//    Maybe<String> getUsers2(String hellow);

    @UseCase(ignore = BaseResponse.class,transformer = SigleErrorTransformer.class)
    Single<BaseResponse<String>> getUsers3(String hellow);
}
