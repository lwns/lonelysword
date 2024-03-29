package com.timper.lonelysword.compiler;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import static javax.lang.model.element.ElementKind.INTERFACE;

/**
 * User: tangpeng.yang
 * Date: 18/05/2018
 * Description:
 * FIXME
 */
public class Utils {

  public static boolean isEmpty(CharSequence str) {
    if (str == null || "".equals(str) || "null".equals(str)) {
      return true;
    }

    for (int i = 0; i < str.length(); i++) {
      char c = str.charAt(i);
      if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
        return false;
      }
    }
    return true;
  }

  /**
   * 是否是接口
   *
   * @param typeMirror typemirror
   * @return ture
   */
  public static boolean isInterface(TypeMirror typeMirror) {
    return typeMirror instanceof DeclaredType && ((DeclaredType) typeMirror).asElement().getKind() == INTERFACE;
  }

  /**
   * 获取注解镜像
   *
   * @param element element
   * @param annotation annotation
   * @return annotations
   */
  public static AnnotationMirror getMirror(Element element, Class<? extends Annotation> annotation) {
    for (AnnotationMirror annotationMirror : element.getAnnotationMirrors()) {
      if (annotationMirror.getAnnotationType().toString().equals(annotation.getCanonicalName())) {
        return annotationMirror;
      }
    }
    return null;
  }

  /**
   * @param typeMirror typemirror
   * @param otherType othertype
   * @return typeMirror的超类是否是othertype
   */
  public static boolean isSubtypeOfType(TypeMirror typeMirror, String otherType) {
    if (isTypeEqual(typeMirror, otherType)) {
      return true;
    }

    if (typeMirror.getKind() != TypeKind.DECLARED) {
      return false;
    }
    DeclaredType declaredType = (DeclaredType) typeMirror;
    List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
    if (typeArguments.size() > 0) {
      StringBuilder typeString = new StringBuilder(declaredType.asElement().toString());
      typeString.append('<');
      for (int i = 0; i < typeArguments.size(); i++) {
        if (i > 0) {
          typeString.append(',');
        }
        typeString.append('?');
      }
      typeString.append('>');
      if (typeString.toString().equals(otherType)) {
        return true;
      }
    }
    Element element = declaredType.asElement();
    if (!(element instanceof TypeElement)) {
      return false;
    }
    TypeElement typeElement = (TypeElement) element;
    TypeMirror superType = typeElement.getSuperclass();
    if (isSubtypeOfType(superType, otherType)) {
      return true;
    }
    for (TypeMirror interfaceType : typeElement.getInterfaces()) {
      if (isSubtypeOfType(interfaceType, otherType)) {
        return true;
      }
    }
    return false;
  }

  /**
   * 判断类型是否相等
   */
  private static boolean isTypeEqual(TypeMirror typeMirror, String otherType) {
    return otherType.equals(typeMirror.toString());
  }

  /**
   * typeElement是否在parents的集合类型里
   * @param typeElement typeelement
   * @param parents parents
   * @return typeelement
   * */
  public static TypeElement findParentType(TypeElement typeElement, Set<TypeElement> parents) {
    TypeMirror type;
    while (true) {
      type = typeElement.getSuperclass();
      if (type.getKind() == TypeKind.NONE) {
        return null;
      }
      typeElement = (TypeElement) ((DeclaredType) type).asElement();
      if (parents.contains(typeElement)) {
        return typeElement;
      }
    }
  }
}
