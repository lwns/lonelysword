package com.timper.lonelysword.aspectj;

import android.util.Log;
import com.timper.lonelysword.Lonelysword;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * User: tangpeng.yang
 * Date: 25/05/2018
 * Description: 检查是否login 切面
 * FIXME
 */
@Aspect public class CheckLoginAspect {

  @Pointcut("execution(@com.timper.lonelysword.annotations.aspectj.CheckLogin * *(..))")//方法切入点
  public void methodAnnotated() {
  }

  @Around("methodAnnotated()")//在连接点进行方法替换
  public void aroundJoinPoint(ProceedingJoinPoint joinPoint) throws Throwable {
    Log.i("CheckLoginAspect", "1");
    if (Lonelysword.loginBinder != null) {
      Log.i("CheckLoginAspect", "2");
      Lonelysword.loginBinder.checkLogin();
    }
    joinPoint.proceed();//执行原方法
  }
}

