package com.timper.lonelysword.annotations.apt.internal;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * User: tangpeng.yang
 * Date: 2019-08-06
 * Description:
 * FIXME
 */
@Retention(RUNTIME) @Target(FIELD)
public @interface Ignore {

    /**
     * 需要过滤的类型
     * @return 类型
     */
    Class<?> name();

    /**
     * 变换类型
     * @return
     */
    Class<?> transformer();

}
