package com.timper.lonelysword.dagger;

import android.app.Activity;
import android.app.Application;
import android.app.Fragment;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentProvider;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import dagger.android.HasBroadcastReceiverInjector;
import dagger.android.HasContentProviderInjector;
import dagger.android.HasFragmentInjector;
import dagger.android.HasServiceInjector;
import dagger.android.support.HasSupportFragmentInjector;
import javax.inject.Inject;

/**
 * User: tangpeng.yang
 * Date: 2019/3/15
 * Description:
 * FIXME
 */
public abstract class DaggerMultiModule implements HasActivityInjector, HasFragmentInjector, HasSupportFragmentInjector, HasServiceInjector, HasBroadcastReceiverInjector, HasContentProviderInjector {

  @Inject
  DispatchingAndroidInjector<Activity>                        activityInjector;
  @Inject
  DispatchingAndroidInjector<BroadcastReceiver>               broadcastReceiverInjector;
  @Inject
  DispatchingAndroidInjector<android.app.Fragment>            fragmentInjector;
  @Inject
  DispatchingAndroidInjector<android.support.v4.app.Fragment> supportFragmentInjector;
  @Inject
  DispatchingAndroidInjector<Service>                         serviceInjector;
  @Inject
  DispatchingAndroidInjector<ContentProvider>                 contentProviderInjector;
  private volatile boolean needToInject = true;

  /**
   * Implementations should return an {@link AndroidInjector} for the concrete {@link
   * dagger.android.DaggerApplication}. Typically, that injector is a {@link dagger.Component}.
   */
  protected abstract AndroidInjector<? extends DaggerMultiModule> applicationInjector();

  public void initDaggerMulti(Application application) {
    injectIfNecessary();
  }

  private void injectIfNecessary() {
    if (needToInject) {
      synchronized (this) {
        if (needToInject) {
          @SuppressWarnings("unchecked")
          AndroidInjector<DaggerMultiModule> applicationInjector = (AndroidInjector<DaggerMultiModule>) applicationInjector();
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
  public DispatchingAndroidInjector<Fragment> fragmentInjector() {
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
  public AndroidInjector<android.support.v4.app.Fragment> supportFragmentInjector() {
    return supportFragmentInjector;
  }

  @Override
  public AndroidInjector<ContentProvider> contentProviderInjector() {
    injectIfNecessary();
    return contentProviderInjector;
  }
}