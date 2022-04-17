package com.fern.codegen.utils;

import com.fern.ContainerType;
import com.fern.MapType;
import com.fern.NamedType;
import com.fern.PrimitiveType;
import com.fern.TypeReference;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

final class TypeReferenceToTypeNameConverter implements TypeReference.Visitor<TypeName> {

    private final boolean isPrimitiveAllowed;
    private final ClassNameUtils classNameUtils;
    private final ContainerToTypeNameConverter containerToTypeNameConverter =
            new ContainerToTypeNameConverter();

    TypeReferenceToTypeNameConverter(boolean isPrimitiveAllowed, ClassNameUtils classNameUtils) {
        this.isPrimitiveAllowed = isPrimitiveAllowed;
        this.classNameUtils = classNameUtils;
    }

    @Override
    public TypeName visitNamed(NamedType namedType) {
        return classNameUtils.getClassNameForNamedType(namedType);
    }

    @Override
    public TypeName visitPrimitive(PrimitiveType primitiveType) {
        if (isPrimitiveAllowed) {
            return primitiveType.accept(PrimitiveToTypeNameConverter.PRIMITIVE_ALLOWED_INSTANCE);
        }
        return primitiveType.accept(PrimitiveToTypeNameConverter.PRIMITIVE_DISALLOWED_INSTANCE);
    }

    @Override
    public TypeName visitContainer(ContainerType containerType) {
        return containerType.accept(containerToTypeNameConverter);
    }

    @Override
    public TypeName visitVoid() {
        throw new RuntimeException("Void types should be handled separately!");
    }

    @Override
    public TypeName visitUnknown(String unknownType) {
        throw new RuntimeException("Encountered unknown type reference: " + unknownType);
    }

    private final class ContainerToTypeNameConverter implements ContainerType.Visitor<TypeName> {

        private final TypeReferenceToTypeNameConverter primitiveAllowedTypeReferenceConverter =
                new TypeReferenceToTypeNameConverter(true, classNameUtils);

        @Override
        public TypeName visitMap(MapType mapType) {
            return ParameterizedTypeName.get(
                    ClassName.get(Map.class),
                    mapType.keyType().accept(primitiveAllowedTypeReferenceConverter),
                    mapType.valueType().accept(primitiveAllowedTypeReferenceConverter));
        }

        @Override
        public TypeName visitList(TypeReference typeReference) {
            return ParameterizedTypeName.get(
                    ClassName.get(List.class), typeReference.accept(primitiveAllowedTypeReferenceConverter));
        }

        @Override
        public TypeName visitSet(TypeReference typeReference) {
            return ParameterizedTypeName.get(
                    ClassName.get(Set.class), typeReference.accept(primitiveAllowedTypeReferenceConverter));
        }

        @Override
        public TypeName visitOptional(TypeReference typeReference) {
            return ParameterizedTypeName.get(
                    ClassName.get(Optional.class), typeReference.accept(primitiveAllowedTypeReferenceConverter));
        }

        @Override
        public TypeName visitUnknown(String unknownType) {
            throw new RuntimeException("Encountered unknown container type: " + unknownType);
        }
    }

    private static final class PrimitiveToTypeNameConverter implements PrimitiveType.Visitor<TypeName> {

        private static final PrimitiveToTypeNameConverter PRIMITIVE_ALLOWED_INSTANCE =
                new PrimitiveToTypeNameConverter(false);
        private static final PrimitiveToTypeNameConverter PRIMITIVE_DISALLOWED_INSTANCE =
                new PrimitiveToTypeNameConverter(true);

        private final boolean primitiveAllowed;

        private PrimitiveToTypeNameConverter(boolean primitiveAllowed) {
            this.primitiveAllowed = primitiveAllowed;
        }

        @Override
        public TypeName visitInteger() {
            if (primitiveAllowed) {
                return TypeName.INT;
            }
            return ClassName.get(Integer.class);
        }

        @Override
        public TypeName visitDouble() {
            if (primitiveAllowed) {
                return TypeName.DOUBLE;
            }
            return ClassName.get(Double.class);
        }

        @Override
        public TypeName visitLong() {
            if (primitiveAllowed) {
                return TypeName.LONG;
            }
            return ClassName.get(Long.class);
        }

        @Override
        public TypeName visitString() {
            return ClassName.get(String.class);
        }

        @Override
        public TypeName visitBoolean() {
            if (primitiveAllowed) {
                return TypeName.BOOLEAN;
            }
            return ClassName.get(Boolean.class);
        }

        @Override
        public TypeName visitUnknown(String unknownType) {
            throw new RuntimeException("Encountered unknown primitive type: " + unknownType);
        }
    }
}
