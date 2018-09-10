package com.timper.lonelysword.app.feature.main;

import com.timper.lonelysword.annotations.apt.Dagger;
import com.timper.lonelysword.annotations.apt.RootView;
import com.timper.lonelysword.app.R;
import com.timper.lonelysword.app.databinding.ActMainBinding;
import com.timper.lonelysword.base.AppActivity;

/**
 * User: tangpeng.yang
 * Date: 23/07/2018
 * Description:
 * FIXME
 */
@Dagger @RootView(R.layout.act_main) public class MainActivity extends AppActivity<MainViewModel, ActMainBinding> {

}
