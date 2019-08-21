// Generated code from lonely sword. Do not modify!
package com.timper.lonelysword.app.di;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import com.timper.lonelysword.app.feature.main.dialog.MainDialog;
import com.timper.lonelysword.app.feature.splash.SplashActivity;
import dagger.Binds;
import dagger.Module;

@Module
public abstract class MainDialogModule {
  @Binds
  public abstract Fragment provideSplashActivity(MainDialog dialog);
}
