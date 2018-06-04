package com.timper.lonelysword.annotations.apt;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * User: tangpeng.yang
 * Date: 28/05/2018
 * Description:
 * FIXME
 */
@Documented @Target(FIELD) @Retention(CLASS) public @interface ModelAdapter {
}
