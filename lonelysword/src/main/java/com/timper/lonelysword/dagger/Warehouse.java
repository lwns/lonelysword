package com.timper.lonelysword.dagger;

import java.util.HashMap;
import java.util.Map;

/**
 * User: tangpeng.yang
 * Date: 2019/3/16
 * Description: 将记录整个app所有的DaggerMultiModule。有多少module就就记录多少DaggerMultiModule。
 * FIXME
 */
public class Warehouse {
    // 将记录整个app所有的DaggerMultiModule。有多少module就就记录多少DaggerMultiModule。
    public final static Map<String, DaggerMultiModule> daggerMultiModules = new HashMap<>();
}
