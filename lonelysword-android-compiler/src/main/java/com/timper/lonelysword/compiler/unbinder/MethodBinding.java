package com.timper.lonelysword.compiler.unbinder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User: tangpeng.yang
 * Date: 21/05/2018
 * Description:
 * FIXME
 */
public abstract class MethodBinding implements CodeBinding {

  private final String name;
  private final List<Parameter> parameters;
  private final boolean required;

  MethodBinding(String name, List<Parameter> parameters, boolean required) {
    this.name = name;
    this.parameters = Collections.unmodifiableList(new ArrayList<>(parameters));
    this.required = required;
  }

  public String getName() {
    return name;
  }

  public List<Parameter> getParameters() {
    return parameters;
  }

  public boolean isRequired() {
    return required;
  }
}
