package com.timper.lonelysword.compiler.dagger;

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableSet;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import com.timper.lonelysword.annotations.apt.Dagger;
import com.timper.lonelysword.annotations.apt.DaggerApplication;
import com.timper.lonelysword.compiler.Utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import static com.timper.lonelysword.compiler.Consts.KEY_MODULE_NAME;
import static com.timper.lonelysword.compiler.dagger.DaggerSet.ACTIVITYSCOPE;
import static com.timper.lonelysword.compiler.dagger.DaggerSet.CONTRIBUTESANDROIDINJECTOR;
import static com.timper.lonelysword.compiler.dagger.DaggerSet.MODULE;
import static javax.lang.model.element.ElementKind.INTERFACE;
import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

/**
 * User: tangpeng.yang
 * Date: 17/05/2018
 * Description: @Dagger解析器
 * FIXME
 */
@AutoService(Processor.class)
@SupportedOptions({KEY_MODULE_NAME})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class DaggerProcessor extends AbstractProcessor {

    private static final String OPTION_SDK_INT = "lonelysword.minSdk";
    private static final String OPTION_DEBUGGABLE = "lonelysword.debuggable";

    static final String APPACTIVITY_TYPE = "android.support.v4.app.FragmentActivity";
    static final String APPFRAGMENT_TYPE = "android.support.v4.app.Fragment";
    static final String MULTIMODULE_TYPE = "com.timper.lonelysword.dagger.DaggerMultiModule";

    private static final String LONELYSWORDPACKAGENAME = "lonelysword.di";
    private static final String DAGGERPACKAGENAME = "lonelysword.dagger";

    static final ClassName MOLTIMODULE = ClassName.get("com.timper.lonelysword.dagger", "MultiModule");
    static final ClassName DAGGERMOLTIMODULE = ClassName.get("com.timper.lonelysword.dagger", "DaggerMultiModule");
    static final ClassName MAP = ClassName.get("java.util", "Map");
    static final ClassName STRING = ClassName.get("java.lang", "String");
    static final ClassName EXCEPTION = ClassName.get("java.lang", "Exception");
    static final ClassName APPLICATION = ClassName.get("android.app", "Application");
    private static final ClassName OVERRIIDE = ClassName.get("java.lang", "Override");

    public static final String NO_MODULE_NAME_TIPS = "These no module name, at 'build.gradle', like :\n" + "android {\n" + "    defaultConfig {\n"
            + "        ...\n" + "        javaCompileOptions {\n" + "            annotationProcessorOptions {\n"
            + "                arguments = [AROUTER_MODULE_NAME: project.getName()]\n" + "            }\n" + "        }\n" + "    }\n" + "}\n";

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
        annotations.add(Dagger.class);
        annotations.add(DaggerApplication.class);
        return annotations;
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment env) {
        Map<TypeElement, DaggerSet> bindingMap = parseTargets(env);
        Map<TypeElement, DaggerSet> daggerSetApp = parseDaggerApplication(env);
        //获取每个module的name


        Map<String, String> options = processingEnv.getOptions();
        String moduleName = "";
        if (options != null && !options.isEmpty()) {
            moduleName = options.get(KEY_MODULE_NAME);
        }

        if (!Utils.isEmpty(moduleName)) {
            moduleName = moduleName.replaceAll("[^0-9a-zA-Z_]+", "");
        } else {
            throw new RuntimeException("Dagger注解器::编译 >>> 没发现ModuleName, 更多信息请看gradle build log.");
        }
        //生成AppModule类
        if (bindingMap != null && bindingMap.size() > 0) {
            brewActivityModuleJava(bindingMap, moduleName);
        }
        //生成moduler支持dagger类
        if (daggerSetApp != null && daggerSetApp.size() != 0) {
            for (Map.Entry<TypeElement, DaggerSet> entry : daggerSetApp.entrySet()) {
                DaggerSet binding = entry.getValue();
                brewMultiModuleJava(binding, bindingMap, moduleName);
            }
        }
        for (Map.Entry<TypeElement, DaggerSet> entry : bindingMap.entrySet()) {
            TypeElement typeElement = entry.getKey();
            DaggerSet binding = entry.getValue();
            try {
                binding.brewJava(filer, sdk, debuggable);
            } catch (IOException e) {
                error(typeElement, "生产类型失败： %s: %s", typeElement, e.getMessage());
            }
        }
        return true;
    }

    private Map<TypeElement, DaggerSet> parseDaggerApplication(RoundEnvironment env) {
        Map<TypeElement, DaggerSet.Builder> builderMap = new LinkedHashMap<>();

        for (Element element : env.getElementsAnnotatedWith(DaggerApplication.class)) {
            try {
                TypeMirror elementType = element.asType();
                if (Utils.isSubtypeOfType(elementType, MULTIMODULE_TYPE)) {
                    parseMultiModuleDagger(element, builderMap);
                }
            } catch (Exception e) {
                logParsingError(element, DaggerApplication.class, e);
            }
        }

        Deque<Map.Entry<TypeElement, DaggerSet.Builder>> entries = new ArrayDeque<>(builderMap.entrySet());
        Map<TypeElement, DaggerSet> bindingMap = new LinkedHashMap<>();

        while (!entries.isEmpty()) {
            Map.Entry<TypeElement, DaggerSet.Builder> entry = entries.removeFirst();

            TypeElement type = entry.getKey();
            DaggerSet.Builder builder = entry.getValue();
            bindingMap.put(type, builder.build());
        }
        return bindingMap;
    }

    private Map<TypeElement, DaggerSet> parseTargets(RoundEnvironment env) {
        //activity modules
        Map<TypeElement, DaggerSet.Builder> builderMap = new LinkedHashMap<>();
        //fragmeng modules
        Map<String, SubModuleBinding> subModuleMap = new LinkedHashMap<>();
        //other modules
        Map<String, SubModuleBinding> otherModuleMap = new LinkedHashMap<>();

        Set<TypeElement> erasedTargetNames = new LinkedHashSet<>();

        for (Element element : env.getElementsAnnotatedWith(Dagger.class)) {
            try {
                TypeMirror elementType = element.asType();
                AnnotationValue action = null;// Dagger是否有value
                for (AnnotationMirror m : element.getAnnotationMirrors()) {
                    if (m.getAnnotationType()
                            .toString()
                            .equals(Dagger.class.getCanonicalName())) {
                        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : m.getElementValues()
                                .entrySet()) {
                            if ("value".equals(entry.getKey()
                                    .getSimpleName()
                                    .toString())) {
                                action = entry.getValue();
                            }
                        }
                    }
                }

                if (Utils.isSubtypeOfType(elementType, APPACTIVITY_TYPE)) {
                    parseModuleDagger(element, builderMap, erasedTargetNames, true, false);
                } else if (Utils.isSubtypeOfType(elementType, APPFRAGMENT_TYPE)) {
                    if (action == null) {
                        parseModuleDagger(element, builderMap, erasedTargetNames, false, true);
                    } else {
                        parseSubModuleDagger(element, action, subModuleMap, otherModuleMap, false, true);
                    }
                } else {
                    parseSubModuleDagger(element, action, subModuleMap, otherModuleMap, false, false);
                }
            } catch (Exception e) {
                logParsingError(element, Dagger.class, e);
            }
        }

        //fragment modules add to activity
        for (Map.Entry<String, SubModuleBinding> entry : subModuleMap.entrySet()) {
            DaggerSet.Builder builder = getBindingBuilder(builderMap, entry.getValue()
                    .getModuleName());
            if (builder != null) {
                builder.addSubModuleBinding(entry.getValue()
                        .getModuleName(), entry.getValue()
                        .getSubClassName());
            }
        }

        //other modules add to activity
        for (Map.Entry<String, SubModuleBinding> entry : otherModuleMap.entrySet()) {
            DaggerSet.Builder builder = getBindingBuilder(builderMap, entry.getValue()
                    .getModuleName());
            if (builder != null) {
                builder.addOtherModuleBinding(entry.getValue()
                        .getModuleName(), entry.getValue()
                        .getSubClassName());
            }
        }

        Deque<Map.Entry<TypeElement, DaggerSet.Builder>> entries = new ArrayDeque<>(builderMap.entrySet());
        Map<TypeElement, DaggerSet> bindingMap = new LinkedHashMap<>();

        while (!entries.isEmpty()) {
            Map.Entry<TypeElement, DaggerSet.Builder> entry = entries.removeFirst();

            TypeElement type = entry.getKey();
            DaggerSet.Builder builder = entry.getValue();
            bindingMap.put(type, builder.build());
        }
        return bindingMap;
    }

    private void parseModuleDagger(Element element, Map<TypeElement, DaggerSet.Builder> builderMap, Set<TypeElement> erasedTargetNames,
                                   boolean isActivity, boolean isFragment) {

        TypeElement enclosingElement = (TypeElement) element;

        boolean hasError = false;

        TypeMirror elementType = element.asType();
        Name simpleName = element.getSimpleName();
        Name qualifiedName = enclosingElement.getQualifiedName();

        if (!Utils.isSubtypeOfType(elementType, APPACTIVITY_TYPE) && !Utils.isSubtypeOfType(elementType, APPFRAGMENT_TYPE) && !Utils.isInterface(
                elementType)) {
            if (elementType.getKind() == TypeKind.ERROR) {
                note(element, "@%s 注解定义在未知类型 (%s) ，(%s.%s)",
                        Dagger.class.getSimpleName(), elementType, qualifiedName, simpleName);
            } else {
                error(element, "@%s 字段必须定义在不为接口的类. (%s.%s)", Dagger.class.getSimpleName(), qualifiedName, simpleName);
                hasError = true;
            }
        }

        if (hasError) {
            return;
        }

        DaggerSet.Builder builder = getOrCreateBindingBuilder(builderMap, enclosingElement, isActivity);
        String value = simpleName.toString()
                .replace("Activity", "");
        builder.addDaggerBinding(simpleName.toString(), value);

        erasedTargetNames.add(enclosingElement);
    }

    private void parseSubModuleDagger(Element element, AnnotationValue action, Map<String, SubModuleBinding> subModuleMap,
                                      Map<String, SubModuleBinding> otherModuleMap, boolean isActivity, boolean isFragment) {

        TypeElement enclosingElement = (TypeElement) element;

        boolean hasError = false;

        TypeMirror elementType = element.asType();
        Name simpleName = element.getSimpleName();
        Name qualifiedName = enclosingElement.getQualifiedName();

        if (elementType.getKind() == TypeKind.ERROR) {
            note(element, "@%s 注解定义在未知类型 (%s) ，(%s.%s)",
                    Dagger.class.getSimpleName(), elementType, qualifiedName, simpleName);
        }

        Type.ClassType classType = null;
        String moduleName = null;
        if (action == null) {
            error(element, "@%s 注解value不能为null. (%s.%s)", Dagger.class.getSimpleName(), qualifiedName, simpleName);
            hasError = true;
        } else {
            Object value = action.getValue();
            if (value instanceof Type.ClassType) {
                classType = (Type.ClassType) value;
                moduleName = classType.tsym.name.toString();
            } else {
                error(element, "@%s 注解必须注明Activity类型. (%s.%s)", Dagger.class.getSimpleName(), qualifiedName, simpleName);
                hasError = true;
            }
        }

        if (hasError) {
            return;
        }
        ClassName className = ClassName.get(enclosingElement);
        SubModuleBinding subModuleBinding = new SubModuleBinding(moduleName, simpleName.toString(), className);
        if (Utils.isSubtypeOfType(elementType, APPFRAGMENT_TYPE)) {
            subModuleMap.put(simpleName.toString(), subModuleBinding);
        } else {
            otherModuleMap.put(simpleName.toString(), subModuleBinding);
        }
    }

    private void parseMultiModuleDagger(Element element, Map<TypeElement, DaggerSet.Builder> builderMap) {
        TypeElement enclosingElement = (TypeElement) element;

        boolean hasError = false;

        TypeMirror elementType = element.asType();
        Name simpleName = element.getSimpleName();
        Name qualifiedName = enclosingElement.getQualifiedName();

        if (elementType.getKind() == TypeKind.ERROR) {
            hasError = true;
            note(element, "@%s @%s 注解定义在未知类型 (%s) ，(%s.%s)",
                    Dagger.class.getSimpleName(), elementType, qualifiedName, simpleName);
        }
        if (hasError) {
            return;
        }

        getOrCreateBindingBuilder(builderMap, enclosingElement, false);
    }

    private TypeName getTypeName(Type type) {
        TypeName[] typeNames = new TypeName[type.allparams()
                .size()];
        if (type.allparams()
                .size() > 0) {
            for (int i = 0; i < type.allparams()
                    .size(); i++) {
                Type.ClassType classType = (Type.ClassType) type.allparams()
                        .get(i);
                if (classType.allparams()
                        .size() > 0) {
                    typeNames[i] = getTypeName(classType);
                } else {
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

    private DaggerSet.Builder getOrCreateBindingBuilder(Map<TypeElement, DaggerSet.Builder> builderMap, TypeElement enclosingElement,
                                                        boolean isActivity) {
        DaggerSet.Builder builder = builderMap.get(enclosingElement);
        if (builder == null) {
            builder = DaggerSet.newBuilder(enclosingElement, isActivity);
            builderMap.put(enclosingElement, builder);
        }
        return builder;
    }

    private DaggerSet.Builder getBindingBuilder(Map<TypeElement, DaggerSet.Builder> builderMap, String moduleName) {
        for (Map.Entry<TypeElement, DaggerSet.Builder> entry : builderMap.entrySet()) {
            if (entry.getKey()
                    .getSimpleName()
                    .toString()
                    .equals(moduleName)) {
                return entry.getValue();
            }
        }
        return null;
    }

    private void brewActivityModuleJava(Map<TypeElement, DaggerSet> bindingMap, String moduleName) {
        try {
            JavaFile javaFile = JavaFile.builder(LONELYSWORDPACKAGENAME, createActivityModuleType(bindingMap, moduleName, sdk, debuggable))
                    .addFileComment("Generated code from lonely sword. Do not modify!")
                    .build();
            javaFile.writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void brewMultiModuleJava(DaggerSet daggerSet, Map<TypeElement, DaggerSet> bindingMap, String moduleName) {
        try {
            JavaFile javaFile = JavaFile.builder(DAGGERPACKAGENAME, createMultiModuleType(daggerSet, bindingMap, moduleName, sdk, debuggable))
                    .addFileComment("Generated code from lonely sword. Do not modify!")
                    .build();
            javaFile.writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private TypeSpec createActivityModuleType(Map<TypeElement, DaggerSet> bindings, String moduleName, int sdk, boolean debuggable) {
        String className = "AppModule$$" + moduleName;
        TypeSpec.Builder result = TypeSpec.classBuilder(className)
                .addModifiers(PUBLIC);
        result.addModifiers(ABSTRACT);
        result.addAnnotation(MODULE);

        for (DaggerSet binding : bindings.values()) {
            MethodSpec.Builder builder = MethodSpec.methodBuilder("bind" + binding.daggerBinding.getModuleName())
                    .addModifiers(ABSTRACT);
            builder.addAnnotation(ACTIVITYSCOPE);

            ClassName moduleClass = ClassName.get(binding.bindingClassName.packageName(), binding.daggerBinding.getSimpleName() + "Module");
            ClassName subModuleClass = ClassName.get(binding.bindingClassName.packageName(), binding.daggerBinding.getSimpleName() + "SubModule");
            AnnotationSpec.Builder annotationBuilder = AnnotationSpec.builder(CONTRIBUTESANDROIDINJECTOR)
                    .addMember("modules", "$T.class", moduleClass)
                    .addMember("modules", "$T.class", subModuleClass);
            for (ClassName otherClassName : binding.daggerBinding.getOtherModules()) {
                annotationBuilder.addMember("modules", "$T.class", otherClassName);
            }
            builder.addAnnotation(annotationBuilder.build());

            builder.returns(binding.targetTypeName);
            result.addMethod(builder.build());
        }
        return result.build();
    }

    private TypeSpec createMultiModuleType(DaggerSet binding, Map<TypeElement, DaggerSet> bindingMap, String moduleName, int sdk, boolean debuggable) {
        String className = "MultiModule$$" + moduleName;
        TypeSpec.Builder result = TypeSpec.classBuilder(className)
                .addModifiers(PUBLIC);
        result.addModifiers(FINAL);

        result.addSuperinterface(MOLTIMODULE);

        ParameterizedTypeName parameterizedTypeName = ParameterizedTypeName.get(MAP, STRING, DAGGERMOLTIMODULE);

        MethodSpec.Builder builder = MethodSpec.methodBuilder("saveAndroidModule")
                .addAnnotation(OVERRIIDE)
                .addModifiers(PUBLIC)
                .addException(EXCEPTION);

        builder.addParameter(parameterizedTypeName, "map");
        builder.addParameter(APPLICATION, "application");
        StringBuilder statement = new StringBuilder(
                "DaggerMultiModule module = (DaggerMultiModule) Class.forName(" + "\"" + binding.targetTypeName.toString() + "\"" + ")\n"
                        + "                                                        .getConstructor()\n"
                        + "                                                        .newInstance();\n");

        statement.append("\n" +
                "        module.initDaggerMulti(application);\n");
        for (Map.Entry<TypeElement, DaggerSet> entry : bindingMap.entrySet()) {
            TypeElement typeElement = entry.getKey();
            statement.append("map.put(" + "\"" + typeElement.getQualifiedName() + "\"" + ", module);\n");
        }
        builder.addStatement(statement.toString());
        result.addMethod(builder.build());
        return result.build();
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

        processingEnv.getMessager()
                .printMessage(kind, message, element);
    }

    private void logParsingError(Element element, Class<? extends Annotation> annotation, Exception e) {
        StringWriter stackTrace = new StringWriter();
        e.printStackTrace(new PrintWriter(stackTrace));
        error(element, "解析错误 @%s binding.\n\n%s", annotation.getSimpleName(), stackTrace);
    }
}
