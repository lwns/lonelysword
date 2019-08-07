package com.timper.lonelysword.compiler.usecase;

import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.timper.lonelysword.compiler.TypeBinding;

/**
 * User: tangpeng.yang
 * Date: 29/05/2018
 * Description:
 * FIXME
 */
public class UseCaseBinding implements TypeBinding {

    private final String name;
    private final TypeName returnClass;
    private final TypeName parameter;


    private TypeName ignoreType;
    private TypeName transformerType;

    public UseCaseBinding(TypeName returnClass, String name, TypeName parameter, TypeName ignoreType, TypeName transformerType) {
        this.returnClass = returnClass;
        this.name = name;
        this.parameter = parameter;
        this.ignoreType = ignoreType;
        this.transformerType =transformerType;
    }

    @Override
    public TypeSpec render() {
        return null;
    }

    public String getName() {
        return name;
    }

    public TypeName getParameter() {
        return parameter;
    }

    public TypeName getReturnClass() {
        return returnClass;
    }

    public TypeName getIgnoreType() {
        return ignoreType;
    }

    public TypeName getTransformerType() {
        return transformerType;
    }

    static final class Builder {
        private final String name;
        private TypeName returnClass;
        private TypeName parameter;

        private TypeName ignoreType;
        private TypeName transformerType;

        Builder(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        void addReturnClass(TypeName returnClass) {
            this.returnClass = returnClass;
        }

        void addIgnoreType(TypeName ignoreType) {
            this.ignoreType = ignoreType;
        }

        void addTransformerType(TypeName transformerType) {
            this.transformerType = transformerType;
        }

        void addParameter(TypeName parameter) {
            this.parameter = parameter;
        }

        UseCaseBinding build() {
            return new UseCaseBinding(returnClass, name, parameter, ignoreType, transformerType);
        }
    }
}