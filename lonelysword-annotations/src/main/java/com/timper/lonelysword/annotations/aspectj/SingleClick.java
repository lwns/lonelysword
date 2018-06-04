package com.timper.lonelysword.annotations.aspectj;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * User: tangpeng.yang
 * Date: 25/05/2018
 * Description:
 * FIXME
 */
@Retention(RetentionPolicy.CLASS) @Target(ElementType.METHOD) public @interface SingleClick {
}
