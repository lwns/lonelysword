package com.timper.lonelysword.app.data.repository;

import com.timper.lonelysword.app.data.MainRepository;
import com.timper.lonelysword.app.data.remote.BaseResponse;
import io.reactivex.*;

import javax.inject.Inject;

/**
 * User: tangpeng.yang
 * Date: 04/06/2018
 * Description:
 * FIXME
 */
public class MainRepositoryImp implements MainRepository {
    @Inject
    public MainRepositoryImp() {
    }


    @Override
    public Flowable<BaseResponse<String>> getUser(String hellow) {
        return null;
    }

    @Override
    public Completable getUsers(String hellow) {
        return null;
    }

//    @Override
//    public Observable<String> getUsers1(String hellow) {
//        return null;
//    }
//
//    @Override
//    public Maybe<String> getUsers2(String hellow) {
//        return null;
//    }

    @Override
    public Single<BaseResponse<String>> getUsers3(String hellow) {
        return null;
    }
}
