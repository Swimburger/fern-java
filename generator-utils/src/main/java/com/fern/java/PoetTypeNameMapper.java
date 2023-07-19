/*
 * (c) Copyright 2022 Birch Solutions Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fern.java;

import com.fasterxml.jackson.databind.JsonNode;
import com.fern.irV16.model.commons.TypeId;
import com.fern.irV16.model.types.AliasTypeDeclaration;
import com.fern.irV16.model.types.ContainerType;
import com.fern.irV16.model.types.DeclaredTypeName;
import com.fern.irV16.model.types.Literal;
import com.fern.irV16.model.types.MapType;
import com.fern.irV16.model.types.PrimitiveType;
import com.fern.irV16.model.types.ResolvedNamedType;
import com.fern.irV16.model.types.ResolvedTypeReference;
import com.fern.irV16.model.types.TypeDeclaration;
import com.fern.irV16.model.types.TypeReference;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public final class PoetTypeNameMapper {

    private final AbstractPoetClassNameFactory poetClassNameFactory;
    private final TypeReferenceToTypeNameConverter primitiveDisAllowedTypeReferenceConverter =
            new TypeReferenceToTypeNameConverter(false);
    private final ContainerToTypeNameConverter containerToTypeNameConverter = new ContainerToTypeNameConverter();
    private final ICustomConfig customConfig;
    private final Map<TypeId, TypeDeclaration> typeDefinitionsByName;

    public PoetTypeNameMapper(
            AbstractPoetClassNameFactory poetClassNameFactory,
            ICustomConfig customConfig,
            Map<TypeId, TypeDeclaration> typeDefinitionsByName) {
        this.poetClassNameFactory = poetClassNameFactory;
        this.customConfig = customConfig;
        this.typeDefinitionsByName = typeDefinitionsByName;
    }

    public TypeName convertToTypeName(boolean primitiveAllowed, TypeReference typeReference) {
        return typeReference.visit(new TypeReferenceToTypeNameConverter(primitiveAllowed));
    }

    private final class TypeReferenceToTypeNameConverter
            implements TypeReference.Visitor<TypeName>, ResolvedTypeReference.Visitor<TypeName> {

        private final boolean primitiveAllowed;

        private TypeReferenceToTypeNameConverter(boolean primitiveAllowed) {
            this.primitiveAllowed = primitiveAllowed;
        }

        @Override
        public TypeName visitNamed(DeclaredTypeName declaredTypeName) {
            if (!customConfig.wrappedAliases()) {
                TypeDeclaration typeDeclaration = typeDefinitionsByName.get(declaredTypeName.getTypeId());
                boolean isAlias = typeDeclaration.getShape().isAlias();
                if (isAlias) {
                    AliasTypeDeclaration aliasTypeDeclaration =
                            typeDeclaration.getShape().getAlias().get();
                    return aliasTypeDeclaration.getResolvedType().visit(this);
                }
            }
            return poetClassNameFactory.getTypeClassName(declaredTypeName);
        }

        @Override
        public TypeName visitNamed(ResolvedNamedType named) {
            return poetClassNameFactory.getTypeClassName(named.getName());
        }

        @Override
        public TypeName visitPrimitive(PrimitiveType primitiveType) {
            if (primitiveAllowed) {
                return primitiveType.visit(PrimitiveToTypeNameConverter.PRIMITIVE_ALLOWED_CONVERTER);
            }
            return primitiveType.visit(PrimitiveToTypeNameConverter.PRIMITIVE_DISALLOWED_CONVERTER);
        }

        @Override
        public TypeName visitContainer(ContainerType containerType) {
            return containerType.visit(containerToTypeNameConverter);
        }

        @Override
        public TypeName visitUnknown() {
            if (customConfig.generateUnknownAsJsonNode()) {
                return ClassName.get(JsonNode.class);
            }
            return ClassName.get(Object.class);
        }

        @Override
        public TypeName _visitUnknown(Object unknown) {
            throw new RuntimeException("Encountered unknown type reference: " + unknown);
        }
    }

    private static final class PrimitiveToTypeNameConverter implements PrimitiveType.Visitor<TypeName> {

        private static final PrimitiveToTypeNameConverter PRIMITIVE_ALLOWED_CONVERTER =
                new PrimitiveToTypeNameConverter(true);
        private static final PrimitiveToTypeNameConverter PRIMITIVE_DISALLOWED_CONVERTER =
                new PrimitiveToTypeNameConverter(false);

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
        public TypeName visitDateTime() {
            return ClassName.get(String.class);
        }

        @Override
        public TypeName visitUuid() {
            return ClassName.get(UUID.class);
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

    private final class ContainerToTypeNameConverter implements ContainerType.Visitor<TypeName> {

        @Override
        public TypeName visitMap(MapType mapType) {
            return ParameterizedTypeName.get(
                    ClassName.get(Map.class),
                    mapType.getKeyType().visit(primitiveDisAllowedTypeReferenceConverter),
                    mapType.getValueType().visit(primitiveDisAllowedTypeReferenceConverter));
        }

        @Override
        public TypeName visitList(TypeReference typeReference) {
            return ParameterizedTypeName.get(
                    ClassName.get(List.class), typeReference.visit(primitiveDisAllowedTypeReferenceConverter));
        }

        @Override
        public TypeName visitSet(TypeReference typeReference) {
            return ParameterizedTypeName.get(
                    ClassName.get(Set.class), typeReference.visit(primitiveDisAllowedTypeReferenceConverter));
        }

        @Override
        public TypeName visitLiteral(Literal literal) {
            return literal.visit(new Literal.Visitor<>() {
                @Override
                public TypeName visitString(String string) {
                    return ClassName.get(String.class);
                }

                @Override
                public TypeName _visitUnknown(Object unknownType) {
                    throw new RuntimeException("Unsupported literal type: " + unknownType);
                }
            });
        }

        @Override
        public TypeName visitOptional(TypeReference typeReference) {
            TypeName typeName = typeReference.visit(primitiveDisAllowedTypeReferenceConverter);
            if (typeName instanceof ParameterizedTypeName) {
                // Optional should not be re-wrapped in Optional
                if (((ParameterizedTypeName) typeName).rawType.equals(ClassName.get(Optional.class))) {
                    return typeName;
                }
            }
            return ParameterizedTypeName.get(ClassName.get(Optional.class), typeName);
        }

        @Override
        public TypeName _visitUnknown(Object unknown) {
            throw new RuntimeException("Encountered unknown container type: " + unknown);
        }
    }
}
