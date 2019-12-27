package com.timper.lonelysword.base;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.ViewDataBinding;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.timper.lonelysword.Lonelysword;
import com.timper.lonelysword.Unbinder;
import com.timper.lonelysword.dagger.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasFragmentInjector;
import dagger.android.support.HasSupportFragmentInjector;

import javax.inject.Inject;

/**
 * User: tangpeng.yang
 * Date: 17/05/2018
 * Description: mvvm activity
 * FIXME
 */
public abstract class AppActivity<V extends AppViewModel, T extends ViewDataBinding> extends AppCompatActivity
        implements HasFragmentInjector, HasSupportFragmentInjector {

    private Fragment currentFragment;
    public FragmentManager fragmentManager;
    public T binding;
    public V viewModel;
    @Inject
    public ViewModelFactor<V> factor;
    @Inject
    DispatchingAndroidInjector<Fragment> supportFragmentInjector;
    @Inject
    DispatchingAndroidInjector<android.app.Fragment> frameworkFragmentInjector;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        fragmentManager = getSupportFragmentManager();
        Unbinder unbinder = Lonelysword.bind(this);
        super.onCreate(savedInstanceState);
        unbinder.afterViews();
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return supportFragmentInjector;
    }

    @Override
    public AndroidInjector<android.app.Fragment> fragmentInjector() {
        return frameworkFragmentInjector;
    }

    protected void addFragment(int frameLayoutId, Fragment fragment) {
        if (fragment != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            if (fragment.isAdded()) {
                if (currentFragment != null) {
                    transaction.hide(currentFragment)
                            .show(fragment);
                } else {
                    transaction.show(fragment);
                }
            } else {
                if (currentFragment != null) {
                    transaction.hide(currentFragment)
                            .add(frameLayoutId, fragment);
                } else {
                    transaction.add(frameLayoutId, fragment);
                }
            }
            currentFragment = fragment;
            transaction.commit();
        }
    }

    protected void replaceFragment(int frameLayoutId, Fragment fragment) {
        if (fragment != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(frameLayoutId, fragment);
            transaction.commit();
        }
    }
}
