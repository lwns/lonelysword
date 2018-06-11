package com.timper.lonelysword.app.di;

import com.timper.lonelysword.ActivityScope;
import com.timper.lonelysword.app.SplashActivity;
import com.timper.lonelysword.app.di.module.MainModule;
import com.timper.lonelysword.app.di.module.MainSubModule;
import com.timper.lonelysword.app.di.module.SplashModule;
import com.timper.lonelysword.app.di.module.SplashSubModule;
import com.timper.lonelysword.app.feature.MainActivity;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module public abstract class ActivityModule {

  @ActivityScope @ContributesAndroidInjector(modules = { SplashModule.class, SplashSubModule.class })
  abstract SplashActivity bindSplashActivity();

  @ActivityScope @ContributesAndroidInjector(modules = { MainModule.class, MainSubModule.class })
  abstract MainActivity bindMainActivity();
}
