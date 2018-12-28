package com.timper.lonelysword.base.dialog;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.ViewDataBinding;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import com.timper.lonelysword.Lonelysword;
import com.timper.lonelysword.Unbinder;
import com.timper.lonelysword.base.AppViewModel;
import com.timper.lonelysword.base.ViewModelFactor;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.AndroidSupportInjection;
import dagger.android.support.HasSupportFragmentInjector;
import javax.inject.Inject;

/**
 * User: tangpeng.yang
 * Date: 2018/12/4
 * Description:
 * FIXME
 */
public abstract class AppDialog<V extends AppViewModel, T extends ViewDataBinding> extends DialogFragment implements HasSupportFragmentInjector {

  DialogBuilder dialogBuilder = initBuilder();

  public T                  binding;
  @Inject
  public V                  viewModel;
  @Inject
  public ViewModelFactor<V> factor;

  public View view;

  private View fadeView;

  private FrameLayout contentView;

  private Unbinder unbinder;

  private boolean animationing = false;

  @Inject
  DispatchingAndroidInjector<Fragment> supportFragmentInjector;

  @Override
  public void onAttach(Context context) {
    AndroidSupportInjection.inject(this);
    super.onAttach(context);
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setStyle(DialogFragment.STYLE_NO_FRAME, 0);
  }

  @Override
  public AndroidInjector<Fragment> supportFragmentInjector() {
    return supportFragmentInjector;
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    unbinder.afterViews();
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
    unbinder = Lonelysword.bind(this, container);
    unbinder.beforeViews();

    ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    final FrameLayout rootView = new FrameLayout(getContext());
    rootView.setLayoutParams(layoutParams);
    rootView.setBackgroundColor(Color.parseColor("#00000000"));

    FrameLayout.LayoutParams fadeParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
    fadeView = new View(getContext());
    fadeView.setLayoutParams(fadeParams);
    fadeView.setBackgroundColor(Color.parseColor("#66000000"));
    fadeView.setAlpha(0);
    fadeView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (dialogBuilder.isCancelable()) {
          dismiss();
        }
      }
    });
    rootView.addView(fadeView);

    contentView = new FrameLayout(getContext());
    contentView.setLayoutParams(dialogBuilder.getLayoutParams());
    rootView.setBackgroundColor(Color.parseColor("#00000000"));
    unbinder.initViews();
    view.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
      }
    });
    view.setAlpha(0);
    contentView.addView(view);
    rootView.addView(contentView);

    view.post(new Runnable() {
      @Override
      public void run() {
        showAnim();
      }
    });
    return rootView;
  }

  private ObjectAnimator alphaOut(View view) {
    ObjectAnimator alphaOut = ObjectAnimator.ofFloat(view, "alpha", view.getAlpha(), 0f);
    return alphaOut;
  }

  private ObjectAnimator alphaIn(View view) {
    ObjectAnimator alphaIn = ObjectAnimator.ofFloat(view, "alpha", view.getAlpha(), 1f);
    return alphaIn;
  }

  public void show(FragmentManager fragmentManager) {

    show(fragmentManager, this.getClass()
                              .getCanonicalName());
  }

  @Override
  public void show(FragmentManager manager, String tag) {
    super.show(manager, tag);
  }

  @Override
  public int show(FragmentTransaction transaction, String tag) {
    int backStackId = super.show(transaction, tag);
    return backStackId;
  }

  private void showAnim() {
    AnimatorSet animatorSet = new AnimatorSet();

    animatorSet.playTogether(alphaIn(fadeView), dialogBuilder.getInAnimation(view));
    animatorSet.setDuration(300);
    animatorSet.setInterpolator(new DecelerateInterpolator());
    animatorSet.addListener(new Animator.AnimatorListener() {
      @Override
      public void onAnimationStart(Animator animation) {
        animationing = true;
      }

      @Override
      public void onAnimationEnd(Animator animation) {
        animationing = false;
      }

      @Override
      public void onAnimationCancel(Animator animation) {

      }

      @Override
      public void onAnimationRepeat(Animator animation) {

      }
    });
    animatorSet.start();
  }

  @Override
  public void dismiss() {
    if (!animationing) {
      AnimatorSet animatorSet = new AnimatorSet();
      animatorSet.playTogether(alphaOut(fadeView), dialogBuilder.getOutAnimation(view));
      animatorSet.setDuration(300);
      animatorSet.setInterpolator(new DecelerateInterpolator());
      animatorSet.addListener(new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {
          animationing = true;
        }

        @Override
        public void onAnimationEnd(Animator animation) {
          AppDialog.super.dismiss();
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
      });
      animatorSet.start();
    }
  }

  public abstract DialogBuilder initBuilder();

  @Override
  public void onStart() {
    Dialog dialog = getDialog();
    if (dialog != null) {
      dialog.getWindow()
            .clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
      Window window = dialog.getWindow();

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//沉浸式主题设置
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        window.setStatusBarColor(Color.TRANSPARENT);
      } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
      }

      WindowManager.LayoutParams lp = window.getAttributes();
      lp.gravity = dialogBuilder.getGravity();
      // 宽度持平
      lp.width = WindowManager.LayoutParams.MATCH_PARENT;
      lp.height = WindowManager.LayoutParams.MATCH_PARENT;
      window.setAttributes(lp);

      dialog.setCancelable(dialogBuilder.isCancelable());

      dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
        @Override
        public boolean onKey(DialogInterface anInterface, int keyCode, KeyEvent event) {
          if (dialogBuilder.isCancelable() && keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            dismiss();
            return true;
          }
          return false;
        }
      });
    }
    super.onStart();
    unbinder.onStart();
  }

  @Override
  public void onResume() {
    super.onResume();
    unbinder.onResume();
  }

  @Override
  public void onPause() {
    super.onPause();
    unbinder.onPause();
  }

  @Override
  public void onStop() {
    super.onStop();
    unbinder.onStop();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    unbinder.onDestroy();
    unbinder.unbind();
  }
}
