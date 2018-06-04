package com.timper.lonelysword.base;

import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import com.timper.lonelysword.Lonelysword;
import com.timper.lonelysword.Unbinder;
import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import javax.inject.Inject;

/**
 * User: tangpeng.yang
 * Date: 17/05/2018
 * Description:
 * FIXME
 */
public abstract class AppActivity<T extends ViewDataBinding> extends AppCompatActivity implements HasSupportFragmentInjector {

  @Inject DispatchingAndroidInjector<Fragment> dispatchingAndroidInjector;

  public T binding;

  protected Unbinder unbinder;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    unbinder = Lonelysword.bind(this);
    //AndroidInjection.inject(this);
    unbinder.beforeViews();
    super.onCreate(savedInstanceState);
    unbinder.initViews();
    unbinder.afterViews();
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

  @Override public AndroidInjector<Fragment> supportFragmentInjector() {
    return dispatchingAndroidInjector;
  }
}
