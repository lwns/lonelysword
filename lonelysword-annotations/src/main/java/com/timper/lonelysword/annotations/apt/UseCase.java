package com.timper.lonelysword.annotations.apt;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * User: tangpeng.yang
 * Date: 29/05/2018
 * Description:
 * FIXME
 */
@Documented
@Target({METHOD, TYPE})
@Retention(CLASS)
public @interface UseCase {

    /**
     * 需要过滤的类型
     *
     * @return 类型
     */
    Class<?> name() default Object.class;

    /**
     * 变换类型
     *
     * @return
     */
    Class<?> transformer() default Object.class;

    ;

}
