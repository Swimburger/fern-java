package com.fern.codegen.utils;

import com.fern.FernFilepath;
import com.fern.NamedType;
import com.fern.TypeReference;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;

public final class ClassNameUtils {

    public static final ClassName STRING_CLASS_NAME = ClassName.get(String.class);
    public static final ClassName OPTIONAL_CLASS_NAME = ClassName.get(Optional.class);

    private final List<String> packagePrefixes;

    public ClassNameUtils(Optional<String> maybePackagePrefix) {
        String[] splitPackagePrefix = maybePackagePrefix
                .map(packagePrefix -> packagePrefix.split("/"))
                .orElseGet(() -> new String[0]);
        this.packagePrefixes = Arrays.asList(splitPackagePrefix);
    }

    public ClassName getClassNameForNamedType(NamedType namedType) {
        return ClassName.get(getPackageFromFilepath(namedType.fernFilepath()), namedType.name());
    }

    public TypeName getTypeNameFromTypeReference(boolean primitiveAllowed, TypeReference typeReference) {
        return typeReference.accept(new TypeReferenceToTypeNameConverter(primitiveAllowed, this));
    }

    public static String getKeywordCompatibleClassName(String name) {
        return KeyWordUtils.getKeyWordCompatibleName(StringUtils.capitalize(name));
    }

    public String getPackageFromFilepath(FernFilepath filepath) {
        List<String> splitFilepath = Arrays.asList(filepath.value().split("/"));
        List<String> packagePath = new ArrayList<>(packagePrefixes);
        packagePath.addAll(splitFilepath);
        return String.join(".", packagePath);
    }
}
