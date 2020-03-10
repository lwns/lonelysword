package com.timper.lonelysword.compiler.usecase;

import com.squareup.javapoet.*;

import javax.annotation.processing.Filer;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import java.io.IOException;
import java.util.*;

import static com.google.auto.common.MoreElements.getPackage;
import static javax.lang.model.element.Modifier.*;

/**
 * User: tangpeng.yang
 * Date: 29/05/2018
 * Description: 生成UseCase实体类
 * FIXME
 */
public class UseCaseSet {

    private final TypeName targetTypeName;
    private final ClassName bindingClassName;

    private static final ClassName COMPLETABLEUSECASE = ClassName.get("com.timper.lonelysword.data", "CompletableUseCase");
    private static final ClassName USECASE = ClassName.get("com.timper.lonelysword.data", "UseCase");

    private static final ClassName INJECT = ClassName.get("javax.inject", "Inject");
    private static final ClassName POSTEXECUTIONTHREAD =
            ClassName.get("com.timper.lonelysword.data.executor", "PostExecutionThread");
    private static final ClassName THREADEXECUTOR = ClassName.get("com.timper.lonelysword.data.executor", "ThreadExecutor");
    private static final ClassName OVERRIIDE = ClassName.get("java.lang", "Override");

    private static final ClassName COMPLETABLE = ClassName.get("io.reactivex", "Completable");
    private static final ClassName SCHEDULERS = ClassName.get("io.reactivex.schedulers", "Schedulers");

    private final Map<String, UseCaseBinding> useCaseBindings;

    public UseCaseSet(TypeName targetTypeName, ClassName bindingClassName, Map<String, UseCaseBinding> useCaseBindings) {
        this.targetTypeName = targetTypeName;
        this.bindingClassName = bindingClassName;
        this.useCaseBindings = useCaseBindings;
    }

    void brewJava(Filer filer, int sdk, boolean debuggable) throws IOException {
        for (UseCaseBinding binding : useCaseBindings.values()) {
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

        ParameterizedTypeName parameterizedTypeName = null;

        String reflectionName = null;

        TypeName returnClass = binding.getReturnClass();

        if (returnClass instanceof ParameterizedTypeName) {
            reflectionName = ((ParameterizedTypeName) returnClass).rawType.reflectionName();
        } else if (returnClass instanceof ClassName) {
            reflectionName = ((ClassName) returnClass).reflectionName();
        }

        if (reflectionName.equals(COMPLETABLE.reflectionName())) {
            parameterizedTypeName = ParameterizedTypeName.get(COMPLETABLEUSECASE,
                    binding.getParameter() != null ? binding.getParameter() : ClassName.OBJECT);
        } else {
            parameterizedTypeName = ParameterizedTypeName.get(USECASE,
                    binding.getReturnClass() != null ? returnClass : ClassName.OBJECT,
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

        builder.returns(returnClass);
        builder.addParameter(binding.getParameter() != null ? binding.getParameter() : ClassName.OBJECT, "request");
        StringBuilder stringBuilder = new StringBuilder("return this.repository.$L");

        if (binding.getParameter() == null) {
            stringBuilder.append("()");
        } else {
            stringBuilder.append("(request)");
        }
        stringBuilder.append(".subscribeOn($T.from(threadExecutor)).observeOn(postExecutionThread.getScheduler())");
        if (binding.getTransformerType() != null && !reflectionName.equals(COMPLETABLE.reflectionName())) {
            stringBuilder.append(".compose(new $T())");
            builder.addStatement(CodeBlock.of(stringBuilder.toString(), binding.getName(), SCHEDULERS, binding.getTransformerType()));
        } else {
            builder.addStatement(CodeBlock.of(stringBuilder.toString(), binding.getName(), SCHEDULERS));
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

        /**
         * 如果name 相同，UseCaseBinding.Builder直接覆盖
         *
         * @param builder
         */
        public void addUseCaseBinding(UseCaseBinding.Builder builder) {
            useCaseBindings.put(builder.getName(), builder);
        }

        UseCaseSet build() {
            Deque<Map.Entry<String, UseCaseBinding.Builder>> entries = new ArrayDeque<>(useCaseBindings.entrySet());
            Map<String, UseCaseBinding> bindingMap = new HashMap();
            while (!entries.isEmpty()) {
                Map.Entry<String, UseCaseBinding.Builder> entry = entries.removeFirst();
                String type = entry.getKey();
                UseCaseBinding.Builder builder = entry.getValue();
                bindingMap.put(type, builder.build());
            }
            return new UseCaseSet(targetTypeName, bindingClassName, bindingMap);
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
