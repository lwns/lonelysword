package com.timper.lonelysword.base;

import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import com.timper.lonelysword.Lonelysword;
import com.timper.lonelysword.Unbinder;
import com.timper.lonelysword.annotations.apt.Dagger;
import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasFragmentInjector;
import dagger.android.support.HasSupportFragmentInjector;
import javax.inject.Inject;

/**
 * User: tangpeng.yang
 * Date: 17/05/2018
 * Description:
 * FIXME
 */
public abstract class AppActivity<V extends AppViewModel, T extends ViewDataBinding> extends AppCompatActivity
    implements HasFragmentInjector, HasSupportFragmentInjector {

  protected Unbinder unbinder;
  public FragmentManager fragmentManager;
  public T binding;
  public V viewModel;
  @Inject public ViewModelFactor<V> factor;

  @Inject DispatchingAndroidInjector<Fragment> supportFragmentInjector;
  @Inject DispatchingAndroidInjector<android.app.Fragment> frameworkFragmentInjector;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    AndroidInjection.inject(this);
    fragmentManager = getSupportFragmentManager();
    unbinder = Lonelysword.bind(this);
    unbinder.beforeViews();
    super.onCreate(savedInstanceState);
    unbinder.initViews();
    unbinder.afterViews();
    getLifecycle().addObserver(unbinder);
  }

  @Override public AndroidInjector<Fragment> supportFragmentInjector() {
    return supportFragmentInjector;
  }

  @Override public AndroidInjector<android.app.Fragment> fragmentInjector() {
    return frameworkFragmentInjector;
  }

  public void addFragment(int containerViewId, Fragment fragment) {
    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
    fragmentTransaction.add(containerViewId, fragment);
    fragmentTransaction.commit();
  }

  @Override protected void onResume() {
    super.onResume();
    unbinder.onResume();
  }

  @Override protected void onStart() {
    super.onStart();
    unbinder.onStart();
  }

  @Override protected void onPause() {
    super.onPause();
    unbinder.onPause();
  }

  @Override protected void onStop() {
    super.onStop();
    unbinder.onStop();
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    unbinder.onDestroy();
    unbinder.unbind();
  }
}
