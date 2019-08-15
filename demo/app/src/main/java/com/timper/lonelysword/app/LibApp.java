package com.timper.lonelysword.app;

import android.app.Application;
import com.timper.lonelysword.annotations.apt.DaggerApplication;
import com.timper.lonelysword.app.di.DaggerAppComponent;
import com.timper.lonelysword.dagger.DaggerMultiModule;
import dagger.android.AndroidInjector;

/**
 * User: tangpeng.yang
 * Date: 2019/3/16
 * Description:
 * FIXME
 */
@DaggerApplication
public class LibApp extends DaggerMultiModule {
    @Override
    protected AndroidInjector<? extends DaggerMultiModule> applicationInjector() {
        return DaggerAppComponent.builder().create(this);
    }

}
