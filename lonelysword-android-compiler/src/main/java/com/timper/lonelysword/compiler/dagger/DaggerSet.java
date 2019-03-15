package com.timper.lonelysword.compiler.dagger;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.timper.lonelysword.compiler.Utils;
import java.io.IOException;
import javax.annotation.processing.Filer;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import static com.google.auto.common.MoreElements.getPackage;
import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.element.Modifier.PUBLIC;

/**
 * User: tangpeng.yang
 * Date: 29/05/2018
 * Description:
 * FIXME
 */
public class DaggerSet {

  final TypeName targetTypeName;
  final ClassName bindingClassName;
  final DaggerBinding daggerBinding;

  private final boolean isActivity;
  private final boolean isFragment;

  static final ClassName MODULE = ClassName.get("dagger", "Module");
  static final ClassName BINDS = ClassName.get("dagger", "Binds");
  static final ClassName ACTIVITYSCOPE = ClassName.get("com.timper.lonelysword", "ActivityScope");
  static final ClassName CONTRIBUTESANDROIDINJECTOR = ClassName.get("dagger.android", "ContributesAndroidInjector");
  //static final ClassName APPACTIVITY = ClassName.get("com.timper.lonelysword.base", "AppActivity");
  static final ClassName FRAGMENTACTIVITY = ClassName.get("android.support.v4.app", "FragmentActivity");
  static final ClassName FRAGMENT = ClassName.get("android.support.v4.app", "Fragment");

  public DaggerSet(TypeName targetTypeName, ClassName bindingClassName, DaggerBinding daggerBinding, boolean isActivity) {
    this.targetTypeName = targetTypeName;
    this.bindingClassName = bindingClassName;
    this.daggerBinding = daggerBinding;
    this.isActivity = isActivity;
    this.isFragment = !isActivity;
  }

  void brewJava(Filer filer, int sdk, boolean debuggable) throws IOException {
    JavaFile moduleFile = JavaFile.builder(bindingClassName.packageName(), createModuleType(daggerBinding, sdk, debuggable))
        .addFileComment("Generated code from lonely sword. Do not modify!")
        .build();
    moduleFile.writeTo(filer);

    JavaFile subModuleFile = JavaFile.builder(bindingClassName.packageName(), createSubModuleType(daggerBinding, sdk, debuggable))
        .addFileComment("Generated code from lonely sword. Do not modify!")
        .build();
    subModuleFile.writeTo(filer);
  }

  private TypeSpec createModuleType(DaggerBinding binding, int sdk, boolean debuggable) {
    String className = binding.getSimpleName() + "Module";
    TypeSpec.Builder result = TypeSpec.classBuilder(className).addModifiers(PUBLIC).addModifiers(ABSTRACT);
    result.addAnnotation(MODULE);

    MethodSpec.Builder builder =
        MethodSpec.methodBuilder("provide" + binding.getModuleName()).addModifiers(PUBLIC).addModifiers(ABSTRACT);
    builder.addAnnotation(BINDS);
    builder.addParameter(bindingClassName, "param");
    if(isActivity){
      builder.returns(FRAGMENTACTIVITY);
    }else if(isFragment){
      builder.returns(FRAGMENT);
    }

    result.addMethod(builder.build());

    return result.build();
  }

  private TypeSpec createSubModuleType(DaggerBinding binding, int sdk, boolean debuggable) {
    String className = binding.getSimpleName() + "SubModule";
    TypeSpec.Builder result = TypeSpec.classBuilder(className).addModifiers(PUBLIC).addModifiers(ABSTRACT);
    result.addAnnotation(MODULE);

    for (ClassName subClassName : binding.getSubModules()) {
      MethodSpec.Builder builder =
          MethodSpec.methodBuilder("bind" + subClassName.simpleName()).addModifiers(PUBLIC).addModifiers(ABSTRACT);
      builder.addAnnotation(CONTRIBUTESANDROIDINJECTOR);
      builder.returns(subClassName);
      result.addMethod(builder.build());
    }

    return result.build();
  }

  static final class Builder {
    private final TypeName targetTypeName;
    private final ClassName bindingClassName;
    private DaggerBinding.Builder builder;
    private final boolean isActivity;

    private Builder(TypeName targetTypeName, ClassName bindingClassName,boolean isActivity) {
      this.targetTypeName = targetTypeName;
      this.bindingClassName = bindingClassName;
      this.isActivity = isActivity;
    }

    public void addDaggerBinding(String moduleName, String simpleName) {
      if (builder == null) {
        builder = new DaggerBinding.Builder(moduleName, simpleName);
      }
    }

    public boolean addSubModuleBinding(String moduleName, ClassName className) {
      if (!Utils.isEmpty(moduleName)) {
        return builder.addModule(className);
      } else {
        return false;
      }
    }

    public boolean addOtherModuleBinding(String moduleName, ClassName className) {
      if (!Utils.isEmpty(moduleName)) {
        return builder.addOtherModule(className);
      } else {
        return false;
      }
    }

    DaggerSet build() {
      return new DaggerSet(targetTypeName, bindingClassName, builder.build(),isActivity);
    }
  }

  static Builder newBuilder(TypeElement enclosingElement,boolean isActivity) {
    TypeMirror typeMirror = enclosingElement.asType();

    TypeName targetType = TypeName.get(typeMirror);
    if (targetType instanceof ParameterizedTypeName) {
      targetType = ((ParameterizedTypeName) targetType).rawType;
    }

    String packageName = getPackage(enclosingElement).getQualifiedName().toString();
    String className = enclosingElement.getQualifiedName().toString().substring(packageName.length() + 1).replace('.', '$');
    ClassName bindingClassName = ClassName.get(packageName, className);

    return new Builder(targetType, bindingClassName, isActivity);
  }
}
