package com.timper.lonelysword.base;

import io.reactivex.disposables.CompositeDisposable;

/**
 * User: tangpeng.yang
 * Date: 19/03/2018
 * Description:
 * FIXME
 */
public abstract class ModelAdapter extends android.arch.lifecycle.ViewModel {

  private CompositeDisposable mCompositeDisposable;

  public ModelAdapter() {
    this.mCompositeDisposable = new CompositeDisposable();
  }

  @Override protected void onCleared() {
    mCompositeDisposable.dispose();
    super.onCleared();
  }

  public CompositeDisposable getCompositeDisposable() {
    return mCompositeDisposable;
  }
}
