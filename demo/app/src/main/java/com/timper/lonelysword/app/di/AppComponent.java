package com.timper.lonelysword.app.di;

import com.timper.lonelysword.app.MainApplication;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;
import lonelysword.di.AppModule$$app;

import javax.inject.Singleton;

@Singleton
@Component(modules = {
        AndroidSupportInjectionModule.class, DataModule.class, UiModule.class, AppModule$$app.class
})
public interface AppComponent extends AndroidInjector<MainApplication> {

    @Component.Builder
    abstract class Builder extends AndroidInjector.Builder<MainApplication> {
    }
}