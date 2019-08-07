package com.timper.lonelysword.app.data;

import com.timper.lib.di.BaseResponse;
import com.timper.lonelysword.annotations.apt.UseCase;
import com.timper.lonelysword.annotations.apt.internal.Ignore;
import com.timper.lonelysword.app.data.remote.ErrorTransformer;
import com.timper.lonelysword.app.data.remote.SigleErrorTransformer;
import io.reactivex.*;

/**
 * User: tangpeng.yang
 * Date: 31/05/2018
 * Description:
 * FIXME
 */
@UseCase(name = BaseResponse.class,transformer = ErrorTransformer.class)
public interface MainRepository {

    Flowable<BaseResponse<String>> getUser(String hellow);

    Completable getUsers(String hellow);


//    Observable<String> getUsers1(String hellow);
//
//    Maybe<String> getUsers2(String hellow);

    @UseCase(name = BaseResponse.class,transformer = SigleErrorTransformer.class)
    Single<BaseResponse<String>> getUsers3(String hellow);
}
