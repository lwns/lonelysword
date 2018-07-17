package com.timper.lonelysword.base;

import android.arch.lifecycle.ViewModelProvider;
import javax.inject.Inject;

/**
 * User: tangpeng.yang
 * Date: 19/03/2018
 * Description:
 * FIXME
 */
public class ViewModelFactor<V extends AppViewModel> implements ViewModelProvider.Factory {

  private V modelAdapter;

  @Inject public ViewModelFactor(V modelAdapter) {
    this.modelAdapter = modelAdapter;
  }

  @Override public <T extends android.arch.lifecycle.ViewModel> T create(Class<T> modelClass) {
    if (modelClass.isAssignableFrom(modelAdapter.getClass())) {
      return (T) modelAdapter;
    }
    throw new IllegalArgumentException("Unknown class name");
  }
}

