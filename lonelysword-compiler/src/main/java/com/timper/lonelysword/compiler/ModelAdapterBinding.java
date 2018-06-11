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
public class ModelAdapterBinding implements CodeBinding {
  private static final ClassName VIEWMODELPROVIDERS = ClassName.get("android.arch.lifecycle", "ViewModelProviders");

  private final String factorName;
  private final String fieldName;
  private final ClassName className;

  public ModelAdapterBinding(ClassName className, String fieldName, String factorName) {
    this.className = className;
    this.fieldName = fieldName;
    if (!Utils.isEmpty(factorName)) {
      this.factorName = factorName;
    } else {
      this.factorName = "factor";
    }
  }

  @Override public CodeBlock render() {
    return CodeBlock.of("target.$L = $T.of(target,target.$L).get($T.class);", fieldName, VIEWMODELPROVIDERS, factorName, className);
  }

  static final class Builder {
    private final String fieldName;
    private final ClassName className;
    private String factorName;

    public Builder(ClassName className, String fieldName) {
      this.className = className;
      this.fieldName = fieldName;
    }

    public void addFactorName(String factorName) {
      this.factorName = factorName;
    }

    public String getFieldName() {
      return fieldName;
    }

    ModelAdapterBinding build() {
      return new ModelAdapterBinding(className, fieldName, factorName);
    }
  }
}