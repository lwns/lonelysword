package com.timper.lib.di;

import com.timper.lonelysword.annotations.apt.UseCase;
import io.reactivex.Flowable;

/**
 * User: tangpeng.yang
 * Date: 2019/3/20
 * Description:
 * FIXME
 */
public interface TestRepository {

    @UseCase
    Flowable<String> getUseCase();
}
