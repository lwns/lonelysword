package com.timper.lonelysword.compiler;

import com.squareup.javapoet.CodeBlock;
import java.util.List;

/**
 * User: tangpeng.yang
 * Date: 21/05/2018
 * Description:
 * FIXME
 */
public class DefaultMethodBinding extends MethodBinding {

  DefaultMethodBinding(String name, List<Parameter> parameters, boolean required) {
    super(name, parameters, required);
  }

  @Override public CodeBlock render() {
    return CodeBlock.of("target.$L();", getName());
  }
}
