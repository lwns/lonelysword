//package com.timper.lib.di.feature;
//
//import android.content.Context;
//import android.content.Intent;
//import com.timper.lib.R2;
//import com.timper.lib.databinding.ActLibBinding;
//import com.timper.lonelysword.annotations.apt.AfterViews;
//import com.timper.lonelysword.annotations.apt.Dagger;
//import com.timper.lonelysword.annotations.apt.RootView;
//import com.timper.lonelysword.base.AppActivity;
//
///**
// * User: tangpeng.yang
// * Date: 2019/3/20
// * Description:
// * FIXME
// */
//@Dagger
//@RootView(R2.layout.act_lib)
//public class LibActivity extends AppActivity<LibViewModel, ActLibBinding> {
//
//    public static final void instance(Context context) {
//        Intent intent = new Intent(context, LibActivity.class);
//        context.startActivity(intent);
//    }
//
//    @AfterViews
//    void view() {
//
//    }
//}
