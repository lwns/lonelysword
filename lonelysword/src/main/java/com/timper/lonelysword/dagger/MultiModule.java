package com.timper.lonelysword.dagger;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * User: tangpeng.yang
 * Date: 2019/3/16
 * Description:
 * FIXME
 */
public interface MultiModule {

  /**
   * Load routes to input
   *
   * @param map input
   */
  void saveAndroidModule(Map<String, DaggerMultiModule> map) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException;
}
