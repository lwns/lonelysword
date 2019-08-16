package com.timper.lonelysword.app.feature.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;
import com.timper.lonelysword.annotations.apt.*;
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

//    @Inject
//    MainDialog dialog;



    @DisableNetwork
    void disableNetwork(){
        Toast.makeText(this,"断网了",Toast.LENGTH_SHORT).show();
    }

    @EnableNetwork
    void enableNetwork(){
        Toast.makeText(this,"连上网啦",Toast.LENGTH_SHORT).show();
    }

    public void clickbutton() {
    MainDialog dialog = new MainDialog();
    dialog.show(getSupportFragmentManager(), "adfasfsf");
//        LibActivity.Companion.instance(this);
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
                clickbutton();
            }
        });
    }
}
