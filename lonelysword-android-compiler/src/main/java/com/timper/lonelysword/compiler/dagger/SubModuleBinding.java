package com.timper.lonelysword.compiler.dagger;

import com.squareup.javapoet.ClassName;

/**
 * User: tangpeng.yang
 * Date: 04/07/2018
 * Description:
 * FIXME
 */
public class SubModuleBinding {

  private final String moduleName;
  private final String subModuleName;

  private final ClassName subClassName;

  public SubModuleBinding(String moduleName, String subModuleName, ClassName subClassName) {
    this.moduleName = moduleName;
    this.subClassName = subClassName;
    this.subModuleName = subModuleName;
  }

  public String getModuleName() {
    return moduleName;
  }

  public String getSubModuleName() {
    return subModuleName;
  }

  public ClassName getSubClassName() {
    return subClassName;
  }
}
