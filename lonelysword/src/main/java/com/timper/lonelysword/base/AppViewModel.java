package com.timper.lonelysword.base;

import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

/**
 * User: tangpeng.yang
 * Date: 03/07/2018
 * Description:
 * FIXME
 */
public abstract class AppViewModel<T extends ViewDataBinding> extends ViewModel {

  protected Application application;

  protected AppActivity activity;

  protected Bundle bundle;

  protected T binding;

  public FragmentManager fragmentManager;

  public AppViewModel(AppActivity activity) {
    this.activity = activity;
    bundle = activity.getIntent().getExtras();
    application = activity.getApplication();
  }

  public void setBinding(T binding) {
    this.binding = binding;
  }

  public void setFragmentManager(FragmentManager fragmentManager) {
    this.fragmentManager = fragmentManager;
  }

  /**
   * Adds a {@link Fragment} to this activity's layout.
   *
   * @param containerViewId The container view to where add the fragment.
   * @param fragment The fragment to be added.
   */
  public void addFragment(int containerViewId, Fragment fragment) {
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    fragmentTransaction.add(containerViewId, fragment);
    fragmentTransaction.commit();
  }

  public void afterViews() {

  }

  public void onStart() {
  }

  public void onResume() {
  }

  public void onPause() {
  }

  public void onStop() {
  }

  public void onDestroy() {
  }
}
