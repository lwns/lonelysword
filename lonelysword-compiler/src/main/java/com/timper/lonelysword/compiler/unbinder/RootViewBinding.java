package com.timper.lonelysword.compiler.unbinder;

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
  // viewModel className
  private final ClassName className;

  RootViewBinding(Id id, ClassName className, boolean isActivity, boolean isFragment) {
    this.id = id;
    this.className = className;
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
      if (id.rSymbol != null) {
        ClassName br = ClassName.get(id.rSymbol.packge().getQualifiedName().toString(), "BR");
        return CodeBlock.of("target.binding = $T.setContentView(target, $L);\n"
            + "target.binding.setVariable($T.viewModel,target.viewModel);\n"
            //+ "target.binding.setVariable($T.view,target);\n"
            + "target.viewModel.setBinding(target.binding);\n"
            + "target.viewModel.setFragmentManager(target.getSupportFragmentManager());\n", DATABINDINGUTIL, id.code, br);
      } else {
        return CodeBlock.of("target.binding = $T.setContentView(target, $L);\n"
            + "target.binding.setViewModel(target.viewModel);\n"
            //+ "target.binding.setView(target);\n"
            + "target.viewModel.setBinding(target.binding);\n"
            + "target.viewModel.setFragmentManager(target.getSupportFragmentManager());\n", DATABINDINGUTIL, id.code);
      }
    } else if (isFragment) {
      if (id.rSymbol != null) {
        StringBuilder builder = new StringBuilder("if (target.view == null) {\n"
            + "      target.binding = $T.inflate(target.getLayoutInflater(), $L,($T)container,false);\n"
            + "      target.binding.setVariable($T.viewModel,target.viewModel);\n"
            //+ "      target.binding.setVariable($T.view,target);\n"
            + "      target.viewModel.setBinding(target.binding);\n"
            + "      target.viewModel.setFragmentManager(target.getChildFragmentManager());\n"
            + "      target.view = target.binding.getRoot();\n"
            + "    }\n");

        ClassName br = ClassName.get(id.rSymbol.packge().getQualifiedName().toString(), "BR");
        return CodeBlock.of(builder.toString(), DATABINDINGUTIL, id.code, VIEWGROUP, br);
      } else {
        StringBuilder builder = new StringBuilder("if (target.view == null) {\n"
            + "      target.binding = $T.inflate(target.getLayoutInflater(), $L,($T)container,false);\n"
            + "      target.binding.setViewModel(target.viewModel);\n"
            //+ "      target.binding.setView(target);\n"
            + "      target.viewModel.setBinding(target.binding);\n"
            + "      target.viewModel.setFragmentManager(target.getChildFragmentManager());\n"
            + "      target.view = target.binding.getRoot();\n"
            + "    }\n");

        return CodeBlock.of(builder.toString(), DATABINDINGUTIL, id.code, VIEWGROUP);
      }
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
