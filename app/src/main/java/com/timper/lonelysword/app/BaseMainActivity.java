//package com.timper.lonelysword.app;
//
//import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
//import dagger.android.AndroidInjection;
//import javax.inject.Inject;
//import junit.framework.Test;
//
///**
// * User: tangpeng.yang
// * Date: 08/06/2018
// * Description:
// * FIXME
// */
//public class BaseMainActivity extends AppCompatActivity {
//
//  @Inject Test test;
//
//  @Override protected void onCreate(Bundle savedInstanceState) {
//    AndroidInjection.inject(this);
//    super.onCreate(savedInstanceState);
//    setContentView(R.layout.act_main);
//  }
//}