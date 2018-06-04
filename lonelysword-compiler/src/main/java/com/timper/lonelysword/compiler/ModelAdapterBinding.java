package com.timper.lonelysword.compiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import java.util.List;

/**
 * User: tangpeng.yang
 * Date: 28/05/2018
 * Description:
 * FIXME
 */
public class ModelAdapterBinding extends MethodBinding {
  private static final ClassName VIEWMODELPROVIDERS = ClassName.get("android.arch.lifecycle", "ViewModelProviders");
  private ClassName className;

  ModelAdapterBinding(ClassName className, String name, List<Parameter> parameters, boolean required) {
    super(name, parameters, required);
    this.className = className;
  }

  public ClassName getClassName() {
    return className;
  }

  @Override public CodeBlock render() {
    return CodeBlock.of("target.$L = $T.of(target,factor).get($T.class)", getName(), VIEWMODELPROVIDERS, className);
  }
}