package com.timper.lonelysword.compiler.unbinder;

import com.squareup.javapoet.CodeBlock;

interface ResourceBinding {
  Id id();

  /** True if the code for this binding requires a 'res' variable for {@code Resources} access.
   * @param sdk sdk versions
   * @return true
   * */
  boolean requiresResources(int sdk);

  CodeBlock render(int sdk);
}
