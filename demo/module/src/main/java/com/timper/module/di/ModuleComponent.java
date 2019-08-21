package com.timper.module.di;

import com.timper.module.ModuleApplication;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;

import javax.inject.Singleton;

import lonelysword.di.AppModule$$module;

@Singleton
@Component(modules = {
        AndroidSupportInjectionModule.class, DataModule.class, AppModule$$module.class
})
public interface ModuleComponent extends AndroidInjector<ModuleApplication> {

    @Component.Builder
    abstract class Builder extends AndroidInjector.Builder<ModuleApplication> {
    }
}