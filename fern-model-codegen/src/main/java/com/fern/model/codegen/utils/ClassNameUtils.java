package com.fern.model.codegen.utils;

import com.fern.NamedTypeReference;
import com.squareup.javapoet.ClassName;

public class ClassNameUtils {

    public static final ClassName STRING_TYPE_NAME = ClassName.get(String.class);

    private ClassNameUtils() {}

    public static ClassName getClassName(NamedTypeReference namedTypeReference) {
        return ClassName.get(
                FilepathUtils.convertFilepathToPackage(namedTypeReference.filepath()), namedTypeReference.name());
    }
}
