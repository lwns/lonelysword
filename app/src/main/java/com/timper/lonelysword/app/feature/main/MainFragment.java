//package com.timper.lonelysword.app.feature.main;
//
//import android.databinding.ObservableField;
//import android.widget.Toast;
//import com.timper.lonelysword.annotations.apt.AfterViews;
//import com.timper.lonelysword.annotations.apt.BeforeViews;
//import com.timper.lonelysword.annotations.apt.DisableNetwork;
//import com.timper.lonelysword.annotations.apt.EnableNetwork;
//import com.timper.lonelysword.annotations.apt.ModelAdapter;
//import com.timper.lonelysword.annotations.apt.RootView;
//import com.timper.lonelysword.annotations.apt.ViewModel;
//import com.timper.lonelysword.annotations.aspectj.CheckLogin;
//import com.timper.lonelysword.annotations.aspectj.SingleClick;
//import com.timper.lonelysword.annotations.aspectj.Time;
//import com.timper.lonelysword.app.R;
//import com.timper.lonelysword.app.databinding.FrgMainBinding;
//import com.timper.lonelysword.app.feature.MainModelAdapter;
//import com.timper.lonelysword.app.feature.MainViewModel;
//import com.timper.lonelysword.base.AppFragment;
//import com.timper.lonelysword.base.ModelAdapterFactor;
//import javax.inject.Inject;
//
///**
// * User: tangpeng.yang
// * Date: 31/05/2018
// * Description:
// * FIXME
// */
//@ViewModel @RootView(R.layout.frg_main) public class MainFragment extends AppFragment<FrgMainBinding> {
//
//  @Inject ModelAdapterFactor<MainModelAdapter> factor;
//  @ModelAdapter("factor") MainModelAdapter adapter;
//
//  public final ObservableField<String> abc = new ObservableField<>("SDFASDFSADFSADF");
//  //@Inject @ViewModel MainViewModel viewModel;
//
//  @BeforeViews void beforViews() {
//    Toast.makeText(getActivity(), "beforviews", Toast.LENGTH_LONG).show();
//  }
//
//  @AfterViews void AfterViews() {
//
//    Toast.makeText(getActivity(), "AfterViews", Toast.LENGTH_LONG).show();
//  }
//
//  @DisableNetwork void disable() {
//    Toast.makeText(getActivity(), "disable", Toast.LENGTH_LONG).show();
//  }
//
//  @EnableNetwork @CheckLogin void enable() {
//    Toast.makeText(getActivity(), "enable", Toast.LENGTH_LONG).show();
//  }
//
//  @SingleClick @Time void click() {
//    Toast.makeText(getActivity(), "SingleClick", Toast.LENGTH_SHORT).show();
//  }
//}
