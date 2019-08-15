package com.timper.lib.di;

import com.timper.lonelysword.data.executor.PostExecutionThread;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;

import javax.inject.Inject;

/**
 * User: tangpeng.yang
 * Date: 20/03/2018
 * Description:
 * FIXME
 */
public class UiThread implements PostExecutionThread {

  @Inject public UiThread() {
  }

  @Override public Scheduler getScheduler() {
    return AndroidSchedulers.mainThread();
  }
}
