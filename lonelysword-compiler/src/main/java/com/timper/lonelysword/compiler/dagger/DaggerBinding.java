package com.timper.lonelysword.compiler.dagger;

import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.ClassName;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * User: tangpeng.yang
 * Date: 04/07/2018
 * Description:
 * FIXME
 */
public class DaggerBinding {

  private final String moduleName;
  private final String simpleName;

  private final ImmutableList<ClassName> subModules;
  private final ImmutableList<ClassName> otherModules;

  public DaggerBinding(String moduleName, String simpleName, ImmutableList<ClassName> subModules,
      ImmutableList<ClassName> otherModules) {
    this.moduleName = moduleName;
    this.subModules = subModules;
    this.simpleName = simpleName;
    this.otherModules = otherModules;
  }

  public String getModuleName() {
    return moduleName;
  }

  public String getSimpleName() {
    return simpleName;
  }

  public ImmutableList<ClassName> getSubModules() {
    return subModules;
  }

  public ImmutableList<ClassName> getOtherModules() {
    return otherModules;
  }

  static final class Builder {

    private final String moduleName;
    private final String simpleName;

    private final Map<String, ClassName> moduleMap = new LinkedHashMap<>();
    private final Map<String, ClassName> otherModuleMap = new LinkedHashMap<>();

    public Builder(String moduleName, String simpleName) {
      this.moduleName = moduleName;
      this.simpleName = simpleName;
    }

    public String getModuleName() {
      return moduleName;
    }

    public boolean addModule(ClassName className) {
      if (moduleMap.get(className.reflectionName()) == null) {
        moduleMap.put(className.reflectionName(), className);
        return true;
      } else {
        return false;
      }
    }

    public boolean addOtherModule(ClassName className) {
      if (otherModuleMap.get(className.reflectionName()) == null) {
        otherModuleMap.put(className.reflectionName(), className);
        return true;
      } else {
        return false;
      }
    }

    DaggerBinding build() {
      ImmutableList.Builder<ClassName> modules = ImmutableList.builder();
      for (ClassName className : moduleMap.values()) {
        modules.add(className);
      }

      ImmutableList.Builder<ClassName> otherModules = ImmutableList.builder();
      for (ClassName className : otherModuleMap.values()) {
        otherModules.add(className);
      }
      return new DaggerBinding(moduleName, simpleName, modules.build(), otherModules.build());
    }
  }
}
