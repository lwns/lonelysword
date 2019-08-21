package com.timper.lonelysword.aspectj;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * User: tangpeng.yang
 * Date: 25/05/2018
 * Description: 防止重复单点 切面
 * FIXME
 */
@Aspect public class SingleClickAspect {

  private static final int MIN_CLICK_DELAY_TIME = 1000;
  private static long lastClickTime;

  @Pointcut("execution(@com.timper.lonelysword.annotations.aspectj.SingleClick * *(..))") public void methodAnnotated() {
  }

  @Around("methodAnnotated()") public void aroundJoinPoint(ProceedingJoinPoint joinPoint) throws Throwable {
    long curClickTime = System.currentTimeMillis();
    if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
      joinPoint.proceed();
      lastClickTime = curClickTime;
    }
  }
}
