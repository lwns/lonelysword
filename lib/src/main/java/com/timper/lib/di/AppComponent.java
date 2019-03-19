package com.timper.lib.di;

import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;
import javax.inject.Singleton;
import lonelysword.di.AppModule$$lib;

@Singleton
@Component(modules = {
  AndroidSupportInjectionModule.class, AppModule$$lib.class
})
public interface AppComponent extends AndroidInjector<LibApp> {

  @Component.Builder
  abstract class Builder extends AndroidInjector.Builder<LibApp> {}
}