package com.timper.lonelysword.app;

import android.widget.Toast;
import com.timper.lonelysword.LoginBinder;
import com.timper.lonelysword.Lonelysword;
import com.timper.lonelysword.app.di.DaggerAppComponent;
import com.timper.lonelysword.base.BaseApplication;
import com.timper.lonelysword.dagger.DaggerApplication;
import dagger.android.AndroidInjector;

/**
 * User: tangpeng.yang
 * Date: 04/06/2018
 * Description:
 * FIXME
 */
public class MainApplication extends BaseApplication {

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
