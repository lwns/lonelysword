package com.timper.lonelysword.compiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;

/**
 * User: tangpeng.yang
 * Date: 10/05/2018
 * Description:
 * FIXME
 */
public class RootViewBinding implements ResourceBinding {
  private static final ClassName DATABINDINGUTIL = ClassName.get("android.databinding", "DataBindingUtil");
  private static final ClassName VIEWGROUP = ClassName.get("android.view", "ViewGroup");

  private final Id id;

  private final boolean isActivity;
  private final boolean isFragment;

  RootViewBinding(Id id, boolean isActivity, boolean isFragment) {
    this.id = id;
    this.isActivity = isActivity;
    this.isFragment = isFragment;
  }

  @Override public Id id() {
    return id;
  }

  @Override public boolean requiresResources(int sdk) {
    return false;
  }

  @Override public CodeBlock render(int sdk) {
    if (isActivity) {
      return CodeBlock.of("target.binding = $T.setContentView(target, $L);\n",
          DATABINDINGUTIL, id.code);
    } else if (isFragment) {
      StringBuilder builder = new StringBuilder("if (target.view == null) {\n"
          + "      target.binding = $T.inflate(target.getLayoutInflater(), $L,($T)container,false);\n"
          + "      target.view = target.binding.getRoot();\n"
          + "    }\n");
      return CodeBlock.of(builder.toString(), DATABINDINGUTIL, id.code, VIEWGROUP);
    } else {
      return CodeBlock.of("return null;");
    }
  }

  public CodeBlock returnRender() {
    if (isActivity) {
      return CodeBlock.of("return target.binding.getRoot();");
    } else if (isFragment) {
      return CodeBlock.of("return target.view;");
    } else {
      return CodeBlock.of("return null;");
    }
  }
}
