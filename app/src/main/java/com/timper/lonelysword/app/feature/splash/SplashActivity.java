package com.timper.lonelysword.app.feature.splash;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.FrameLayout;
import butterknife.BindView;
import com.timper.lib.TestActivity;
import com.timper.lonelysword.app.R;

//@Dagger @RootView(R.layout.act_splash) public class SplashActivity extends AppActivity<SplashViewModel, ActSplashBinding>
//    implements SplashViewModel.Navigation {
//
//  @Override public void gotoMain() {
//
//  }
//}
public class SplashActivity extends TestActivity implements SplashViewModel.Navigation {

  @BindView(R.id.fl_content) FrameLayout flContent;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override public void gotoMain() {

  }
}


