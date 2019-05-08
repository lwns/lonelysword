package com.timper.lonelysword.compiler.usecase;

import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.processing.Filer;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import static com.google.auto.common.MoreElements.getPackage;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PROTECTED;
import static javax.lang.model.element.Modifier.PUBLIC;

/**
 * User: tangpeng.yang
 * Date: 29/05/2018
 * Description:
 * FIXME
 */
public class UseCaseSet {

    private final TypeName targetTypeName;
    private final ClassName bindingClassName;

    private static final ClassName FLOWABLEUSECASE = ClassName.get("com.timper.lonelysword.data", "FlowableUseCase");
    private static final ClassName COMPLETABLEUSECASE = ClassName.get("com.timper.lonelysword.data", "CompletableUseCase");
    private static final ClassName MAYBEUSECASE = ClassName.get("com.timper.lonelysword.data", "MaybeUseCase");
    private static final ClassName OBSERVABLEUSECASE = ClassName.get("com.timper.lonelysword.data", "ObservableUseCase");
    private static final ClassName SINGLEUSECASE = ClassName.get("com.timper.lonelysword.data", "SingleUseCase");

    private static final ClassName INJECT = ClassName.get("javax.inject", "Inject");
    private static final ClassName POSTEXECUTIONTHREAD =
            ClassName.get("com.timper.lonelysword.data.executor", "PostExecutionThread");
    private static final ClassName THREADEXECUTOR = ClassName.get("com.timper.lonelysword.data.executor", "ThreadExecutor");
    private static final ClassName OVERRIIDE = ClassName.get("java.lang", "Override");

    private static final ClassName FLOWABLE = ClassName.get("io.reactivex", "Flowable");
    private static final ClassName COMPLETABLE = ClassName.get("io.reactivex", "Completable");
    private static final ClassName MAYBE = ClassName.get("io.reactivex", "Maybe");
    private static final ClassName OBSERVABLE = ClassName.get("io.reactivex", "Observable");
    private static final ClassName SINGLE = ClassName.get("io.reactivex", "Single");

    private final ImmutableList<UseCaseBinding> useCaseBindings;

    public UseCaseSet(TypeName targetTypeName, ClassName bindingClassName, ImmutableList<UseCaseBinding> useCaseBindings) {
        this.targetTypeName = targetTypeName;
        this.bindingClassName = bindingClassName;
        this.useCaseBindings = useCaseBindings;
    }

    void brewJava(Filer filer, int sdk, boolean debuggable) throws IOException {
        for (UseCaseBinding binding : useCaseBindings) {
            JavaFile javaFile = JavaFile.builder(bindingClassName.packageName(), createType(binding, sdk, debuggable))
                    .addFileComment("Generated code from lonely sword. Do not modify!")
                    .build();
            javaFile.writeTo(filer);
        }
    }

    private TypeSpec createType(UseCaseBinding binding, int sdk, boolean debuggable) {
        String className = binding.getName().substring(0, 1).toUpperCase() + binding.getName().substring(1) + "UseCase";
        TypeSpec.Builder result = TypeSpec.classBuilder(className).addModifiers(PUBLIC);
        result.addModifiers(FINAL);

        ClassName returnClass = null;

        ParameterizedTypeName parameterizedTypeName = null;

        String reflectionName = null;

        TypeName returnRxClassName = binding.getReturnRxClass();

        if (returnRxClassName instanceof ParameterizedTypeName) {
            reflectionName = ((ParameterizedTypeName) returnRxClassName).rawType.reflectionName();
        } else if (returnRxClassName instanceof ClassName) {
            reflectionName = ((ClassName) returnRxClassName).reflectionName();
        }

        if (reflectionName.equals(FLOWABLE.reflectionName())) {
            returnClass = FLOWABLE;
            parameterizedTypeName = ParameterizedTypeName.get(FLOWABLEUSECASE,
                    binding.getReturnClass() != null ? binding.getReturnClass() : ClassName.OBJECT,
                    binding.getParameter() != null ? binding.getParameter() : ClassName.OBJECT);
        } else if (reflectionName.equals(COMPLETABLE.reflectionName())) {
            returnClass = COMPLETABLE;
            parameterizedTypeName = ParameterizedTypeName.get(COMPLETABLEUSECASE,
                    binding.getParameter() != null ? binding.getParameter() : ClassName.OBJECT);
        } else if (reflectionName.equals(MAYBE.reflectionName())) {
            returnClass = MAYBE;
            parameterizedTypeName = ParameterizedTypeName.get(MAYBEUSECASE,
                    binding.getReturnClass() != null ? binding.getReturnClass() : ClassName.OBJECT,
                    binding.getParameter() != null ? binding.getParameter() : ClassName.OBJECT);
        } else if (reflectionName.equals(OBSERVABLE.reflectionName())) {
            returnClass = OBSERVABLE;
            parameterizedTypeName = ParameterizedTypeName.get(OBSERVABLEUSECASE,
                    binding.getReturnClass() != null ? binding.getReturnClass() : ClassName.OBJECT,
                    binding.getParameter() != null ? binding.getParameter() : ClassName.OBJECT);
        } else if (reflectionName.equals(SINGLE.reflectionName())) {
            returnClass = SINGLE;
            parameterizedTypeName = ParameterizedTypeName.get(SINGLEUSECASE,
                    binding.getReturnClass() != null ? binding.getReturnClass() : ClassName.OBJECT,
                    binding.getParameter() != null ? binding.getParameter() : ClassName.OBJECT);
        }


        result.superclass(parameterizedTypeName);

        result.addField(targetTypeName, "repository", PRIVATE);

        MethodSpec.Builder constructor = MethodSpec.constructorBuilder()
                .addAnnotation(INJECT)
                .addModifiers(PUBLIC)
                .addParameter(targetTypeName, "repository")
                .addParameter(THREADEXECUTOR, "threadExecutor")
                .addParameter(POSTEXECUTIONTHREAD, "postExecutionThread");
        constructor.addStatement("super(threadExecutor, postExecutionThread)");
        constructor.addStatement("this.repository = repository");

        result.addMethod(constructor.build());

        MethodSpec.Builder builder =
                MethodSpec.methodBuilder("buildUseCaseObservable").addAnnotation(OVERRIIDE).addModifiers(PROTECTED);
        if (returnClass.reflectionName().equals(FLOWABLE.reflectionName())) {
            ParameterizedTypeName typeName =
                    ParameterizedTypeName.get(returnClass, binding.getReturnClass() != null ? binding.getReturnClass() : ClassName.OBJECT);
            builder.returns(typeName);
        } else {
            builder.returns(returnClass);
        }
        builder.addParameter(binding.getParameter() != null ? binding.getParameter() : ClassName.OBJECT, "request");
        if (binding.getParameter() == null) {
            builder.addStatement("return this.repository.$L()", binding.getName());
        } else {
            builder.addStatement("return this.repository.$L(request)", binding.getName());
        }
        result.addMethod(builder.build());

        return result.build();
    }

    static final class Builder {
        private final TypeName targetTypeName;
        private final ClassName bindingClassName;
        private final Map<String, UseCaseBinding.Builder> useCaseBindings = new LinkedHashMap<>();

        private Builder(TypeName targetTypeName, ClassName bindingClassName) {
            this.targetTypeName = targetTypeName;
            this.bindingClassName = bindingClassName;
        }

        public boolean addUseCaseBinding(UseCaseBinding.Builder builder) {
            if (useCaseBindings.get(builder.getName()) == null) {
                useCaseBindings.put(builder.getName(), builder);
                return true;
            } else {
                return false;
            }
        }

        UseCaseSet build() {
            ImmutableList.Builder<UseCaseBinding> useCaseBindingBuilder = ImmutableList.builder();
            for (UseCaseBinding.Builder builder : useCaseBindings.values()) {
                useCaseBindingBuilder.add(builder.build());
            }
            return new UseCaseSet(targetTypeName, bindingClassName, useCaseBindingBuilder.build());
        }
    }

    static Builder newBuilder(TypeElement enclosingElement) {
        TypeMirror typeMirror = enclosingElement.asType();

        TypeName targetType = TypeName.get(typeMirror);
        if (targetType instanceof ParameterizedTypeName) {
            targetType = ((ParameterizedTypeName) targetType).rawType;
        }

        String packageName = getPackage(enclosingElement).getQualifiedName().toString();
        String className = enclosingElement.getQualifiedName().toString().substring(packageName.length() + 1).replace('.', '$');
        ClassName bindingClassName = ClassName.get(packageName, className);

        return new Builder(targetType, bindingClassName);
    }
}
