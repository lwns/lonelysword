package com.timper.lonelysword;

import androidx.annotation.UiThread;
import android.view.View;
import androidx.lifecycle.LifecycleObserver;

/** An unbinder contract that will unbind views when called. */
public interface Unbinder extends LifecycleObserver {

  void beforeViews();

  View initViews();

  void afterViews();

  void onResume();

  void onStart();

  void onPause();

  void onStop();

  void onDestroy();

  @UiThread void unbind();

  Unbinder EMPTY = new Unbinder() {
    @Override public void beforeViews() {

    }

    @Override public View initViews() {
      return null;
    }

    @Override public void afterViews() {

    }

    @Override public void onResume() {

    }

    @Override public void onStart() {

    }

    @Override public void onPause() {

    }

    @Override public void onStop() {

    }

    @Override public void onDestroy() {

    }

    @Override public void unbind() {
    }
  };
}
