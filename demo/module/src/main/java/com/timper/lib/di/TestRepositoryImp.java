package com.timper.lib.di;

import io.reactivex.Flowable;

import javax.inject.Inject;

/**
 * User: tangpeng.yang
 * Date: 2019-07-30
 * Description:
 * FIXME
 */
public class TestRepositoryImp implements TestRepository {
    @Inject
    public TestRepositoryImp() {
    }

    @Override
    public Flowable<String> getUseCase() {
        return null;
    }
}