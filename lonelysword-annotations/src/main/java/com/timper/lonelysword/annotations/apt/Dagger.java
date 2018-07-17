package com.timper.lonelysword.annotations.apt;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * User: tangpeng.yang
 * Date: 29/05/2018
 * Description:
 * FIXME
 */
@Documented @Retention(CLASS) @Target({ ElementType.TYPE }) public @interface Dagger {
  Class<?> value() default Object.class;
}
