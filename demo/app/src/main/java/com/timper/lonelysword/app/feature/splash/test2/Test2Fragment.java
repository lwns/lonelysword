package com.timper.lonelysword.app.feature.splash.test2;

import com.timper.lonelysword.annotations.apt.AfterViews;
import com.timper.lonelysword.annotations.apt.RootView;
import com.timper.lonelysword.app.R;
import com.timper.lonelysword.app.databinding.FrgTestTooBinding;
import com.timper.lonelysword.base.AppFragment;

/**
 * User: tangpeng.yang
 * Date: 06/07/2018
 * Description:
 * FIXME
 */
@RootView(R.layout.frg_test_too) public class Test2Fragment<V extends Test2ViewModel> extends AppFragment<V, FrgTestTooBinding> {

  @AfterViews
  void afterViews(){
  }
}
