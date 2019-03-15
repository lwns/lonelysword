package com.timper.lonelysword.base;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.databinding.ViewDataBinding;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Display;
import android.view.Gravity;
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
import com.timper.lonelysword.context.App;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.AndroidSupportInjection;
import dagger.android.support.HasSupportFragmentInjector;
import java.util.Arrays;
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

  private static Handler handler = new Handler(Looper.getMainLooper());

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

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    getDialog().setDismissMessage(null);
    getDialog().setCancelMessage(null);//防止在dismiss的时候，发送handle消息，导致内存泄漏
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
    unbinder = Lonelysword.bind(this, container);
    unbinder.beforeViews();

    ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    final FrameLayout rootView = new FrameLayout(getContext());
    if (dialogBuilder.getGravity() == Gravity.BOTTOM) {
      rootView.setPadding(0, 0, 0, getNavigationBarHeight(getActivity()));//当底部有虚拟按键的时候添加
    }
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
      handler.sendMessage(handler.obtainMessage());//解决内存message.obj = activity 内存泄漏问题
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

      DetachableOnKeyListener onKeyListener = DetachableOnKeyListener.wrap(new DialogInterface.OnKeyListener() {
        @Override
        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
          if (dialogBuilder.isCancelable() && keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            dismiss();
            return true;
          }
          return false;
        }
      });

      dialog.setOnKeyListener(onKeyListener);
      onKeyListener.clearOnDetach(dialog);
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

  /**
   * 小米mix3检查是否存在 NavigationBar
   * 原理是拿到 横屏时候的允许宽度与 内屏的真实高度进行对比来推算是否存在NavigationBar
   */
  public static boolean isNavigationBarShow(Activity activity) {
    Display display = activity.getWindowManager()
                              .getDefaultDisplay();
    Point outSmallestSize = new Point();
    Point outLargestSize = new Point();
    display.getCurrentSizeRange(outSmallestSize, outLargestSize);
    display.getRealSize(outSmallestSize);
    return outSmallestSize.y != outLargestSize.x;
  }

  public static int getNavigationBarHeight(Activity activity) {
    if (!isNavigationBarShow(activity)) {
      return 0;
    }
    Resources resources = App.context()
                             .getResources();
    int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
    return resources.getDimensionPixelSize(resourceId);
  }

  /**
   * 防止内存泄漏
   */
  public final static class DetachableOnKeyListener implements DialogInterface.OnKeyListener {

    public static DetachableOnKeyListener wrap(DialogInterface.OnKeyListener delegate) {
      return new DetachableOnKeyListener(delegate);
    }

    private DialogInterface.OnKeyListener delegateOrNull;

    private DetachableOnKeyListener(DialogInterface.OnKeyListener delegate) {
      this.delegateOrNull = delegate;
    }

    public void clearOnDetach(Dialog dialog) {
      dialog.getWindow()
            .getDecorView()
            .getViewTreeObserver()
            .addOnWindowAttachListener(new ViewTreeObserver.OnWindowAttachListener() {
              @Override
              public void onWindowAttached() {
              }

              @Override
              public void onWindowDetached() {
                delegateOrNull = null;
              }
            });
    }

    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
      if (delegateOrNull != null) {
        return delegateOrNull.onKey(dialog, keyCode, event);
      }
      return false;
    }
  }


  public static class DialogBuilder {
    private static final int INVALID = -1;

    private final int[] margin  = new int[4];
    private final int[] padding = new int[4];

    private int     gravity      = Gravity.BOTTOM;
    private boolean isCancelable = true;
    private boolean isFullScreen = false;
    private boolean isBlur       = false;

    private AnimatorSet inAnimation;
    private AnimatorSet outAnimation;

    private final FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
      ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM);

    public DialogBuilder() {
      Arrays.fill(margin, INVALID);
    }

    public static DialogBuilder newDialog() {
      return new DialogBuilder();
    }

    public int getGravity() {
      return gravity;
    }

    public DialogBuilder setGravity(int gravity) {
      this.gravity = gravity;
      layoutParams.gravity = gravity;
      return this;
    }

    public boolean isCancelable() {
      return isCancelable;
    }

    /**
     * Define if the dialog is cancelable and should be closed when back pressed or click outside is pressed
     *
     * @return DialogBuilder
     */
    public DialogBuilder setCancelable(boolean isCancelable) {
      this.isCancelable = isCancelable;
      return this;
    }

    public boolean isFullScreen() {
      return isFullScreen;
    }

    /**
     * @return DialogBuilder
     */
    public DialogBuilder setFullScreen(boolean fullScreen) {
      isFullScreen = fullScreen;
      return this;
    }

    public boolean isBlur() {
      return isBlur;
    }

    public DialogBuilder setBlur(boolean blur) {
      isBlur = blur;
      return this;
    }

    /**
     * Add margins to your dialog. They are set to 0 except when gravity is center. In that case basic margins
     * are applied
     *
     * @return DialogBuilder
     */
    public DialogBuilder setMargin(int left, int top, int right, int bottom) {
      this.margin[0] = left;
      this.margin[1] = top;
      this.margin[2] = right;
      this.margin[3] = bottom;
      return this;
    }

    /**
     * Set paddings for the dialog content
     *
     * @return DialogBuilder
     */
    public DialogBuilder setPadding(int left, int top, int right, int bottom) {
      this.padding[0] = left;
      this.padding[1] = top;
      this.padding[2] = right;
      this.padding[3] = bottom;
      return this;
    }

    public FrameLayout.LayoutParams getLayoutParams() {
      layoutParams.bottomMargin = this.margin[3];
      layoutParams.leftMargin = this.margin[0];
      layoutParams.rightMargin = this.margin[2];
      layoutParams.topMargin = this.margin[1];
      return layoutParams;
    }

    public void setInAnimation(AnimatorSet inAnimation) {
      this.inAnimation = inAnimation;
    }

    public void setOutAnimation(AnimatorSet outAnimation) {
      this.outAnimation = outAnimation;
    }

    public AnimatorSet getInAnimation(View view) {
      return (inAnimation == null) ? getInAnim(this.gravity, view) : inAnimation;
    }

    public AnimatorSet getOutAnimation(View view) {
      return (outAnimation == null) ? getOutAnim(this.gravity, view) : outAnimation;
    }

    private AnimatorSet getInAnim(int gravity, View view) {
      AnimatorSet animatorSet = new AnimatorSet();
      switch (gravity) {
        case Gravity.BOTTOM:
          view.setAlpha(1f);
          animatorSet.playTogether(ObjectAnimator.ofFloat(view, "TranslationY", view.getHeight(), 0));
          break;
        case Gravity.CENTER:
          animatorSet.playTogether(ObjectAnimator.ofFloat(view, "scaleX", 0.9f, 1f), ObjectAnimator.ofFloat(view, "scaleY", 0.9f, 1f),
            ObjectAnimator.ofFloat(view, "alpha", view.getAlpha(), 1.0f));
          break;
        case Gravity.TOP:
          view.setAlpha(1f);
          animatorSet.playTogether(ObjectAnimator.ofFloat(view, "TranslationY", -view.getHeight(), 0));
          break;
        default:
          animatorSet.playTogether(ObjectAnimator.ofFloat(view, "scaleX", 0.9f, 1f), ObjectAnimator.ofFloat(view, "scaleY", 0.9f, 1f));
      }
      return animatorSet;
    }

    private AnimatorSet getOutAnim(int gravity, View view) {
      AnimatorSet animatorSet = new AnimatorSet();
      switch (gravity) {
        case Gravity.BOTTOM:
          animatorSet.playTogether(ObjectAnimator.ofFloat(view, "TranslationY", 0, view.getHeight()));
          break;
        case Gravity.CENTER:
          animatorSet.playTogether(ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.9f), ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.9f),
            ObjectAnimator.ofFloat(view, "alpha", view.getAlpha(), 0f));
          break;
        case Gravity.TOP:
          animatorSet.playTogether(ObjectAnimator.ofFloat(view, "TranslationY", 0, -view.getHeight()));
          break;
        default:
          animatorSet.playTogether(ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.9f), ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.9f));
      }
      return animatorSet;
    }
  }
}
