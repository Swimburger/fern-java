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
package com.fern.model.codegen;

import com.fern.codegen.GeneratedInterface;
import com.fern.codegen.GeneratorContext;
import com.fern.codegen.IGeneratedFile;
import com.fern.ir.model.types.AliasTypeDeclaration;
import com.fern.ir.model.types.DeclaredTypeName;
import com.fern.ir.model.types.EnumTypeDeclaration;
import com.fern.ir.model.types.ObjectTypeDeclaration;
import com.fern.ir.model.types.Type;
import com.fern.ir.model.types.TypeDeclaration;
import com.fern.ir.model.types.UnionTypeDeclaration;
import com.fern.model.codegen.types.AliasGenerator;
import com.fern.model.codegen.types.EnumGenerator;
import com.fern.model.codegen.types.ObjectGenerator;
import com.fern.model.codegen.types.UnionGenerator;
import com.squareup.javapoet.ClassName;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public final class TypeDefinitionGenerator implements Type.Visitor<IGeneratedFile> {

    private final TypeDeclaration typeDeclaration;
    private final GeneratorContext generatorContext;
    private final Map<DeclaredTypeName, GeneratedInterface> generatedInterfaces;
    private final ClassName generatedClassName;

    public TypeDefinitionGenerator(
            TypeDeclaration typeDeclaration,
            GeneratorContext generatorContext,
            Map<DeclaredTypeName, GeneratedInterface> generatedInterfaces,
            ClassName generatedClassName) {
        this.typeDeclaration = typeDeclaration;
        this.generatorContext = generatorContext;
        this.generatedInterfaces = generatedInterfaces;
        this.generatedClassName = generatedClassName;
    }

    @Override
    public IGeneratedFile visitObject(ObjectTypeDeclaration objectTypeDefinition) {
        Optional<GeneratedInterface> selfInterface =
                Optional.ofNullable(generatedInterfaces.get(typeDeclaration.getName()));
        List<GeneratedInterface> extendedInterfaces = objectTypeDefinition.getExtends().stream()
                .map(generatedInterfaces::get)
                .sorted(Comparator.comparing(
                        generatedInterface -> generatedInterface.className().simpleName()))
                .collect(Collectors.toList());
        ObjectGenerator objectGenerator = new ObjectGenerator(
                typeDeclaration.getName(),
                objectTypeDefinition,
                extendedInterfaces,
                selfInterface,
                generatorContext,
                generatedClassName);
        return objectGenerator.generate();
    }

    @Override
    public IGeneratedFile visitUnion(UnionTypeDeclaration unionTypeDeclaration) {
        UnionGenerator unionGenerator = new UnionGenerator(
                typeDeclaration.getName(), unionTypeDeclaration, generatedClassName, generatorContext);
        return unionGenerator.generate();
    }

    @Override
    public IGeneratedFile visitAlias(AliasTypeDeclaration aliasTypeDeclaration) {
        AliasGenerator aliasGenerator = new AliasGenerator(aliasTypeDeclaration, generatorContext, generatedClassName);
        return aliasGenerator.generate();
    }

    @Override
    public IGeneratedFile visitEnum(EnumTypeDeclaration enumTypeDeclaration) {
        EnumGenerator enumGenerator =
                new EnumGenerator(typeDeclaration.getName(), enumTypeDeclaration, generatorContext, generatedClassName);
        return enumGenerator.generate();
    }

    @Override
    public IGeneratedFile _visitUnknown(Object unknownType) {
        throw new RuntimeException("Encountered unknown Type: " + unknownType);
    }
}
