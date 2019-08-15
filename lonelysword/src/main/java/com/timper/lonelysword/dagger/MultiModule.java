package com.timper.lonelysword.dagger;

import android.app.Application;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * User: tangpeng.yang
 * Date: 2019/3/16
 * Description: 多模块接口，每一个module会生成一个MultiModule实现类，来记录当前module注入的类
 * FIXME
 */
public interface MultiModule {

  /**
   * @param map 将DaggerMultiModule保存至map
   * @param application
   */
  void saveAndroidModule(Map<String, DaggerMultiModule> map, Application application) throws Exception;
}
