package com.timper.lonelysword.app;

import android.widget.Toast;
import com.timper.lonelysword.LoginBinder;
import com.timper.lonelysword.Lonelysword;
import com.timper.lonelysword.app.di.DaggerAppComponent;
import com.timper.lonelysword.base.App;
import dagger.android.AndroidInjector;
import dagger.android.DaggerApplication;

/**
 * User: tangpeng.yang
 * Date: 04/06/2018
 * Description:
 * FIXME
 */
public class MainApplication extends App {

  @Override public void onCreate() {
    super.onCreate();

    Lonelysword.setLoginBinder(new LoginBinder() {
      @Override public void checkLogin() {
        Toast.makeText(MainApplication.this, "go to login", Toast.LENGTH_SHORT).show();
      }
    });
  }

  @Override protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
    return DaggerAppComponent.builder().create(this);
  }
}
