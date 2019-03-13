package com.timper.lonelysword.base;

import androidx.databinding.ViewDataBinding;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import com.timper.lonelysword.Lonelysword;
import com.timper.lonelysword.Unbinder;
import com.timper.lonelysword.dagger.HasSupportFragmentInjector;
import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasFragmentInjector;
import javax.inject.Inject;

/**
 * User: tangpeng.yang
 * Date: 17/05/2018
 * Description:
 * FIXME
 */
public abstract class AppActivity<V extends AppViewModel, T extends ViewDataBinding> extends AppCompatActivity implements HasFragmentInjector, HasSupportFragmentInjector {

  private Unbinder           unbinder;
  public  FragmentManager    fragmentManager;
  public  T                  binding;
  public  V                  viewModel;
  @Inject
  public  ViewModelFactor<V> factor;

  @Inject
  DispatchingAndroidInjector<Fragment>             supportFragmentInjector;
  @Inject
  DispatchingAndroidInjector<android.app.Fragment> frameworkFragmentInjector;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    AndroidInjection.inject(this);
    fragmentManager = getSupportFragmentManager();
    unbinder = Lonelysword.bind(this);
    unbinder.beforeViews();
    super.onCreate(savedInstanceState);
    unbinder.initViews();
    unbinder.afterViews();
    getLifecycle().addObserver(unbinder);
  }

  @Override
  public AndroidInjector<Fragment> supportFragmentInjector() {
    return supportFragmentInjector;
  }

  @Override
  public AndroidInjector<android.app.Fragment> fragmentInjector() {
    return frameworkFragmentInjector;
  }

  public void addFragment(int containerViewId, Fragment fragment) {
    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
    fragmentTransaction.add(containerViewId, fragment);
    fragmentTransaction.commit();
  }

  @Override
  protected void onResume() {
    super.onResume();
    unbinder.onResume();
  }

  @Override
  protected void onStart() {
    super.onStart();
    unbinder.onStart();
  }

  @Override
  protected void onPause() {
    super.onPause();
    unbinder.onPause();
  }

  @Override
  protected void onStop() {
    super.onStop();
    unbinder.onStop();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    unbinder.onDestroy();
    unbinder.unbind();
  }
}
