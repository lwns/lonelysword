package com.timper.lonelysword.app.feature.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import com.timper.lib.di.feature.LibActivity;
import com.timper.lonelysword.annotations.apt.AfterViews;
import com.timper.lonelysword.annotations.apt.Dagger;
import com.timper.lonelysword.annotations.apt.RootView;
import com.timper.lonelysword.app.R;
import com.timper.lonelysword.app.databinding.ActMainBinding;
import com.timper.lonelysword.app.feature.main.dialog.MainDialog;
import com.timper.lonelysword.app.utils.StatusBarUtil;
import com.timper.lonelysword.base.AppActivity;

/**
 * User: tangpeng.yang
 * Date: 23/07/2018
 * Description:
 * FIXME
 */
@Dagger
@RootView(R.layout.act_main)
public class MainActivity extends AppActivity<MainViewModel, ActMainBinding> {

    MainDialog dialog = new MainDialog();

    public void onClick() {
//    MainDialog dialog = new MainDialog();
//    dialog.show(getSupportFragmentManager(), "adfasfsf");
        LibActivity.instance(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        StatusBarUtil.setTransparent(this);
        StatusBarUtil.setLightMode(this);
        super.onCreate(savedInstanceState);

    }

    @AfterViews
    void sfsf() {
        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LibActivity.instance(MainActivity.this);
//                MainDialog.instance()
//                        .show(getSupportFragmentManager());
            }
        });
    }
}
