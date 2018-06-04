package com.timper.lonelysword.compiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import static com.google.auto.common.MoreElements.getPackage;
import static com.timper.lonelysword.compiler.NetworkBinding.BROADCASTRECEIVER;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;

/**
 * User: tangpeng.yang
 * Date: 17/05/2018
 * Description:
 * FIXME
 */
public class BindingSet {

  static final String VIEW_TYPE = "android.view.View";
  static final String ACTIVITY_TYPE = "android.app.Activity";
  static final String FRAGMENT_TYPE = "android.app.Fragment";
  static final String V4FRAGMENT_TYPE = "android.support.v4.app.Fragment";
  static final String DIALOG_TYPE = "android.app.Dialog";

  private static final String AFTERVIEWS = "afterViews";
  private static final String INITVIEWS = "initViews";
  private static final String BEFORVIEWS = "beforeViews";
  private static final String ONRESUME = "onResume";
  private static final String ONSTART = "onStart";
  private static final String ONPAUSE = "onPause";
  private static final String ONSTOP = "onStop";
  private static final String ONDESTROY = "onDestroy";
  private static final String UNBIND = "unbind";

  private static final ClassName UNBINDER = ClassName.get("com.timper.lonelysword", "Unbinder");
  private static final ClassName UI_THREAD = ClassName.get("android.support.annotation", "UiThread");
  private static final ClassName INJECT = ClassName.get("javax.inject", "Inject");
  private static final ClassName OVERRIIDE = ClassName.get("java.lang", "Override");
  private static final ClassName MODELADAPTERFACTOR = ClassName.get("com.timper.lonelysword.base", "ModelAdapterFactor");
  private static final ClassName VIEW = ClassName.get("android.view", "View");

  private final TypeName targetTypeName;
  private final ClassName bindingClassName;
  private final boolean isFinal;
  private final boolean isFragment;
  private final boolean isActivity;
  private final boolean isDialog;

  private final RootViewBinding rootViewBinding;
  private final DefaultMethodBinding beforViewsBinding;
  private final DefaultMethodBinding afterViewsBinding;
  private final NetworkBinding networkBinding;
  private final ModelAdapterBinding modelAdapterBinding;

  public BindingSet(TypeName targetTypeName, ClassName bindingClassName, boolean isFinal, boolean isFragment, boolean isActivity,
      boolean isDialog, RootViewBinding rootViewBinding, DefaultMethodBinding beforViewsBinding,
      DefaultMethodBinding afterViewsBinding, NetworkBinding networkBinding, ModelAdapterBinding modelAdapterBinding) {
    this.targetTypeName = targetTypeName;
    this.bindingClassName = bindingClassName;
    this.isFinal = isFinal;
    this.isFragment = isFragment;
    this.isActivity = isActivity;
    this.isDialog = isDialog;
    this.rootViewBinding = rootViewBinding;
    this.beforViewsBinding = beforViewsBinding;
    this.afterViewsBinding = afterViewsBinding;
    this.networkBinding = networkBinding;
    this.modelAdapterBinding = modelAdapterBinding;
  }

  JavaFile brewJava(int sdk, boolean debuggable) {
    return JavaFile.builder(bindingClassName.packageName(), createType(sdk, debuggable))
        .addFileComment("Generated code from lonely sword. Do not modify!")
        .build();
  }

  private TypeSpec createType(int sdk, boolean debuggable) {
    TypeSpec.Builder result = TypeSpec.classBuilder(bindingClassName.simpleName()).addModifiers(PUBLIC);
    if (isFinal) {
      result.addModifiers(FINAL);
    }
    result.addSuperinterface(UNBINDER);

    result.addField(targetTypeName, "target", PRIVATE);
    result.addField(VIEW, "container", PRIVATE);
    if (networkBinding != null) {
      result.addField(createNetworkField(sdk, debuggable));
    }

    if (modelAdapterBinding != null) {
      result.addField(createModelAdapterFactor(sdk, debuggable));
    }

    result.addMethod(createConstructorForActivity(sdk, debuggable));
    result.addMethod(createBeforeViewsMethod(sdk, debuggable));
    result.addMethod(createInitViewsMethod(sdk, debuggable));
    result.addMethod(createAfterViewsMethod(sdk, debuggable));
    result.addMethod(createOnResumeMethod(sdk, debuggable));
    result.addMethod(createOnStartMethod(sdk, debuggable));
    result.addMethod(createOnPauseMethod(sdk, debuggable));
    result.addMethod(createOnStopMethod(sdk, debuggable));
    result.addMethod(createOnDestroyMethod(sdk, debuggable));
    result.addMethod(createUnbindMethod(sdk, debuggable));
    return result.build();
  }

  private FieldSpec createNetworkField(int sdk, boolean debuggable) {
    FieldSpec.Builder builder = FieldSpec.builder(BROADCASTRECEIVER, "connectReceiver", Modifier.PRIVATE);
    builder.initializer(networkBinding.render());
    return builder.build();
  }

  private FieldSpec createModelAdapterFactor(int sdk, boolean debuggable) {
    ParameterizedTypeName parameterizedTypeName =
        ParameterizedTypeName.get(MODELADAPTERFACTOR, modelAdapterBinding.getClassName());
    FieldSpec.Builder builder = FieldSpec.builder(parameterizedTypeName, "factor", Modifier.PUBLIC);
    builder.addAnnotation(INJECT);
    return builder.build();
  }

  /**
   * create constructor method
   */
  private MethodSpec createConstructorForActivity(int sdk, boolean debuggable) {
    MethodSpec.Builder constructor = MethodSpec.constructorBuilder()
        .addAnnotation(UI_THREAD)
        .addModifiers(PUBLIC)
        .addParameter(targetTypeName, "target")
        .addParameter(VIEW, "container");

    constructor.addStatement("this.target = target");
    constructor.addStatement("this.container = container");
    constructor.addCode("\n");

    return constructor.build();
  }

  /**
   * create beforeViews method
   */
  private MethodSpec createBeforeViewsMethod(int sdk, boolean debuggable) {
    MethodSpec.Builder builder = MethodSpec.methodBuilder(BEFORVIEWS).addAnnotation(OVERRIIDE).addModifiers(PUBLIC);
    if (modelAdapterBinding != null) {
      builder.addStatement(modelAdapterBinding.render());
      builder.addCode("\n");
    }
    if (beforViewsBinding != null) {
      builder.addCode(beforViewsBinding.render());
      builder.addCode("\n");
    }
    return builder.build();
  }

  /**
   * create initViews method
   */
  private MethodSpec createInitViewsMethod(int sdk, boolean debuggable) {
    MethodSpec.Builder builder = MethodSpec.methodBuilder(INITVIEWS).addAnnotation(OVERRIIDE).addModifiers(PUBLIC);
    builder.returns(VIEW);
    if (rootViewBinding != null) {
      builder.addCode(rootViewBinding.render(sdk));
      builder.addCode("\n");
    }
    return builder.build();
  }

  /**
   * create afterViews method
   */
  private MethodSpec createAfterViewsMethod(int sdk, boolean debuggable) {
    MethodSpec.Builder builder = MethodSpec.methodBuilder(AFTERVIEWS).addAnnotation(OVERRIIDE).addModifiers(PUBLIC);
    if (afterViewsBinding != null) {
      builder.addCode(afterViewsBinding.render());
      builder.addCode("\n");
    }
    return builder.build();
  }

  /**
   * create onResume method
   */
  private MethodSpec createOnResumeMethod(int sdk, boolean debuggable) {
    MethodSpec.Builder builder = MethodSpec.methodBuilder(ONRESUME).addAnnotation(OVERRIIDE).addModifiers(PUBLIC);

    return builder.build();
  }

  /**
   * create onStart method
   */
  private MethodSpec createOnStartMethod(int sdk, boolean debuggable) {
    MethodSpec.Builder builder = MethodSpec.methodBuilder(ONSTART).addAnnotation(OVERRIIDE).addModifiers(PUBLIC);
    if (networkBinding != null) {
      builder.addCode(networkBinding.bindNetworkConnection());
    }
    return builder.build();
  }

  /**
   * create onPause method
   */
  private MethodSpec createOnPauseMethod(int sdk, boolean debuggable) {
    MethodSpec.Builder builder = MethodSpec.methodBuilder(ONPAUSE).addAnnotation(OVERRIIDE).addModifiers(PUBLIC);

    return builder.build();
  }

  /**
   * create onStop method
   */
  private MethodSpec createOnStopMethod(int sdk, boolean debuggable) {
    MethodSpec.Builder builder = MethodSpec.methodBuilder(ONSTOP).addAnnotation(OVERRIIDE).addModifiers(PUBLIC);
    if (networkBinding != null) {
      builder.addCode(networkBinding.unBindNetworkConnection());
    }
    return builder.build();
  }

  /**
   * create onDestroy method
   */
  private MethodSpec createOnDestroyMethod(int sdk, boolean debuggable) {
    MethodSpec.Builder builder = MethodSpec.methodBuilder(ONDESTROY).addAnnotation(OVERRIIDE).addModifiers(PUBLIC);

    return builder.build();
  }

  /**
   * create onDestroy method
   */
  private MethodSpec createUnbindMethod(int sdk, boolean debuggable) {
    MethodSpec.Builder builder = MethodSpec.methodBuilder(UNBIND).addAnnotation(OVERRIIDE).addModifiers(PUBLIC);

    return builder.build();
  }

  static final class Builder {
    private final TypeName targetTypeName;
    private final ClassName bindingClassName;
    private final boolean isFinal;
    private final boolean isFragment;
    private final boolean isActivity;
    private final boolean isDialog;

    private RootViewBinding rootViewBinding;
    private DefaultMethodBinding beforViewsBinding;
    private DefaultMethodBinding afterViewsBinding;
    private NetworkBinding.Builder networkBuilder;
    private ModelAdapterBinding modelAdapterBinding;

    private Builder(TypeName targetTypeName, ClassName bindingClassName, boolean isFinal, boolean isFragment, boolean isActivity,
        boolean isDialog) {
      this.targetTypeName = targetTypeName;
      this.bindingClassName = bindingClassName;
      this.isFinal = isFinal;
      this.isFragment = isFragment;
      this.isActivity = isActivity;
      this.isDialog = isDialog;
    }

    public boolean isFragment() {
      return isFragment;
    }

    public boolean isActivity() {
      return isActivity;
    }

    void addRootView(RootViewBinding rootViewBinding) {
      this.rootViewBinding = rootViewBinding;
    }

    void addBeforViews(DefaultMethodBinding beforViewsBinding) {
      this.beforViewsBinding = beforViewsBinding;
    }

    void addAfterViews(DefaultMethodBinding afterViewsBinding) {
      this.afterViewsBinding = afterViewsBinding;
    }

    void addDisableNetwork(DefaultMethodBinding disableNetwork) {
      if (networkBuilder == null) {
        networkBuilder = new NetworkBinding.Builder(isActivity, isFragment);
      }
      this.networkBuilder.addDisableNetwor(disableNetwork);
    }

    void addEnableNetwork(DefaultMethodBinding enableNetwork) {
      if (networkBuilder == null) {
        networkBuilder = new NetworkBinding.Builder(isActivity, isFragment);
      }
      this.networkBuilder.addenableNetwor(enableNetwork);
    }

    void addModelAdapter(ModelAdapterBinding modelAdapterBinding) {
      this.modelAdapterBinding = modelAdapterBinding;
    }

    BindingSet build() {
      return new BindingSet(targetTypeName, bindingClassName, isFinal, isFragment, isActivity, isDialog, rootViewBinding,
          beforViewsBinding, afterViewsBinding, networkBuilder.build(), modelAdapterBinding);
    }
  }

  static Builder newBuilder(TypeElement enclosingElement) {
    TypeMirror typeMirror = enclosingElement.asType();

    boolean isFragment = Utils.isSubtypeOfType(typeMirror, FRAGMENT_TYPE) || Utils.isSubtypeOfType(typeMirror, V4FRAGMENT_TYPE);
    boolean isActivity = Utils.isSubtypeOfType(typeMirror, ACTIVITY_TYPE);
    boolean isDialog = Utils.isSubtypeOfType(typeMirror, DIALOG_TYPE);

    TypeName targetType = TypeName.get(typeMirror);
    if (targetType instanceof ParameterizedTypeName) {
      targetType = ((ParameterizedTypeName) targetType).rawType;
    }

    String packageName = getPackage(enclosingElement).getQualifiedName().toString();
    String className = enclosingElement.getQualifiedName().toString().substring(packageName.length() + 1).replace('.', '$');
    ClassName bindingClassName = ClassName.get(packageName, "LonelySword_" + className);

    boolean isFinal = enclosingElement.getModifiers().contains(Modifier.FINAL);
    return new Builder(targetType, bindingClassName, isFinal, isFragment, isActivity, isDialog);
  }
}
