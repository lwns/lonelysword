package com.timper.lonelysword.app.data;

import com.timper.lonelysword.annotations.apt.UseCase;
import io.reactivex.*;

/**
 * User: tangpeng.yang
 * Date: 31/05/2018
 * Description:
 * FIXME
 */
public interface MainRepository {
    @UseCase
    Flowable<String> getUser(String hellow);

    @UseCase
    Completable getUsers(String hellow);


    @UseCase
    Observable<String> getUsers1(String hellow);

    @UseCase
    Maybe<String> getUsers2(String hellow);

    @UseCase
    Single<String> getUsers3(String hellow);
}
