package com.timper.lonelysword.base;

import android.arch.lifecycle.AndroidViewModel;
import com.timper.lonelysword.context.App;

/**
 * User: tangpeng.yang
 * Date: 03/07/2018
 * Description:
 * FIXME
 */
public abstract class AppViewModel extends AndroidViewModel {

  public AppViewModel() {
    super(App.context());
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
