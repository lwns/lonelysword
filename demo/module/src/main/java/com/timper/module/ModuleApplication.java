package com.timper.module;

import com.timper.lonelysword.annotations.apt.DaggerApplication;
import com.timper.lonelysword.dagger.DaggerMultiModule;
import com.timper.module.di.DaggerModuleComponent;
import dagger.android.AndroidInjector;

/**
 * User: tangpeng.yang
 * Date: 2019/3/16
 * Description:
 * FIXME
 */
@DaggerApplication
public class ModuleApplication extends DaggerMultiModule {
    @Override
    protected AndroidInjector<? extends DaggerMultiModule> applicationInjector() {
        return DaggerModuleComponent.builder().create(this);
    }
}
