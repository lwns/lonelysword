package com.timper.lonelysword.base.dialog;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import java.util.Arrays;

/**
 * User: tangpeng.yang
 * Date: 2018/12/4
 * Description:
 * FIXME
 */
public class DialogBuilder {
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


