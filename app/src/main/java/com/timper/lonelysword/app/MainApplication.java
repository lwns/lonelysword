package com.timper.lonelysword.app;

import android.app.Activity;
import android.app.Application;
import android.widget.Toast;
import com.timper.lonelysword.LoginBinder;
import com.timper.lonelysword.Lonelysword;
import com.timper.lonelysword.app.di.DaggerAppComponent;
import com.timper.lonelysword.base.App;
import dagger.android.AndroidInjector;
import dagger.android.DaggerApplication;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import javax.inject.Inject;

/**
 * User: tangpeng.yang
 * Date: 04/06/2018
 * Description:
 * FIXME
 */
public class MainApplication extends App {

  //@Inject DispatchingAndroidInjector<Activity> activityDispatchingAndroidInjector;

  @Override public void onCreate() {
    super.onCreate();

    //DaggerAppComponent.builder().application(this).build().inject(this);
    Lonelysword.setLoginBinder(new LoginBinder() {
      @Override public void checkLogin() {
        Toast.makeText(MainApplication.this, "go to login", Toast.LENGTH_SHORT).show();
      }
    });
  }

  //@Override public AndroidInjector<Activity> activityInjector() {
  //  return activityDispatchingAndroidInjector;
  //}
  @Override protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
    return DaggerAppComponent.builder().create(this);
  }
}
