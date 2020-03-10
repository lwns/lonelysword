package com.timper.lonelysword.compiler.unbinder;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.timper.lonelysword.compiler.Utils;

/**
 * User: tangpeng.yang
 * Date: 28/05/2018
 * Description:
 * FIXME
 */
public class ViewModelFactorBinding implements CodeBinding {
  private static final ClassName VIEWMODELPROVIDERS = ClassName.get("androidx.lifecycle", "ViewModelProviders");

  private final String factorName;
  private final String fieldName;
  private final ClassName className;
  private final boolean isActivity;
  private final boolean isFragment;

  public ViewModelFactorBinding(ClassName className, String fieldName, String factorName, boolean isActivity, boolean isFragment) {
    this.className = className;
    this.fieldName = fieldName;
    this.isActivity = isActivity;
    this.isFragment = isFragment;
    if (!Utils.isEmpty(factorName)) {
      this.factorName = factorName;
    } else {
      this.factorName = "factor";
    }
  }

  @Override public CodeBlock render() {
    if (isActivity) {
      return CodeBlock.of("target.$L = $T.of(target,target.$L).get($T.class);", fieldName, VIEWMODELPROVIDERS, factorName,
          className);
    } else if (isFragment) {
      return CodeBlock.of("target.$L = $T.of(target.getActivity(),target.$L).get($T.class);", fieldName, VIEWMODELPROVIDERS,
          factorName, className);
    }
    return null;
  }

  static final class Builder {
    private final String fieldName;
    private final ClassName className;
    private String factorName;
    private final boolean isActivity;
    private final boolean isFragment;

    public Builder(ClassName className, String fieldName, boolean isActivity, boolean isFragment) {
      this.className = className;
      this.fieldName = fieldName;
      this.isActivity = isActivity;
      this.isFragment = isFragment;
    }

    public void addFactorName(String factorName) {
      this.factorName = factorName;
    }

    public String getFieldName() {
      return fieldName;
    }

    ViewModelFactorBinding build() {
      return new ViewModelFactorBinding(className, fieldName, factorName, isActivity, isFragment);
    }
  }
}