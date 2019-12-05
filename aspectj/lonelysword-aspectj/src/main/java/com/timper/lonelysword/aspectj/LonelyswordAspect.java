package com.timper.lonelysword.aspectj;

/**
 * User: tangpeng.yang
 * Date: 2019-11-18
 * Description:
 * FIXME
 */
public class LonelyswordAspect {


    public static LoginBinder loginBinder;

    public static void setLoginBinder(LoginBinder loginBinder) {
        LonelyswordAspect.loginBinder = loginBinder;
    }
}
