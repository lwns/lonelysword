package com.timper.lonelysword.aspectj;

import android.util.Log;
import java.util.concurrent.TimeUnit;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

/**
 * User: tangpeng.yang
 * Date: 25/05/2018
 * Description: 方法执行时间切面
 * FIXME
 */
@Aspect public class TimeAspect {

  @Pointcut("execution(@com.timper.lonelysword.annotations.aspectj.Time * *(..))") public void methodAnnotated() {
  }

  @Pointcut("execution(@com.timper.lonelysword.annotations.aspectj.Time *.new(..))") public void constructorAnnotated() {
  }

  @Around("methodAnnotated() || constructorAnnotated()") public Object aroundJoinPoint(ProceedingJoinPoint joinPoint)
      throws Throwable {
    MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
    String className = methodSignature.getDeclaringType().getSimpleName();
    String methodName = methodSignature.getName();
    long startTime = System.nanoTime();
    Object result = joinPoint.proceed();
    StringBuilder keyBuilder = new StringBuilder();
    keyBuilder.append(methodName + ":");
    for (Object obj : joinPoint.getArgs()) {
      if (obj instanceof String) {
        keyBuilder.append((String) obj);
      } else if (obj instanceof Class) keyBuilder.append(((Class) obj).getSimpleName());
    }
    String key = keyBuilder.toString();
    Log.w("time", (className + "." + key + joinPoint.getArgs().toString() + " --->:" + "[" + (TimeUnit.NANOSECONDS.toMillis(
        System.nanoTime() - startTime)) + "ms]"));// 打印时间差
    return result;
  }
}
