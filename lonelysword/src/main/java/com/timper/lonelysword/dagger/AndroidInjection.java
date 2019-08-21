package com.timper.lonelysword.dagger;

import android.app.Activity;
import android.app.Application;
import android.app.Fragment;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentProvider;
import android.content.Context;
import android.util.Log;
import dagger.android.AndroidInjector;
import dagger.android.HasActivityInjector;
import dagger.android.HasBroadcastReceiverInjector;
import dagger.android.HasContentProviderInjector;
import dagger.android.HasFragmentInjector;
import dagger.android.HasServiceInjector;
import dagger.internal.Beta;

import static dagger.internal.Preconditions.checkNotNull;

/**
 * User: tangpeng.yang
 * Date: 2019/3/16
 * Description:
 * FIXME
 */
@Beta
public final class AndroidInjection {
    private static final String TAG = "dagger.android";

    /**
     * inject到activity类，参考dagger2  AndroidInjection类
     * @param activity
     */
    public static void inject(Activity activity) {
        checkNotNull(activity, "activity");
        HasActivityInjector hasActivityInjector = null;
        if (Warehouse.daggerMultiModules.containsKey(activity.getClass().getName())) {
            hasActivityInjector = Warehouse.daggerMultiModules.get(activity.getClass().getName());
        } else {
            Application application = activity.getApplication();
            if (!(application instanceof HasActivityInjector)) {
                throw new RuntimeException(
                        String.format(
                                "%s does not implement %s",
                                application.getClass().getCanonicalName(),
                                HasActivityInjector.class.getName()));
            }
            hasActivityInjector = ((HasActivityInjector) application);
        }

        AndroidInjector<Activity> activityInjector =
                hasActivityInjector.activityInjector();
        checkNotNull(
                activityInjector,
                "%s.activityInjector() returned null",
                hasActivityInjector.getClass().getCanonicalName());

        activityInjector.inject(activity);
    }

    /**
     * inject到fragment类，参考dagger2  AndroidInjection类
     * @param fragment
     */
    public static void inject(Fragment fragment) {
        checkNotNull(fragment, "fragment");
        HasFragmentInjector hasFragmentInjector = findHasFragmentInjector(fragment);
        Log.d(
                TAG,
                String.format(
                        "An injector for %s was found in %s",
                        fragment.getClass().getCanonicalName(),
                        hasFragmentInjector.getClass().getCanonicalName()));

        AndroidInjector<Fragment> fragmentInjector = hasFragmentInjector.fragmentInjector();
        checkNotNull(
                fragmentInjector,
                "%s.fragmentInjector() returned null",
                hasFragmentInjector.getClass().getCanonicalName());

        fragmentInjector.inject(fragment);
    }

    private static HasFragmentInjector findHasFragmentInjector(Fragment fragment) {
        Fragment parentFragment = fragment;
        while ((parentFragment = parentFragment.getParentFragment()) != null) {
            if (parentFragment instanceof HasFragmentInjector) {
                return (HasFragmentInjector) parentFragment;
            }
        }
        Activity activity = fragment.getActivity();
        if (activity instanceof HasFragmentInjector) {
            return (HasFragmentInjector) activity;
        }
        if (activity.getApplication() instanceof HasFragmentInjector) {
            return (HasFragmentInjector) activity.getApplication();
        }
        throw new IllegalArgumentException(
                String.format("No injector was found for %s", fragment.getClass().getCanonicalName()));
    }

    /**
     * inject到service类，参考dagger2  AndroidInjection类
     * @param service
     */
    public static void inject(Service service) {
        checkNotNull(service, "service");
        Application application = service.getApplication();
        if (!(application instanceof HasServiceInjector)) {
            throw new RuntimeException(
                    String.format(
                            "%s does not implement %s",
                            application.getClass().getCanonicalName(),
                            HasServiceInjector.class.getCanonicalName()));
        }

        AndroidInjector<Service> serviceInjector = ((HasServiceInjector) application).serviceInjector();
        checkNotNull(
                serviceInjector,
                "%s.serviceInjector() returned null",
                application.getClass().getCanonicalName());

        serviceInjector.inject(service);
    }

    /**
     * inject到broadcastReceiver类，参考dagger2  AndroidInjection类
     * @param broadcastReceiver
     */
    public static void inject(BroadcastReceiver broadcastReceiver, Context context) {
        checkNotNull(broadcastReceiver, "broadcastReceiver");
        checkNotNull(context, "context");
        Application application = (Application) context.getApplicationContext();
        if (!(application instanceof HasBroadcastReceiverInjector)) {
            throw new RuntimeException(
                    String.format(
                            "%s does not implement %s",
                            application.getClass().getCanonicalName(),
                            HasBroadcastReceiverInjector.class.getCanonicalName()));
        }

        AndroidInjector<BroadcastReceiver> broadcastReceiverInjector =
                ((HasBroadcastReceiverInjector) application).broadcastReceiverInjector();
        checkNotNull(
                broadcastReceiverInjector,
                "%s.broadcastReceiverInjector() returned null",
                application.getClass().getCanonicalName());

        broadcastReceiverInjector.inject(broadcastReceiver);
    }

    /**
     * inject到contentProvider类，参考dagger2  AndroidInjection类
     * @param contentProvider
     */
    public static void inject(ContentProvider contentProvider) {
        checkNotNull(contentProvider, "contentProvider");
        Application application = (Application) contentProvider.getContext().getApplicationContext();
        if (!(application instanceof HasContentProviderInjector)) {
            throw new RuntimeException(
                    String.format(
                            "%s does not implement %s",
                            application.getClass().getCanonicalName(),
                            HasContentProviderInjector.class.getCanonicalName()));
        }

        AndroidInjector<ContentProvider> contentProviderInjector =
                ((HasContentProviderInjector) application).contentProviderInjector();
        checkNotNull(
                contentProviderInjector,
                "%s.contentProviderInjector() returned null",
                application.getClass().getCanonicalName());

        contentProviderInjector.inject(contentProvider);
    }

    private AndroidInjection() {
    }
}

