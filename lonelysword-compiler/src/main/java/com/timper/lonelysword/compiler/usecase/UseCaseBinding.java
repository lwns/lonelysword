package com.timper.lonelysword.compiler.usecase;

import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.timper.lonelysword.compiler.unbinder.TypeBinding;

/**
 * User: tangpeng.yang
 * Date: 29/05/2018
 * Description:
 * FIXME
 */
public class UseCaseBinding implements TypeBinding {

  private final String name;
  private final TypeName returnClass;
  private final TypeName parameter;

  public UseCaseBinding(TypeName returnClass, String name, TypeName parameter) {
    this.returnClass = returnClass;
    this.name = name;
    this.parameter = parameter;
  }

  @Override public TypeSpec render() {
    return null;
  }

  public String getName() {
    return name;
  }

  public TypeName getParameter() {
    return parameter;
  }

  public TypeName getReturnClass() {
    return returnClass;
  }

  static final class Builder {
    private final String name;
    private TypeName returnClass;
    private TypeName parameter;

    Builder(String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }

    void addReturnClass(TypeName returnClass) {
      this.returnClass = returnClass;
    }

    void addParameter(TypeName parameter) {
      this.parameter = parameter;
    }

    UseCaseBinding build() {
      return new UseCaseBinding(returnClass, name, parameter);
    }
  }
}