package com.timper.lonelysword.annotations.apt;

/**
 * User: tangpeng.yang
 * Date: 05/06/2018
 * Description:
 * FIXME
 */

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.CLASS;
import static java.lang.annotation.RetentionPolicy.SOURCE;

@Target(FIELD) @Retention(CLASS) public @interface ViewModel {
  String value() default "";
}
