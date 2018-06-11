package com.timper.lonelysword.base;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.HasActivityInjector;
import dagger.android.support.HasSupportFragmentInjector;

import static dagger.internal.Preconditions.checkNotNull;

/**
 * User: tangpeng.yang
 * Date: 15/03/2018
 * Description:
 * FIXME
 */
public class AppInjector {

  public static void init(Application application) {
    application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
      @Override public void onActivityCreated(Activity activity, Bundle bundle) {
        handleActivity(activity);
      }

      @Override public void onActivityStarted(Activity activity) {

      }

      @Override public void onActivityResumed(Activity activity) {

      }

      @Override public void onActivityPaused(Activity activity) {

      }

      @Override public void onActivityStopped(Activity activity) {

      }

      @Override public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

      }

      @Override public void onActivityDestroyed(Activity activity) {

      }
    });
  }

  private static void handleActivity(Activity activity) {
    if (activity instanceof HasSupportFragmentInjector) {
      AndroidInjection.inject(activity);
    }
  }

  /**
   * Injects {@code activity} if an associated {@link AndroidInjector} implementation can be found,
   * otherwise throws an {@link IllegalArgumentException}.
   *
   * @throws RuntimeException if the {@link Application} doesn't implement {@link
   * HasActivityInjector}.
   */
  public static void inject(Activity activity) {
    checkNotNull(activity, "activity");
    HasActivityInjector application = (HasActivityInjector) activity.getApplication();
    //if (application == null) {
    //  application = activity.getApplication();
    //}
    if (!(application instanceof HasActivityInjector)) {
      throw new RuntimeException(String.format("%s does not implement %s", application.getClass().getCanonicalName(),
          HasActivityInjector.class.getCanonicalName()));
    }

    AndroidInjector<Activity> activityInjector = ((HasActivityInjector) application).activityInjector();
    checkNotNull(activityInjector, "%s.activityInjector() returned null", application.getClass().getCanonicalName());

    activityInjector.inject(activity);
  }
}
