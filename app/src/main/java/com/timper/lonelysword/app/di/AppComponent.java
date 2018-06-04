package com.timper.lonelysword.app.di;

import com.timper.lonelysword.app.MainApplication;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;
import javax.inject.Singleton;

@Singleton @Component(modules = {
    AndroidSupportInjectionModule.class
}) public interface AppComponent extends AndroidInjector<MainApplication> {

  @Component.Builder
  abstract class Builder extends AndroidInjector.Builder<MainApplication> {}
}
