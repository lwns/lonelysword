package com.timper.lonelysword.compiler.unbinder;

import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.timper.lonelysword.compiler.Utils;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import static com.google.auto.common.MoreElements.getPackage;
import static com.timper.lonelysword.compiler.unbinder.NetworkBinding.BROADCASTRECEIVER;
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
    static final String APPACTIVITY_TYPE = "com.timper.lonelysword.base.AppActivity<?,?>";
    static final String APPFRAGMENT_TYPE = "com.timper.lonelysword.base.AppFragment<?,?>";
    static final String V4FRAGMENT_TYPE = "androidx.fragment.app.Fragment";
    static final String DIALOG_TYPE = "android.app.Dialog";

    private static final String AFTERVIEWS = "afterViews";
    private static final String INITVIEWS = "initViews";
    private static final String ONCREATE = "onCreate";
    private static final String ONRESUME = "onResume";
    private static final String ONSTART = "onStart";
    private static final String ONPAUSE = "onPause";
    private static final String ONSTOP = "onStop";
    private static final String ONDESTROY = "onDestroy";
    private static final String ONSTATECHANGED = "onStateChanged";
    private static final String UNBIND = "unbind";

    private static final ClassName UNBINDER = ClassName.get("com.timper.lonelysword", "Unbinder");

    //lifecycle for fragment
    private static final ClassName GENERICLIFECYCLEOBSERVER = ClassName.get("androidx.lifecycle", "GenericLifecycleObserver");
    //lifecycle for activity
    private static final ClassName DEFAULTLIFECYCLEOBSERVER = ClassName.get("androidx.lifecycle", "DefaultLifecycleObserver");
    private static final ClassName LIFECYCLEOWNER = ClassName.get("androidx.lifecycle", "LifecycleOwner");
    private static final ClassName LIFECYCLEEVENT = ClassName.get("androidx.lifecycle.Lifecycle", "Event");

    private static final ClassName UI_THREAD = ClassName.get("androidx.annotation", "UiThread");
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
    private final ImmutableList<ViewModelBinding> viewModelBindings;
    private final ImmutableList<ViewModelFactorBinding> modelAdapterBindings;

    private BindingSet parentBinding;

    public BindingSet(TypeName targetTypeName, ClassName bindingClassName, boolean isFinal, boolean isFragment, boolean isActivity,
                      boolean isDialog, RootViewBinding rootViewBinding, DefaultMethodBinding beforViewsBinding,
                      DefaultMethodBinding afterViewsBinding, NetworkBinding networkBinding, ImmutableList<ViewModelBinding> viewModelBindings,
                      ImmutableList<ViewModelFactorBinding> modelAdapterBindings, BindingSet parentBinding) {
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
        this.viewModelBindings = viewModelBindings;
        this.modelAdapterBindings = modelAdapterBindings;
        this.parentBinding = parentBinding;
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

        if (parentBinding != null) {
            result.superclass(parentBinding.bindingClassName);
        } else {
            result.addSuperinterface(UNBINDER);
            if (isActivity) {
                result.addSuperinterface(DEFAULTLIFECYCLEOBSERVER);
            } else if (isFragment) {
                result.addSuperinterface(GENERICLIFECYCLEOBSERVER);
            }
        }

        result.addField(targetTypeName, "target", PRIVATE);
//    result.addField(VIEW, "container", PRIVATE);

        if (parentBinding != null) {
            if (parentBinding.networkBinding == null && networkBinding != null) {
                result.addField(createNetworkField(sdk, debuggable));
            }
        } else if (networkBinding != null) {
            result.addField(createNetworkField(sdk, debuggable));
        }

        result.addMethod(createConstructor(sdk, debuggable));
        result.addMethod(generateInitViewsMethod(sdk, debuggable));
        result.addMethod(createAfterViewsMethod(sdk, debuggable));
        if (isActivity) {
            result.addMethod(generateOnCreateMethod(sdk, debuggable));
            result.addMethod(generateOnResumeMethod(sdk, debuggable));
            result.addMethod(generateOnStartMethod(sdk, debuggable));
            result.addMethod(generateOnPauseMethod(sdk, debuggable));
            result.addMethod(generateOnStopMethod(sdk, debuggable));
            result.addMethod(generateOnDestroyMethod(sdk, debuggable));
        } else if (isFragment) {
            result.addMethod(generateOnStateChangedMethod(sdk, debuggable));
        }
        result.addMethod(generateUnbindMethod(sdk, debuggable));
        return result.build();
    }

    private FieldSpec createNetworkField(int sdk, boolean debuggable) {
        FieldSpec.Builder builder = FieldSpec.builder(BROADCASTRECEIVER, "connectReceiver", Modifier.PRIVATE);
        builder.initializer(networkBinding.render());
        return builder.build();
    }

    /**
     * create constructor method
     */
    private MethodSpec createConstructor(int sdk, boolean debuggable) {
        MethodSpec.Builder constructor = MethodSpec.constructorBuilder()
                .addAnnotation(UI_THREAD)
                .addModifiers(PUBLIC)
                .addParameter(targetTypeName, "target");

        if (parentBinding != null) {
            constructor.addStatement("super(target)");
            constructor.addStatement("this.target = target");
        } else {
            constructor.addStatement("this.target = target");
            constructor.addStatement("target.getLifecycle().addObserver(this);");
        }
        return constructor.build();
    }


    /**
     * create initViews method
     */
    private MethodSpec generateInitViewsMethod(int sdk, boolean debuggable) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder(INITVIEWS).addAnnotation(OVERRIIDE).addModifiers(PUBLIC);
        builder.addParameter(VIEW, "container");
        builder.returns(VIEW);
        if (rootViewBinding != null) {
            if (parentBinding != null) {
                builder.addStatement("super.initViews(container)");
                builder.addCode("\n");
            }
            builder.addCode(rootViewBinding.render(sdk));
            builder.addCode("\n");
            builder.addCode(rootViewBinding.returnRender());
            builder.addCode("\n");
        } else {
            if (parentBinding != null) {
                builder.addStatement("return super.initViews(container)");
                builder.addCode("\n");
            } else {
                builder.addStatement(CodeBlock.of("return null"));
                builder.addCode("\n");
            }
        }
        return builder.build();
    }

    /**
     * create afterViews method
     */
    private MethodSpec createAfterViewsMethod(int sdk, boolean debuggable) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder(AFTERVIEWS).addAnnotation(OVERRIIDE).addModifiers(PUBLIC);

        // generate beforViews
        for (ViewModelFactorBinding binding : modelAdapterBindings) {
            builder.addCode(binding.render());
            builder.addCode("\n");
        }

        if (beforViewsBinding != null) {
            builder.addCode(beforViewsBinding.render());
            builder.addCode("\n");
        }

        // generate initViews
        builder.addStatement("initViews(null);");
        builder.addCode("\n");

        if (parentBinding != null) {
            builder.addStatement("super.afterViews()");

            if (afterViewsBinding != null) {
                builder.addCode(afterViewsBinding.render());
                builder.addCode("\n");
            }

            builder.addCode("\n");
        } else {
            if (afterViewsBinding != null) {
                builder.addCode(afterViewsBinding.render());
                builder.addCode("\n");
            }

            builder.addStatement("target.viewModel.afterViews()");
            builder.addCode("\n");
        }
        for (ViewModelBinding binding : viewModelBindings) {
            builder.addCode(binding.render());
            builder.addCode("\n");
        }
        return builder.build();
    }

    /**
     * create onCreateMethod
     */
    private MethodSpec generateOnCreateMethod(int sdk, boolean debuggable) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder(ONCREATE).addAnnotation(OVERRIIDE).addModifiers(PUBLIC);
        builder.addParameter(LIFECYCLEOWNER, "owner");
        if (parentBinding != null) {
            builder.addStatement("super.onCreate(owner);");
            builder.addCode("\n");
        }
        return builder.build();
    }


    /**
     * create onResume method
     */
    private MethodSpec generateOnResumeMethod(int sdk, boolean debuggable) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder(ONRESUME).addAnnotation(OVERRIIDE).addModifiers(PUBLIC);
        builder.addParameter(LIFECYCLEOWNER, "owner");
        if (parentBinding != null) {
            builder.addStatement("super.onResume(owner)");
            builder.addCode("\n");
        }

        builder.addStatement("target.viewModel.onResume()");
        builder.addCode("\n");
        return builder.build();
    }

    /**
     * create onStart method
     */
    private MethodSpec generateOnStartMethod(int sdk, boolean debuggable) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder(ONSTART).addAnnotation(OVERRIIDE).addModifiers(PUBLIC);
        builder.addParameter(LIFECYCLEOWNER, "owner");
        if (parentBinding != null) {
            builder.addStatement("super.onStart(owner)");
            builder.addCode("\n");
            if (parentBinding.networkBinding == null && networkBinding != null) {
                builder.addCode(networkBinding.bindNetworkConnection());
                builder.addCode("\n");
            }
        } else if (networkBinding != null) {
            builder.addCode(networkBinding.bindNetworkConnection());
            builder.addCode("\n");
        }

        builder.addStatement("target.viewModel.onStart()");
        return builder.build();
    }

    /**
     * create onPause method
     */
    private MethodSpec generateOnPauseMethod(int sdk, boolean debuggable) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder(ONPAUSE).addAnnotation(OVERRIIDE).addModifiers(PUBLIC);
        builder.addParameter(LIFECYCLEOWNER, "owner");
        if (parentBinding != null) {
            builder.addStatement("super.onPause(owner)");
            builder.addCode("\n");
        }

        builder.addStatement("target.viewModel.onPause()");
        builder.addCode("\n");
        return builder.build();
    }

    /**
     * create onStop method
     */
    private MethodSpec generateOnStopMethod(int sdk, boolean debuggable) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder(ONSTOP).addAnnotation(OVERRIIDE).addModifiers(PUBLIC);
        builder.addParameter(LIFECYCLEOWNER, "owner");
        if (parentBinding != null) {
            builder.addStatement("super.onStop(owner)");
            builder.addCode("\n");
            if (parentBinding.networkBinding == null && networkBinding != null) {
                builder.addCode(networkBinding.unBindNetworkConnection());
                builder.addCode("\n");
            }
        } else {
            if (networkBinding != null) {
                builder.addCode(networkBinding.unBindNetworkConnection());
                builder.addCode("\n");
            }
        }

        builder.addStatement("target.viewModel.onStop()");
        return builder.build();
    }

    /**
     * create onDestroy method
     */
    private MethodSpec generateOnDestroyMethod(int sdk, boolean debuggable) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder(ONDESTROY).addAnnotation(OVERRIIDE).addModifiers(PUBLIC);
        builder.addParameter(LIFECYCLEOWNER, "owner");
        if (parentBinding != null) {
            builder.addStatement("super.onDestroy(owner)");
            builder.addCode("\n");
        }

        builder.addStatement("target.viewModel.onDestroy()");
        return builder.build();
    }

    /**
     * create onDestroy method
     */
    private MethodSpec generateUnbindMethod(int sdk, boolean debuggable) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder(UNBIND).addAnnotation(OVERRIIDE).addModifiers(PUBLIC);
        if (parentBinding != null) {
            builder.addStatement("super.unbind()");
        }
        return builder.build();
    }

    /**
     * create initViews method
     */
    private MethodSpec generateOnStateChangedMethod(int sdk, boolean debuggable) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder(ONSTATECHANGED).addAnnotation(OVERRIIDE).addModifiers(PUBLIC);
        builder.addParameter(LIFECYCLEOWNER, "source");
        builder.addParameter(LIFECYCLEEVENT, "event");
        if (parentBinding != null) {
            builder.addStatement("super.onStateChanged(source,event)");
            builder.addCode("\n");
        } else {
            CodeBlock.Builder codeBlock = CodeBlock.builder()
                    .add("switch (event) {\n" +
                            "      case ON_CREATE:\n");
            // generate beforViews
            for (ViewModelFactorBinding binding : modelAdapterBindings) {
                codeBlock.add(binding.render());
                codeBlock.add("\n");
            }
//            if (beforViewsBinding != null) {
//                codeBlock.add(beforViewsBinding.render());
//                codeBlock.add("\n");
//            }

            codeBlock.addStatement(
                    "        break;\n" +
                            "      case ON_START:\n" +
                            "        target.viewModel.onStart();\n" +
                            "        break;\n" +
                            "      case ON_RESUME:\n" +
                            "        target.viewModel.onResume();\n" +
                            "        break;\n" +
                            "      case ON_PAUSE:\n" +
                            "        target.viewModel.onPause();\n" +
                            "        break;\n" +
                            "      case ON_STOP:\n" +
                            "        target.viewModel.onStop();\n" +
                            "        break;\n" +
                            "      case ON_DESTROY:\n" +
                            "        target.viewModel.onDestroy();\n" +
                            "        break;\n" +
                            "      case ON_ANY:\n" +
                            "        break;\n" +
                            "      default:\n" +
                            "        break;\n" +
                            "    }");

            builder.addCode(codeBlock.build());
        }
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
        private final Map<String, ViewModelBinding.Builder> viewModelBindings = new LinkedHashMap<>();
        private final Map<String, ViewModelFactorBinding.Builder> modelAdaterBindings = new LinkedHashMap<>();

        private BindingSet parentBinding;

        private Builder(TypeName targetTypeName, ClassName bindingClassName, boolean isFinal, boolean isFragment, boolean isActivity,
                        boolean isDialog) {
            this.targetTypeName = targetTypeName;
            this.bindingClassName = bindingClassName;
            this.isFinal = isFinal;
            this.isFragment = isFragment;
            this.isActivity = isActivity;
            this.isDialog = isDialog;
            this.networkBuilder = new NetworkBinding.Builder(isActivity, isFragment);
        }

        void setParent(BindingSet parent) {
            this.parentBinding = parent;
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
            this.networkBuilder.addDisableNetwor(disableNetwork);
        }

        void addEnableNetwork(DefaultMethodBinding enableNetwork) {
            this.networkBuilder.addenableNetwor(enableNetwork);
        }

        public boolean addViewModel(ViewModelBinding.Builder builder) {
            if (viewModelBindings.get(builder.getFieldName()) == null) {
                viewModelBindings.put(builder.getFieldName(), builder);
                return true;
            } else {
                return false;
            }
        }

        public boolean addModelAdater(ViewModelFactorBinding.Builder builder) {
            if (modelAdaterBindings.get(builder.getFieldName()) == null) {
                modelAdaterBindings.put(builder.getFieldName(), builder);
                return true;
            } else {
                return false;
            }
        }

        BindingSet build() {
            ImmutableList.Builder<ViewModelBinding> viewModelBindingBuilder = ImmutableList.builder();
            for (ViewModelBinding.Builder builder : viewModelBindings.values()) {
                viewModelBindingBuilder.add(builder.build());
            }
            ImmutableList.Builder<ViewModelFactorBinding> modelAdapterBindingBuilder = ImmutableList.builder();
            for (ViewModelFactorBinding.Builder builder : modelAdaterBindings.values()) {
                modelAdapterBindingBuilder.add(builder.build());
            }
            return new BindingSet(targetTypeName, bindingClassName, isFinal, isFragment, isActivity, isDialog, rootViewBinding,
                    beforViewsBinding, afterViewsBinding, networkBuilder.build(), viewModelBindingBuilder.build(),
                    modelAdapterBindingBuilder.build(), parentBinding);
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
