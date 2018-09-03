package com.timper.lonelysword.base;

import io.reactivex.disposables.CompositeDisposable;

/**
 * User: tangpeng.yang
 * Date: 19/03/2018
 * Description:
 * FIXME
 */
public abstract class AppModelAdapter extends android.arch.lifecycle.ViewModel {

  private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

  @Override protected void onCleared() {
    mCompositeDisposable.dispose();
    super.onCleared();
  }

  public CompositeDisposable getCompositeDisposable() {
    return mCompositeDisposable;
  }
}
