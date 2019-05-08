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
     * Injects {@code activity} if an associated {@link AndroidInjector} implementation can be found,
     * otherwise throws an {@link IllegalArgumentException}.
     *
     * @throws RuntimeException if the {@link Application} doesn't implement {@link
     *                          HasActivityInjector}.
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
     * Injects {@code fragment} if an associated {@link AndroidInjector} implementation can be found,
     * otherwise throws an {@link IllegalArgumentException}.
     *
     * <p>Uses the following algorithm to find the appropriate {@code AndroidInjector<Fragment>} to
     * use to inject {@code fragment}:
     *
     * <ol>
     * <li>Walks the parent-fragment hierarchy to find the a fragment that implements {@link
     * HasFragmentInjector}, and if none do
     * <li>Uses the {@code fragment}'s {@link Fragment#getActivity() activity} if it implements
     * {@link HasFragmentInjector}, and if not
     * <li>Uses the {@link android.app.Application} if it implements {@link HasFragmentInjector}.
     * </ol>
     * <p>
     * If none of them implement {@link HasFragmentInjector}, a {@link IllegalArgumentException} is
     * thrown.
     *
     * @throws IllegalArgumentException if no parent fragment, activity, or application implements
     *                                  {@link HasFragmentInjector}.
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
     * Injects {@code service} if an associated {@link AndroidInjector} implementation can be found,
     * otherwise throws an {@link IllegalArgumentException}.
     *
     * @throws RuntimeException if the {@link Application} doesn't implement {@link
     *                          HasServiceInjector}.
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
     * Injects {@code broadcastReceiver} if an associated {@link AndroidInjector} implementation can
     * be found, otherwise throws an {@link IllegalArgumentException}.
     *
     * @throws RuntimeException if the {@link Application} from {@link
     *                          Context#getApplicationContext()} doesn't implement {@link HasBroadcastReceiverInjector}.
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
     * Injects {@code contentProvider} if an associated {@link AndroidInjector} implementation can be
     * found, otherwise throws an {@link IllegalArgumentException}.
     *
     * @throws RuntimeException if the {@link Application} doesn't implement {@link
     *                          HasContentProviderInjector}.
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

