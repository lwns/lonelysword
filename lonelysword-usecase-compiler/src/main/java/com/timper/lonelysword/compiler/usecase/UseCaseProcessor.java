package com.timper.lonelysword.compiler.usecase;

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableSet;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import com.timper.lonelysword.annotations.apt.UseCase;
import com.timper.lonelysword.compiler.Utils;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.util.*;

import static javax.lang.model.element.ElementKind.INTERFACE;
import static javax.lang.model.element.ElementKind.METHOD;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.STATIC;

/**
 * User: tangpeng.yang
 * Date: 17/05/2018
 * Description: usecase 注解解析器
 * FIXME
 */
@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class UseCaseProcessor
        extends AbstractProcessor {

    private static final String OPTION_SDK_INT = "lonelysword.minSdk";
    private static final String OPTION_DEBUGGABLE = "lonelysword.debuggable";

    private static final String USECASE_IGNORE = "ignore";
    private static final String USECASE_TRANSFORMER = "transformer";

    private static final ClassName FLOWABLE = ClassName.get("io.reactivex", "Flowable");
    private static final ClassName COMPLETABLE = ClassName.get("io.reactivex", "Completable");
    private static final ClassName MAYBE = ClassName.get("io.reactivex", "Maybe");
    private static final ClassName OBSERVABLE = ClassName.get("io.reactivex", "Observable");
    private static final ClassName SINGLE = ClassName.get("io.reactivex", "Single");

    private Types typeUtils;
    private Filer filer;
    private Elements elements;

    private int sdk = 1;
    private boolean debuggable = true;

    @Override
    public synchronized void init(ProcessingEnvironment env) {
        super.init(env);

        elements = processingEnv.getElementUtils();
        typeUtils = env.getTypeUtils();
        filer = env.getFiler();
    }

    @Override
    public Set<String> getSupportedOptions() {
        return ImmutableSet.of(OPTION_SDK_INT, OPTION_DEBUGGABLE);
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        for (Class<? extends Annotation> annotation : getSupportedAnnotations()) {
            types.add(annotation.getCanonicalName());
        }
        return types;
    }

    private Set<Class<? extends Annotation>> getSupportedAnnotations() {
        Set<Class<? extends Annotation>> annotations = new LinkedHashSet<>();
        annotations.add(UseCase.class);
        return annotations;
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment env) {
        Map<TypeElement, UseCaseSet> bindingMap = parseTargets(env);

        for (Map.Entry<TypeElement, UseCaseSet> entry : bindingMap.entrySet()) {
            TypeElement typeElement = entry.getKey();
            UseCaseSet binding = entry.getValue();
            try {
                binding.brewJava(filer, sdk, debuggable);
            } catch (IOException e) {
                error(typeElement, "生成错误类型 %s: %s", typeElement, e.getMessage());
            }
        }
        return true;
    }

    private Map<TypeElement, UseCaseSet> parseTargets(RoundEnvironment env) {
        Map<TypeElement, UseCaseSet.Builder> builderMap = new LinkedHashMap<>();
        Set<TypeElement> erasedTargetNames = new LinkedHashSet<>();

        // 解析Usecase注解
        for (Element element : env.getElementsAnnotatedWith(UseCase.class)) {
            try {
                parseUseCase(element, builderMap, erasedTargetNames);
            } catch (Exception e) {
                logParsingError(element, UseCase.class, e);
            }
        }

        Deque<Map.Entry<TypeElement, UseCaseSet.Builder>> entries = new ArrayDeque<>(builderMap.entrySet());
        Map<TypeElement, UseCaseSet> bindingMap = new LinkedHashMap<>();
        while (!entries.isEmpty()) {
            Map.Entry<TypeElement, UseCaseSet.Builder> entry = entries.removeFirst();

            TypeElement type = entry.getKey();
            UseCaseSet.Builder builder = entry.getValue();
            bindingMap.put(type, builder.build());
        }
        return bindingMap;
    }

    private void parseUseCase(Element element, Map<TypeElement, UseCaseSet.Builder> builderMap,
                              Set<TypeElement> erasedTargetNames) {
        if ((element instanceof ExecutableElement) && element.getKind() == METHOD) {
            ExecutableElement executableElement = (ExecutableElement) element;

            if (checkElement(executableElement)) {
                return;
            }

            List<AnnotationValue> ignoreClazz = getIgnoreClass(executableElement);

            TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
            parseExecutableElement(enclosingElement, executableElement, ignoreClazz, builderMap, erasedTargetNames);
        } else if ((element instanceof TypeElement) && element.getKind() == INTERFACE) {
            TypeElement enclosingElement = (TypeElement) element;

            List<AnnotationValue> ignoreClazz = getIgnoreClass(element);
            if (enclosingElement.getEnclosedElements() != null) {
                for (Element mothodElement : enclosingElement.getEnclosedElements()) {
                    if ((mothodElement instanceof ExecutableElement) && mothodElement.getKind() == METHOD) {
                        if (checkElement((ExecutableElement) mothodElement)) {
                            return;
                        }
                        parseExecutableElement(enclosingElement, (ExecutableElement) mothodElement, ignoreClazz, builderMap, erasedTargetNames);
                    }
                }
            }
        } else {
            throw new IllegalStateException(String.format("@%s 注解必须是定义在方法或者类上面.", UseCase.class.getSimpleName()));
        }
    }

    private List<AnnotationValue> getIgnoreClass(Element element) {
        List<AnnotationValue> ignoreClazz = new ArrayList<>();
        for (AnnotationMirror m : element.getAnnotationMirrors()) {
            if (m.getAnnotationType()
                    .toString()
                    .equals(UseCase.class.getCanonicalName())) {
                for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : m.getElementValues()
                        .entrySet()) {
                    if (USECASE_IGNORE.equals(entry.getKey()
                            .getSimpleName()
                            .toString())) {
                        ignoreClazz.add(entry.getValue());
                    } else if (USECASE_TRANSFORMER.equals(entry.getKey()
                            .getSimpleName()
                            .toString())) {
                        ignoreClazz.add(entry.getValue());
                    }
                }
            }
        }
        return ignoreClazz;
    }


    private boolean checkElement(ExecutableElement executableElement) {
        boolean hasError = isInaccessibleViaGeneratedCode(UseCase.class, executableElement);
        hasError |= isBindingInWrongPackage(UseCase.class, executableElement);

        return hasError;
    }


    private void parseExecutableElement(TypeElement enclosingElement, ExecutableElement executableElement, List<AnnotationValue> ignoreClazz, Map<TypeElement, UseCaseSet.Builder> builderMap,
                                        Set<TypeElement> erasedTargetNames) {

        boolean hasError = false;
        String name = executableElement.getSimpleName().toString();

        Type.ClassType ignoreType = null;
        Type.ClassType ignoreTransform = null;

        // 验证方法是否有参数
        List<? extends VariableElement> methodParameters = executableElement.getParameters();
        if (methodParameters != null && methodParameters.size() > 1) {
            error(executableElement, "@%s 方法只能有一个参数", UseCase.class);
            hasError = true;
        }
        // 验证返回值类型
        TypeMirror returnType = executableElement.getReturnType();
        TypeName returnClassName = null;
        if (!(returnType instanceof Type.ClassType)) {
            error(executableElement, "@%s 方法返回只能是类", returnType.toString());
            hasError = true;
        } else {
            Type.ClassType classType = ((Type.ClassType) returnType);

            if (ignoreClazz != null && ignoreClazz.size() == 2) {
                Object typeObject = ignoreClazz.get(0).getValue();
                if (typeObject instanceof Type.ClassType) {
                    ignoreType = (Type.ClassType) typeObject;
                } else {
                    error(executableElement, "@%s 注解ignore，必须是Class<?>类型.", UseCase.class.getSimpleName());
                    hasError = true;
                }
                Object transformerObject = ignoreClazz.get(1).getValue();
                if (transformerObject instanceof Type.ClassType) {
                    ignoreTransform = (Type.ClassType) transformerObject;
                } else {
                    error(executableElement, "@%s 注解transformer，必须是Class<?>类型.", UseCase.class.getSimpleName());
                    hasError = true;
                }
            }

            returnClassName = getTypeName(classType, ignoreType);

            String reflectionName = null;

            if (returnClassName instanceof ParameterizedTypeName) {
                reflectionName = ((ParameterizedTypeName) returnClassName).rawType.reflectionName();
            } else if (returnClassName instanceof ClassName) {
                reflectionName = ((ClassName) returnClassName).reflectionName();
            }

            if (Utils.isEmpty(reflectionName)) {
                error(executableElement, "@%s 返回类型必须是  '%s','%s','%s','%s','%s' 类型. (%s.%s)", UseCase.class, "Flowable", "Completable", "Maybe", "Observable", "Single",
                        enclosingElement.getQualifiedName(), executableElement.getSimpleName());
                hasError = true;
            }


            if (!reflectionName.equals(FLOWABLE.reflectionName())
                    && !reflectionName.equals(COMPLETABLE.reflectionName())
                    && !reflectionName.equals(MAYBE.reflectionName())
                    && !reflectionName.equals(OBSERVABLE.reflectionName())
                    && !reflectionName.equals(SINGLE.reflectionName())) {

                error(executableElement, "@%s 返回类型必须是  '%s','%s','%s','%s','%s' 类型. (%s.%s)", UseCase.class, "Flowable", "Completable", "Maybe", "Observable", "Single",
                        enclosingElement.getQualifiedName(), executableElement.getSimpleName());
                hasError = true;
            }

        }

        if (hasError) {
            return;
        }

        UseCaseBinding.Builder builder = new UseCaseBinding.Builder(name);
        UseCaseSet.Builder useCase = getOrCreateBindingBuilder(builderMap, enclosingElement);

        useCase.addUseCaseBinding(builder);//将UseCaseBinding.Builder，添加到UseCaseSet.Builder

        if (methodParameters != null && methodParameters.size() > 0) {
            TypeMirror typeMirror = methodParameters.get(0).asType();
            TypeName parameter = getTypeName((Type.ClassType) typeMirror, null);
            builder.addParameter(parameter);
        }
        builder.addReturnClass(returnClassName);
        if (ignoreType != null) {
            builder.addIgnoreType(getTypeName(ignoreType, null));
        }
        if (ignoreTransform != null) {
            builder.addTransformerType(getTypeName(ignoreTransform, null));
        }

        builderMap.put(enclosingElement, useCase);
        erasedTargetNames.add(enclosingElement);
    }

    private TypeName getTypeName(Type type, Type.ClassType ignoreType) {
        TypeName[] typeNames = new TypeName[type.allparams().size()];
        if (type.allparams().size() > 0) {
            for (int i = 0; i < type.allparams().size(); i++) {
                Type.ClassType classType = (Type.ClassType) type.allparams().get(i);
                if (ignoreType != null && classType.tsym.flatName().toString().equals(ignoreType.tsym.flatName().toString())) {//过滤不需要记录的ClassType
                    if (classType.allparams().size() > 0) {
                        for (int n = 0; n < classType.allparams().size(); n++) {
                            typeNames[i] = getTypeName(classType.allparams().get(n), ignoreType);
                        }
                    }
                } else if (classType.allparams().size() > 0) {
                    typeNames[i] = getTypeName(classType, ignoreType);
                } else {//过滤不需要记录的ClassType
                    typeNames[i] = ClassName.get((Symbol.ClassSymbol) classType.tsym);
                }
            }
        }
        if (typeNames.length == 0) {
            return ClassName.get((Symbol.ClassSymbol) type.tsym);
        } else {
            return ParameterizedTypeName.get(ClassName.get((Symbol.ClassSymbol) type.tsym), typeNames);
        }
    }

    private UseCaseSet.Builder getOrCreateBindingBuilder(Map<TypeElement, UseCaseSet.Builder> builderMap,
                                                         TypeElement enclosingElement) {
        UseCaseSet.Builder builder = builderMap.get(enclosingElement);
        if (builder == null) {
            builder = UseCaseSet.newBuilder(enclosingElement);
            builderMap.put(enclosingElement, builder);
        }
        return builder;
    }

    /**
     * 验证方法是否是公开方法，或者类是否是接口类型
     */
    private boolean isInaccessibleViaGeneratedCode(Class<? extends Annotation> annotationClass,
                                                   Element element) {
        boolean hasError = false;
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

        // 验证方法
        Set<Modifier> modifiers = element.getModifiers();
        if (modifiers.contains(PRIVATE) || modifiers.contains(STATIC)) {
            error(element, "@%s 注解方法不能有private或者static修饰. (%s.%s)", annotationClass.getSimpleName(),
                    enclosingElement.getQualifiedName(), element.getSimpleName());
            hasError = true;
        }

        // 验证类型
        if (enclosingElement.getKind() != INTERFACE) {
            error(enclosingElement, "@%s 注解类只能是接口类型. (%s.%s)", annotationClass.getSimpleName(),
                    enclosingElement.getQualifiedName(), element.getSimpleName());
            hasError = true;
        }
        return hasError;
    }

    /**
     * 验证包名
     */
    private boolean isBindingInWrongPackage(Class<? extends Annotation> annotationClass, Element element) {
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
        String qualifiedName = enclosingElement.getQualifiedName().toString();

        if (qualifiedName.startsWith("android.")) {
            error(element, "@%s-注解不能是android framework包名. (%s)", annotationClass.getSimpleName(),
                    qualifiedName);
            return true;
        }
        if (qualifiedName.startsWith("java.")) {
            error(element, "@%s-注解不能是java framework包名. (%s)", annotationClass.getSimpleName(),
                    qualifiedName);
            return true;
        }

        return false;
    }

    private void error(Element element, String message, Object... args) {
        printMessage(Diagnostic.Kind.ERROR, element, message, args);
    }

    private void note(Element element, String message, Object... args) {
        printMessage(Diagnostic.Kind.NOTE, element, message, args);
    }

    private void printMessage(Diagnostic.Kind kind, Element element, String message, Object[] args) {
        if (args.length > 0) {
            message = String.format(message, args);
        }

        processingEnv.getMessager().printMessage(kind, message, element);
    }

    private void logParsingError(Element element, Class<? extends Annotation> annotation, Exception e) {
        StringWriter stackTrace = new StringWriter();
        e.printStackTrace(new PrintWriter(stackTrace));
        error(element, "解析错误： @%s binding.\n\n%s", annotation.getSimpleName(), stackTrace);
    }
}
