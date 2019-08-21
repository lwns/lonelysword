package com.timper.lonelysword.thread;

import java.util.concurrent.CountDownLatch;

/**
 * User: tangpeng.yang
 * Date: 2019/3/16
 * Description: 参考Aroute
 * FIXME
 */
public class CancelableCountDownLatch extends CountDownLatch {
    public CancelableCountDownLatch(int count) {
        super(count);
    }

    public void cancel() {
        while (getCount() > 0) {
            countDown();
        }
    }
}
