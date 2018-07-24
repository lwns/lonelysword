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

import static com.timper.lonelysword.compiler.dagger.DaggerSet.ACTIVITYSCOPE;
import static com.timper.lonelysword.compiler.dagger.DaggerSet.CONTRIBUTESANDROIDINJECTOR;
import static com.timper.lonelysword.compiler.dagger.DaggerSet.MODULE;
import static javax.lang.model.element.ElementKind.INTERFACE;
import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

/**
 * User: tangpeng.yang
 * Date: 17/05/2018
 * Description:
 * FIXME
 */
@AutoService(Processor.class) @SupportedSourceVersion(SourceVersion.RELEASE_7) public class DaggerProcessor
    extends AbstractProcessor {

  private static final String OPTION_SDK_INT = "lonelysword.minSdk";
  private static final String OPTION_DEBUGGABLE = "lonelysword.debuggable";

  static final String APPACTIVITY_TYPE = "com.timper.lonelysword.base.AppActivity<?,?>";
  static final String APPFRAGMENT_TYPE = "com.timper.lonelysword.base.AppFragment<?,?>";

  private static final String DAGGERPACKAGENAME = "lonelysword.di";

  static final ClassName OBJECT = ClassName.get("java.lang", "Object");

  private Types typeUtils;
  private Filer filer;
  private Elements elements;

  private int sdk = 1;
  private boolean debuggable = true;

  @Override public synchronized void init(ProcessingEnvironment env) {
    super.init(env);
    elements = processingEnv.getElementUtils();
    typeUtils = env.getTypeUtils();
    filer = env.getFiler();
  }

  @Override public Set<String> getSupportedOptions() {
    return ImmutableSet.of(OPTION_SDK_INT, OPTION_DEBUGGABLE);
  }

  @Override public Set<String> getSupportedAnnotationTypes() {
    Set<String> types = new LinkedHashSet<>();
    for (Class<? extends Annotation> annotation : getSupportedAnnotations()) {
      types.add(annotation.getCanonicalName());
    }
    return types;
  }

  private Set<Class<? extends Annotation>> getSupportedAnnotations() {
    Set<Class<? extends Annotation>> annotations = new LinkedHashSet<>();
    annotations.add(Dagger.class);
    return annotations;
  }

  @Override public boolean process(Set<? extends TypeElement> set, RoundEnvironment env) {
    Map<TypeElement, DaggerSet> bindingMap = parseTargets(env);

    if (bindingMap != null && bindingMap.size() > 0) {
      brewActivityModuleJava(bindingMap);
    }
    for (Map.Entry<TypeElement, DaggerSet> entry : bindingMap.entrySet()) {
      TypeElement typeElement = entry.getKey();
      DaggerSet binding = entry.getValue();
      try {
        binding.brewJava(filer, sdk, debuggable);
      } catch (IOException e) {
        error(typeElement, "Unable to write binding for type %s: %s", typeElement, e.getMessage());
      }
    }
    return true;
  }

  private Map<TypeElement, DaggerSet> parseTargets(RoundEnvironment env) {
    //activity modules
    Map<TypeElement, DaggerSet.Builder> builderMap = new LinkedHashMap<>();
    //fragmeng modules
    Map<String, SubModuleBinding> subModuleMap = new LinkedHashMap<>();
    //other modules
    Map<String, SubModuleBinding> otherModuleMap = new LinkedHashMap<>();

    Set<TypeElement> erasedTargetNames = new LinkedHashSet<>();

    // Process each @Dagger element.
    for (Element element : env.getElementsAnnotatedWith(Dagger.class)) {
      // we don't SuperficialValidation.validateElement(element)
      // so that an unresolved View type can be generated by later processing rounds
      try {

        TypeMirror elementType = element.asType();
        if (Utils.isSubtypeOfType(elementType, APPACTIVITY_TYPE)) {
          parseModuleDagger(element, builderMap, erasedTargetNames);
        } else {
          parseSubModuleDagger(element, subModuleMap, otherModuleMap);
        }
      } catch (Exception e) {
        logParsingError(element, Dagger.class, e);
      }
    }

    //fragment modules add to activity
    for (Map.Entry<String, SubModuleBinding> entry : subModuleMap.entrySet()) {
      DaggerSet.Builder builder = getBindingBuilder(builderMap, entry.getValue().getModuleName());
      if (builder != null) {
        builder.addSubModuleBinding(entry.getValue().getModuleName(), entry.getValue().getSubClassName());
      }
    }

    //other modules add to activity
    for (Map.Entry<String, SubModuleBinding> entry : otherModuleMap.entrySet()) {
      DaggerSet.Builder builder = getBindingBuilder(builderMap, entry.getValue().getModuleName());
      if (builder != null) {
        builder.addOtherModuleBinding(entry.getValue().getModuleName(), entry.getValue().getSubClassName());
      }
    }

    // Associate superclass binders with their subclass binders. This is a queue-based tree walk
    // which starts at the roots (superclasses) and walks to the leafs (subclasses).
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

  private void parseModuleDagger(Element element, Map<TypeElement, DaggerSet.Builder> builderMap,
      Set<TypeElement> erasedTargetNames) {

    TypeElement enclosingElement = (TypeElement) element;

    boolean hasError = false;

    TypeMirror elementType = element.asType();
    Name simpleName = element.getSimpleName();
    Name qualifiedName = enclosingElement.getQualifiedName();

    if (!Utils.isSubtypeOfType(elementType, APPACTIVITY_TYPE) && !Utils.isInterface(elementType)) {
      if (elementType.getKind() == TypeKind.ERROR) {
        note(element, "@%s field with unresolved type (%s) " + "must elsewhere be generated as a View or interface. (%s.%s)",
            Dagger.class.getSimpleName(), elementType, qualifiedName, simpleName);
      } else {
        error(element, "@%s fields must extend from View or be an interface. (%s.%s)", Dagger.class.getSimpleName(),
            qualifiedName, simpleName);
        hasError = true;
      }
    }

    if (Utils.isSubtypeOfType(elementType, APPACTIVITY_TYPE)) {
      DaggerSet.Builder builder = getOrCreateBindingBuilder(builderMap, enclosingElement);
      String value = simpleName.toString().replace("Activity", "");
      builder.addDaggerBinding(simpleName.toString(), value);
    }

    if (hasError) {
      return;
    }
    erasedTargetNames.add(enclosingElement);
  }

  private void parseSubModuleDagger(Element element, Map<String, SubModuleBinding> subModuleMap,
      Map<String, SubModuleBinding> otherModuleMap) {

    TypeElement enclosingElement = (TypeElement) element;

    boolean hasError = false;

    TypeMirror elementType = element.asType();
    Name simpleName = element.getSimpleName();
    Name qualifiedName = enclosingElement.getQualifiedName();

    if (elementType.getKind() == TypeKind.ERROR) {
      note(element, "@%s field with unresolved type (%s) " + "must elsewhere be generated as a View or interface. (%s.%s)",
          Dagger.class.getSimpleName(), elementType, qualifiedName, simpleName);
    }

    AnnotationValue action = null;
    for (AnnotationMirror m : element.getAnnotationMirrors()) {
      if (m.getAnnotationType().toString().equals(Dagger.class.getCanonicalName())) {
        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : m.getElementValues().entrySet()) {
          if ("value".equals(entry.getKey().getSimpleName().toString())) {
            action = entry.getValue();
            break;
          }
        }
      }
    }

    Type.ClassType classType = null;
    String moduleName = null;
    if (action == null) {
      error(element, "@%s fields must have a value for subModule. (%s.%s)", Dagger.class.getSimpleName(), qualifiedName,
          simpleName);
      hasError = true;
    } else {
      Object value = action.getValue();
      if (value instanceof Type.ClassType) {
        classType = (Type.ClassType) value;
        moduleName = classType.tsym.name.toString();
      } else {
        error(element, "@%s fields must an Class<?> object. (%s.%s)", Dagger.class.getSimpleName(), qualifiedName, simpleName);
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

  private TypeName getTypeName(Type type) {
    TypeName[] typeNames = new TypeName[type.allparams().size()];
    if (type.allparams().size() > 0) {
      for (int i = 0; i < type.allparams().size(); i++) {
        Type.ClassType classType = (Type.ClassType) type.allparams().get(i);
        if (classType.allparams().size() > 0) {
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

  private DaggerSet.Builder getOrCreateBindingBuilder(Map<TypeElement, DaggerSet.Builder> builderMap,
      TypeElement enclosingElement) {
    DaggerSet.Builder builder = builderMap.get(enclosingElement);
    if (builder == null) {
      builder = DaggerSet.newBuilder(enclosingElement);
      builderMap.put(enclosingElement, builder);
    }
    return builder;
  }

  private DaggerSet.Builder getBindingBuilder(Map<TypeElement, DaggerSet.Builder> builderMap, String moduleName) {
    for (Map.Entry<TypeElement, DaggerSet.Builder> entry : builderMap.entrySet()) {
      if (entry.getKey().getSimpleName().toString().equals(moduleName)) {
        return entry.getValue();
      }
    }
    return null;
  }

  private void brewActivityModuleJava(Map<TypeElement, DaggerSet> bindingMap) {
    try {
      JavaFile javaFile = JavaFile.builder(DAGGERPACKAGENAME, createActivityModuleType(bindingMap, sdk, debuggable))
          .addFileComment("Generated code from lonely sword. Do not modify!")
          .build();
      javaFile.writeTo(filer);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private TypeSpec createActivityModuleType(Map<TypeElement, DaggerSet> bindings, int sdk, boolean debuggable) {
    String className = "ActivityModule";
    TypeSpec.Builder result = TypeSpec.classBuilder(className).addModifiers(PUBLIC);
    result.addModifiers(ABSTRACT);
    result.addAnnotation(MODULE);

    for (DaggerSet binding : bindings.values()) {
      MethodSpec.Builder builder =
          MethodSpec.methodBuilder("bind" + binding.daggerBinding.getModuleName()).addModifiers(ABSTRACT);
      builder.addAnnotation(ACTIVITYSCOPE);

      ClassName moduleClass =
          ClassName.get(binding.bindingClassName.packageName(), binding.daggerBinding.getSimpleName() + "Module");
      ClassName subModuleClass =
          ClassName.get(binding.bindingClassName.packageName(), binding.daggerBinding.getSimpleName() + "SubModule");
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

  /**
   * wrong method varify
   */
  private boolean isInaccessibleViaGeneratedCode(Class<? extends Annotation> annotationClass, String targetThing,
      Element element) {
    boolean hasError = false;
    TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

    // Verify field or method modifiers.
    Set<Modifier> modifiers = element.getModifiers();
    if (modifiers.contains(PRIVATE) || modifiers.contains(STATIC)) {
      error(element, "@%s %s must not be private or static. (%s.%s)", annotationClass.getSimpleName(), targetThing,
          enclosingElement.getQualifiedName(), element.getSimpleName());
      hasError = true;
    }

    // Verify containing type.
    if (enclosingElement.getKind() != INTERFACE) {
      error(enclosingElement, "@%s %s may only be contained in classes. (%s.%s)", annotationClass.getSimpleName(), targetThing,
          enclosingElement.getQualifiedName(), element.getSimpleName());
      hasError = true;
    }

    // Verify containing class visibility is not private.
    if (enclosingElement.getModifiers().contains(PRIVATE)) {
      error(enclosingElement, "@%s %s may not be contained in private classes. (%s.%s)", annotationClass.getSimpleName(),
          targetThing, enclosingElement.getQualifiedName(), element.getSimpleName());
      hasError = true;
    }

    return hasError;
  }

  /**
   * wrong package varify
   */
  private boolean isBindingInWrongPackage(Class<? extends Annotation> annotationClass, Element element) {
    TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
    String qualifiedName = enclosingElement.getQualifiedName().toString();

    if (qualifiedName.startsWith("android.")) {
      error(element, "@%s-annotated class incorrectly in Android framework package. (%s)", annotationClass.getSimpleName(),
          qualifiedName);
      return true;
    }
    if (qualifiedName.startsWith("java.")) {
      error(element, "@%s-annotated class incorrectly in Java framework package. (%s)", annotationClass.getSimpleName(),
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
    error(element, "Unable to parse @%s binding.\n\n%s", annotation.getSimpleName(), stackTrace);
  }
}
