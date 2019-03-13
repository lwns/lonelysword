package com.timper.lonelysword.dagger;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentProvider;
import androidx.fragment.app.Fragment;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import dagger.android.HasBroadcastReceiverInjector;
import dagger.android.HasContentProviderInjector;
import dagger.android.HasFragmentInjector;
import dagger.android.HasServiceInjector;
import dagger.internal.Beta;
import javax.inject.Inject;

/**
 * User: tangpeng.yang
 * Date: 2018/12/26
 * Description:
 * FIXME
 */
@Beta
public abstract class DaggerApplication extends Application implements HasActivityInjector, HasFragmentInjector, HasSupportFragmentInjector, HasServiceInjector, HasBroadcastReceiverInjector, HasContentProviderInjector {

  @Inject
  DispatchingAndroidInjector<Activity>             activityInjector;
  @Inject
  DispatchingAndroidInjector<BroadcastReceiver>    broadcastReceiverInjector;
  @Inject
  DispatchingAndroidInjector<android.app.Fragment> fragmentInjector;
  @Inject
  DispatchingAndroidInjector<Fragment>             supportFragmentInjector;
  @Inject
  DispatchingAndroidInjector<Service>              serviceInjector;
  @Inject
  DispatchingAndroidInjector<ContentProvider>      contentProviderInjector;
  private volatile boolean needToInject = true;

  @Override
  public void onCreate() {
    super.onCreate();
    injectIfNecessary();
  }

  /**
   * Implementations should return an {@link AndroidInjector} for the concrete {@link
   * dagger.android.DaggerApplication}. Typically, that injector is a {@link dagger.Component}.
   */
  protected abstract AndroidInjector<? extends DaggerApplication> applicationInjector();

  /**
   * Lazily injects the {@link dagger.android.DaggerApplication}'s members. Injection cannot be performed in {@link
   * Application#onCreate()} since {@link android.content.ContentProvider}s' {@link
   * android.content.ContentProvider#onCreate() onCreate()} method will be called first and might
   * need injected members on the application. Injection is not performed in the the constructor, as
   * that may result in members-injection methods being called before the constructor has completed,
   * allowing for a partially-constructed instance to escape.
   */
  private void injectIfNecessary() {
    if (needToInject) {
      synchronized (this) {
        if (needToInject) {
          @SuppressWarnings("unchecked")
          AndroidInjector<DaggerApplication> applicationInjector = (AndroidInjector<DaggerApplication>) applicationInjector();
          applicationInjector.inject(this);
          if (needToInject) {
            throw new IllegalStateException("The AndroidInjector returned from applicationInjector() did not inject the " + "DaggerApplication");
          }
        }
      }
    }
  }

  @Inject
  void setInjected() {
    needToInject = false;
  }

  @Override
  public DispatchingAndroidInjector<Activity> activityInjector() {
    return activityInjector;
  }

  @Override
  public DispatchingAndroidInjector<android.app.Fragment> fragmentInjector() {
    return fragmentInjector;
  }

  @Override
  public DispatchingAndroidInjector<BroadcastReceiver> broadcastReceiverInjector() {
    return broadcastReceiverInjector;
  }

  @Override
  public DispatchingAndroidInjector<Service> serviceInjector() {
    return serviceInjector;
  }

  @Override
  public AndroidInjector<Fragment> supportFragmentInjector() {
    return supportFragmentInjector;
  }

  // injectIfNecessary is called here but not on the other *Injector() methods because it is the
  // only one that should be called (in AndroidInjection.inject(ContentProvider)) before
  // Application.onCreate()
  @Override
  public AndroidInjector<ContentProvider> contentProviderInjector() {
    injectIfNecessary();
    return contentProviderInjector;
  }
}
