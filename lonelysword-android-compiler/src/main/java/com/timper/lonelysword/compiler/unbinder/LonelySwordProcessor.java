package com.timper.lonelysword.compiler.unbinder;

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableSet;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.sun.source.util.Trees;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeScanner;
import com.timper.lonelysword.annotations.apt.AfterViews;
import com.timper.lonelysword.annotations.apt.BeforeViews;
import com.timper.lonelysword.annotations.apt.DisableNetwork;
import com.timper.lonelysword.annotations.apt.EnableNetwork;
import com.timper.lonelysword.annotations.apt.ModelAdapter;
import com.timper.lonelysword.annotations.apt.RootView;
import com.timper.lonelysword.annotations.apt.ViewModel;
import com.timper.lonelysword.compiler.Utils;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import static com.timper.lonelysword.compiler.unbinder.BindingSet.ACTIVITY_TYPE;
import static com.timper.lonelysword.compiler.unbinder.BindingSet.APPACTIVITY_TYPE;
import static com.timper.lonelysword.compiler.unbinder.BindingSet.APPFRAGMENT_TYPE;
import static com.timper.lonelysword.compiler.unbinder.BindingSet.FRAGMENT_TYPE;
import static com.timper.lonelysword.compiler.unbinder.BindingSet.V4FRAGMENT_TYPE;
import static com.timper.lonelysword.compiler.unbinder.ViewModelBinding.VIEW;
import static javax.lang.model.element.ElementKind.CLASS;
import static javax.lang.model.element.ElementKind.METHOD;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.STATIC;

/**
 * User: tangpeng.yang
 * Date: 17/05/2018
 * Description:
 * FIXME
 */
@AutoService(Processor.class) @SupportedSourceVersion(SourceVersion.RELEASE_8) public class LonelySwordProcessor
    extends AbstractProcessor {

  private static final String OPTION_SDK_INT = "lonelysword.minSdk";
  private static final String OPTION_DEBUGGABLE = "lonelysword.debuggable";

  static final String VIEWMODEL_TYPE = "android.arch.lifecycle.ViewModel";

  private Types typeUtils;
  private Filer filer;
  private Trees trees;
  private Elements elements;

  private int sdk = 1;
  private boolean debuggable = true;

  private final RScanner rScanner = new RScanner();

  @Override public synchronized void init(ProcessingEnvironment env) {
    super.init(env);
    elements = processingEnv.getElementUtils();
    typeUtils = env.getTypeUtils();
    filer = env.getFiler();
    try {
      trees = Trees.instance(processingEnv);
    } catch (IllegalArgumentException ignored) {
    }
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
    annotations.add(RootView.class);
    annotations.add(BeforeViews.class);
    annotations.add(AfterViews.class);
    return annotations;
  }

  @Override public boolean process(Set<? extends TypeElement> set, RoundEnvironment env) {

    Map<TypeElement, BindingSet> bindingMap = parseTargets(env);

    for (Map.Entry<TypeElement, BindingSet> entry : bindingMap.entrySet()) {
      TypeElement typeElement = entry.getKey();
      BindingSet binding = entry.getValue();

      JavaFile javaFile = binding.brewJava(sdk, debuggable);
      try {
        javaFile.writeTo(filer);
      } catch (IOException e) {
        error(typeElement, "Unable to write binding for type %s: %s", typeElement, e.getMessage());
      }
    }
    return true;
  }

  private Id elementToId(Element element, Class<? extends Annotation> annotation, int value) {
    JCTree tree = (JCTree) trees.getTree(element, Utils.getMirror(element, annotation));
    if (tree != null) { // tree can be null if the references are compiled types and not source
      rScanner.reset();
      tree.accept(rScanner);
      return rScanner.resourceIds.values().iterator().next();
    }
    return new Id(value);
  }

  private Map<TypeElement, BindingSet> parseTargets(RoundEnvironment env) {
    Map<TypeElement, BindingSet.Builder> builderMap = new LinkedHashMap<>();
    Set<TypeElement> erasedTargetNames = new LinkedHashSet<>();

    // Process each @RootView element.
    for (Element element : env.getElementsAnnotatedWith(RootView.class)) {
      // we don't SuperficialValidation.validateElement(element)
      // so that an unresolved View type can be generated by later processing rounds
      try {
        parseRootView(element, builderMap, erasedTargetNames);
      } catch (Exception e) {
        logParsingError(element, RootView.class, e);
      }
    }

    // Process each @BeforeViews element.
    for (Element element : env.getElementsAnnotatedWith(BeforeViews.class)) {
      // we don't SuperficialValidation.validateElement(element)
      // so that an unresolved View type can be generated by later processing rounds
      try {
        parseBeforViews(BeforeViews.class, element, builderMap, erasedTargetNames);
      } catch (Exception e) {
        logParsingError(element, BeforeViews.class, e);
      }
    }

    // Process each @AfterViews element.
    for (Element element : env.getElementsAnnotatedWith(AfterViews.class)) {
      // we don't SuperficialValidation.validateElement(element)
      // so that an unresolved View type can be generated by later processing rounds
      try {
        parseAfterViews(AfterViews.class, element, builderMap, erasedTargetNames);
      } catch (Exception e) {
        logParsingError(element, AfterViews.class, e);
      }
    }

    // Process each @DisableNetwork element.
    for (Element element : env.getElementsAnnotatedWith(DisableNetwork.class)) {
      // we don't SuperficialValidation.validateElement(element)
      // so that an unresolved View type can be generated by later processing rounds
      try {
        parseDisableNetwork(DisableNetwork.class, element, builderMap, erasedTargetNames);
      } catch (Exception e) {
        logParsingError(element, DisableNetwork.class, e);
      }
    }

    // Process each @EnableNetwork element.
    for (Element element : env.getElementsAnnotatedWith(EnableNetwork.class)) {
      // we don't SuperficialValidation.validateElement(element)
      // so that an unresolved View type can be generated by later processing rounds
      try {
        parseEnableNetwork(EnableNetwork.class, element, builderMap, erasedTargetNames);
      } catch (Exception e) {
        logParsingError(element, EnableNetwork.class, e);
      }
    }

    // Process each @ViewModel element.
    for (Element element : env.getElementsAnnotatedWith(ViewModel.class)) {
      // we don't SuperficialValidation.validateElement(element)
      // so that an unresolved View type can be generated by later processing rounds
      try {
        parseViewModel(element, builderMap, erasedTargetNames);
      } catch (Exception e) {
        logParsingError(element, EnableNetwork.class, e);
      }
    }

    // Process each @ModelAdapter element.
    for (Element element : env.getElementsAnnotatedWith(ModelAdapter.class)) {
      // we don't SuperficialValidation.validateElement(element)
      // so that an unresolved View type can be generated by later processing rounds
      try {
        parseModelAdapter(element, builderMap, erasedTargetNames);
      } catch (Exception e) {
        logParsingError(element, EnableNetwork.class, e);
      }
    }

    // Associate superclass binders with their subclass binders. This is a queue-based tree walk
    // which starts at the roots (superclasses) and walks to the leafs (subclasses).
    Deque<Map.Entry<TypeElement, BindingSet.Builder>> entries = new ArrayDeque<>(builderMap.entrySet());
    Map<TypeElement, BindingSet> bindingMap = new LinkedHashMap<>();
    while (!entries.isEmpty()) {
      Map.Entry<TypeElement, BindingSet.Builder> entry = entries.removeFirst();

      TypeElement type = entry.getKey();
      BindingSet.Builder builder = entry.getValue();

      TypeElement parentType = Utils.findParentType(type, erasedTargetNames);
      if (parentType == null) {
        bindingMap.put(type, builder.build());
      } else {
        BindingSet parentBinding = bindingMap.get(parentType);
        if (parentBinding != null) {
          builder.setParent(parentBinding);
          bindingMap.put(type, builder.build());
        } else {
          // Has a superclass binding but we haven't built it yet. Re-enqueue for later.
          entries.addLast(entry);
        }
      }
    }
    return bindingMap;
  }

  private void parseRootView(Element element, Map<TypeElement, BindingSet.Builder> builderMap,
      Set<TypeElement> erasedTargetNames) {
    TypeElement enclosingElement = (TypeElement) element;

    boolean hasError = false;

    TypeMirror elementType = element.asType();
    Name simpleName = element.getSimpleName();
    Name qualifiedName = enclosingElement.getQualifiedName();

    if (!Utils.isSubtypeOfType(elementType, APPACTIVITY_TYPE)
        && !Utils.isSubtypeOfType(elementType, APPFRAGMENT_TYPE)
        && !Utils.isSubtypeOfType(elementType, V4FRAGMENT_TYPE)
        && !Utils.isInterface(elementType)) {
      if (elementType.getKind() == TypeKind.ERROR) {
        note(element, "@%s field with unresolved type (%s) " + "must elsewhere be generated as a View or interface. (%s.%s)",
            RootView.class.getSimpleName(), elementType, qualifiedName, simpleName);
      } else {
        error(element, "@%s fields must extend from View or be an interface. (%s.%s)", RootView.class.getSimpleName(),
            qualifiedName, simpleName);
        hasError = true;
      }
    }

    com.sun.tools.javac.util.List<Type> types = ((Type.ClassType) enclosingElement.getSuperclass()).getTypeArguments();
    if (types.size() <= 0) {
      error(element, "@%s fields and viewModel must at the same time set up.(%s.%s)", RootView.class.getSimpleName(),
          qualifiedName, simpleName);
      hasError = true;
    }

    Type type = types.get(0);
    if (type.getKind() == TypeKind.TYPEVAR) {
      type = ((Type.TypeVar) type).bound;
      if (type == null) {
        error(element, "@%s fields and viewModel must at the same time set up.(%s.%s)", RootView.class.getSimpleName(),
            qualifiedName, simpleName);
        hasError = true;
      }
    }

    if (hasError) {
      return;
    }

    //add viewmodel to BindingSet
    ClassName className = (ClassName) ClassName.get(type);
    BindingSet.Builder builder = getOrCreateBindingBuilder(builderMap, enclosingElement);
    ViewModelFactorBinding.Builder modelAdapter =
        new ViewModelFactorBinding.Builder(className, "viewModel", builder.isActivity(), builder.isFragment());
    modelAdapter.addFactorName("factor");
    builder.addModelAdater(modelAdapter);

    // Assemble information on the field.
    int id = element.getAnnotation(RootView.class).value();
    Id resourceId = elementToId(element, RootView.class, id);
    builder.addRootView(new RootViewBinding(resourceId, className, builder.isActivity(), builder.isFragment()));
    // Add the type-erased version to the valid binding targets set.
    erasedTargetNames.add(enclosingElement);
  }

  private void parseBeforViews(Class<? extends Annotation> annotationClass, Element element,
      Map<TypeElement, BindingSet.Builder> builderMap, Set<TypeElement> erasedTargetNames) {
    DefaultMethodBinding lifeCycleBinding = parseLifeCycle(annotationClass, element);
    if (lifeCycleBinding != null) {
      TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
      BindingSet.Builder builder = getOrCreateBindingBuilder(builderMap, enclosingElement);
      builder.addBeforViews(lifeCycleBinding);
      erasedTargetNames.add(enclosingElement);
    }
  }

  private void parseAfterViews(Class<? extends Annotation> annotationClass, Element element,
      Map<TypeElement, BindingSet.Builder> builderMap, Set<TypeElement> erasedTargetNames) {
    DefaultMethodBinding lifeCycleBinding = parseLifeCycle(annotationClass, element);
    if (lifeCycleBinding != null) {
      TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
      BindingSet.Builder builder = getOrCreateBindingBuilder(builderMap, enclosingElement);
      builder.addAfterViews(lifeCycleBinding);
      erasedTargetNames.add(enclosingElement);
    }
  }

  private void parseDisableNetwork(Class<? extends Annotation> annotationClass, Element element,
      Map<TypeElement, BindingSet.Builder> builderMap, Set<TypeElement> erasedTargetNames) {
    DefaultMethodBinding lifeCycleBinding = parseLifeCycle(annotationClass, element);
    if (lifeCycleBinding != null) {
      TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
      BindingSet.Builder builder = getOrCreateBindingBuilder(builderMap, enclosingElement);
      builder.addDisableNetwork(lifeCycleBinding);
      erasedTargetNames.add(enclosingElement);
    }
  }

  private void parseEnableNetwork(Class<? extends Annotation> annotationClass, Element element,
      Map<TypeElement, BindingSet.Builder> builderMap, Set<TypeElement> erasedTargetNames) {
    DefaultMethodBinding lifeCycleBinding = parseLifeCycle(annotationClass, element);
    if (lifeCycleBinding != null) {
      TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
      BindingSet.Builder builder = getOrCreateBindingBuilder(builderMap, enclosingElement);
      builder.addEnableNetwork(lifeCycleBinding);
      erasedTargetNames.add(enclosingElement);
    }
  }

  private DefaultMethodBinding parseLifeCycle(Class<? extends Annotation> annotationClass, Element element) {
    // This should be guarded by the annotation's @Target but it's worth a check for safe casting.
    if (!(element instanceof ExecutableElement) || element.getKind() != METHOD) {
      throw new IllegalStateException(String.format("@%s annotation must be on a method.", annotationClass.getSimpleName()));
    }

    ExecutableElement executableElement = (ExecutableElement) element;
    TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

    // Verify that the method and its containing class are accessible via generated code.
    boolean hasError = isInaccessibleViaGeneratedCode(annotationClass, "methods", element);
    hasError |= isBindingInWrongPackage(annotationClass, element);

    String name = executableElement.getSimpleName().toString();

    // Verify that the method no parameters.
    List<? extends VariableElement> methodParameters = executableElement.getParameters();
    if (methodParameters != null && methodParameters.size() > 0) {
      error(element, "@%s methods no parameters", annotationClass.getSimpleName());
      hasError = true;
    }

    // Verify method return type matches the beforViews.
    TypeMirror returnType = executableElement.getReturnType();
    if (returnType instanceof TypeVariable) {
      TypeVariable typeVariable = (TypeVariable) returnType;
      returnType = typeVariable.getUpperBound();
    }
    if (!returnType.toString().equals("void")) {
      error(element, "@%s methods must have a '%s' return type. (%s.%s)", annotationClass.getSimpleName(), "void",
          enclosingElement.getQualifiedName(), element.getSimpleName());
      hasError = true;
    }

    if (hasError) {
      return null;
    }

    return new DefaultMethodBinding(name, Arrays.asList(Parameter.NONE), true);
  }

  private void parseModelAdapter(Element element, Map<TypeElement, BindingSet.Builder> builderMap,
      Set<TypeElement> erasedTargetNames) {

    TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

    // Start by verifying common generated code restrictions.
    boolean hasError =
        isInaccessibleViaGeneratedCode(ModelAdapter.class, "fields", element) || isBindingInWrongPackage(ModelAdapter.class,
            element);

    // Verify that the target type extends from View.
    TypeMirror elementType = element.asType();
    if (elementType.getKind() == TypeKind.TYPEVAR) {
      TypeVariable typeVariable = (TypeVariable) elementType;
      elementType = typeVariable.getUpperBound();
    }
    Name qualifiedName = enclosingElement.getQualifiedName();
    Name simpleName = element.getSimpleName();
    String packageName = elements.getPackageOf(element).getQualifiedName().toString();
    if (!Utils.isSubtypeOfType(elementType, VIEWMODEL_TYPE) && !Utils.isInterface(elementType)) {
      if (elementType.getKind() == TypeKind.ERROR) {
        note(element, "@%s field with unresolved type (%s) " + "must elsewhere be generated as a View or interface. (%s.%s)",
            ModelAdapter.class.getSimpleName(), elementType, qualifiedName, simpleName);
      } else {
        error(element, "@%s fields must extend from View or be an interface. (%s.%s)", ModelAdapter.class.getSimpleName(),
            qualifiedName, simpleName);
        hasError = true;
      }
    }

    String factorName = element.getAnnotation(ModelAdapter.class).value();
    ClassName className = ClassName.get(packageName, elementType.toString());

    BindingSet.Builder builder = getOrCreateBindingBuilder(builderMap, enclosingElement);

    ViewModelFactorBinding.Builder modelAdapter =
        new ViewModelFactorBinding.Builder(className, simpleName.toString(), builder.isActivity(), builder.isFragment());
    modelAdapter.addFactorName(factorName);
    if (!builder.addModelAdater(modelAdapter)) {
      error(element, "@%s the same ModelAdapter name.", ModelAdapter.class);
      hasError = true;
    }

    if (hasError) {
      return;
    }

    // Add the type-erased version to the valid binding targets set.
    erasedTargetNames.add(enclosingElement);
  }

  private void parseViewModel(Element element, Map<TypeElement, BindingSet.Builder> builderMap,
      Set<TypeElement> erasedTargetNames) {

    TypeElement enclosingElement = null;
    // Start by verifying common generated code restrictions.
    boolean hasError = false;

    String simpleName;
    if (element.getKind().isClass()) {
      enclosingElement = (TypeElement) element;
      simpleName = VIEW;
    } else if (element.getKind().isField()) {
      enclosingElement = (TypeElement) element.getEnclosingElement();

      // Start by verifying common generated code restrictions.
      hasError =
          isInaccessibleViaGeneratedCode(ViewModel.class, "fields", element) || isBindingInWrongPackage(ViewModel.class, element);

      simpleName = element.getSimpleName().toString();
    } else {
      error(element, "@%s is only bind class and field.", ViewModel.class);
      return;
    }

    Name qualifiedName = enclosingElement.getQualifiedName();

    // Verify that the target type extends from View.
    TypeMirror elementType = enclosingElement.asType();
    if (elementType.getKind() == TypeKind.TYPEVAR) {
      TypeVariable typeVariable = (TypeVariable) elementType;
      elementType = typeVariable.getUpperBound();
    }

    if (!Utils.isSubtypeOfType(elementType, ACTIVITY_TYPE)
        && !Utils.isSubtypeOfType(elementType, FRAGMENT_TYPE)
        && !Utils.isSubtypeOfType(elementType, V4FRAGMENT_TYPE)
        && !Utils.isInterface(elementType)) {
      if (elementType.getKind() == TypeKind.ERROR) {
        note(element, "@%s field with unresolved type (%s) " + "must elsewhere be generated as a View or interface. (%s.%s)",
            ViewModel.class.getSimpleName(), elementType, qualifiedName, simpleName);
      } else {
        error(element, "@%s fields must extend from View or be an interface. (%s.%s)", ViewModel.class.getSimpleName(),
            qualifiedName, simpleName);
        hasError = true;
      }
    }

    String viewModelName = element.getAnnotation(ViewModel.class).value();

    ViewModelBinding.Builder viewModel = new ViewModelBinding.Builder(simpleName.toString());
    viewModel.addViewModelName(viewModelName);
    BindingSet.Builder builder = getOrCreateBindingBuilder(builderMap, enclosingElement);
    if (!builder.addViewModel(viewModel)) {
      error(element, "@%s the same viewModel name.", ViewModel.class);
      hasError = true;
    }

    if (hasError) {
      return;
    }

    // Add the type-erased version to the valid binding targets set.
    erasedTargetNames.add(enclosingElement);
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
    if (enclosingElement.getKind() != CLASS) {
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

  private BindingSet.Builder getOrCreateBindingBuilder(Map<TypeElement, BindingSet.Builder> builderMap,
      TypeElement enclosingElement) {
    BindingSet.Builder builder = builderMap.get(enclosingElement);
    if (builder == null) {
      builder = BindingSet.newBuilder(enclosingElement);
      builderMap.put(enclosingElement, builder);
    }
    return builder;
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

  private static class RScanner extends TreeScanner {
    Map<Integer, Id> resourceIds = new LinkedHashMap<>();

    @Override public void visitSelect(JCTree.JCFieldAccess jcFieldAccess) {
      Symbol symbol = jcFieldAccess.sym;
      if (symbol.getEnclosingElement() != null
          && symbol.getEnclosingElement().getEnclosingElement() != null
          && symbol.getEnclosingElement().getEnclosingElement().enclClass() != null) {
        try {
          int value = (Integer) ((Symbol.VarSymbol) symbol).getConstantValue();
          resourceIds.put(value, new Id(value, symbol));
        } catch (Exception ignored) {
        }
      }
    }

    @Override public void visitLiteral(JCTree.JCLiteral jcLiteral) {
      try {
        int value = (Integer) jcLiteral.value;
        resourceIds.put(value, new Id(value));
      } catch (Exception ignored) {
      }
    }

    void reset() {
      resourceIds.clear();
    }
  }
}
