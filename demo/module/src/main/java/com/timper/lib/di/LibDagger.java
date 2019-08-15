package com.timper.lib.di;

import com.timper.lonelysword.annotations.apt.DaggerApplication;
import com.timper.lonelysword.dagger.DaggerMultiModule;
import dagger.android.AndroidInjector;

/**
 * User: tangpeng.yang
 * Date: 2019/3/16
 * Description:
 * FIXME
 */
@DaggerApplication
public class LibDagger extends DaggerMultiModule {
    @Override
    protected AndroidInjector<? extends DaggerMultiModule> applicationInjector() {
        return DaggerLibComponent.builder().create(this);
    }
}
