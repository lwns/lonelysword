package com.timper.lib.di;

import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;

import javax.inject.Singleton;

import lonelysword.di.AppModule$$module;

@Singleton
@Component(modules = {
        AndroidSupportInjectionModule.class, DataModule.class, AppModule$$module.class
})
public interface LibComponent extends AndroidInjector<LibDagger> {

    @Component.Builder
    abstract class Builder extends AndroidInjector.Builder<LibDagger> {
    }
}