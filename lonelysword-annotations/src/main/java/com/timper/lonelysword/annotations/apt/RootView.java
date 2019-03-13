package com.timper.lonelysword.annotations.apt;

import androidx.annotation.LayoutRes;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.CLASS;

@Documented @Retention(CLASS) @Target({ ElementType.TYPE }) public @interface RootView {
  @LayoutRes int value() default -1;
}
