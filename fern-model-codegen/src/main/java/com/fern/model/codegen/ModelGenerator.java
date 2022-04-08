package com.fern.model.codegen;

import com.fern.*;
import com.squareup.javapoet.JavaFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class ModelGenerator {

    public static List<JavaFile> generate(List<TypeDefinition> typeDefinitions) {
        Map<NamedTypeReference, TypeDefinition> typeDefinitionsByName = typeDefinitions.stream()
                .collect(Collectors.toUnmodifiableMap(TypeDefinition::name, Function.identity()));
        Map<NamedTypeReference, GeneratedInterface> generatedInterfaces = typeDefinitions.stream()
                .map(TypeDefinition::_extends)
                .flatMap(List::stream)
                .map(typeDefinitionsByName::get)
                .collect(Collectors.toMap(typeDefinition -> typeDefinition.name(), InterfaceGenerator::generate));
        List<GeneratedObject> generatedObjects = typeDefinitions.stream()
                .map(typeDefinition -> typeDefinition.shape()
                        .accept(new TypeDefinitionGenerator(typeDefinition, generatedInterfaces)))
                .collect(Collectors.toList());
        return Collections.emptyList();
    }

    private final static class TypeDefinitionGenerator implements Type.Visitor<GeneratedObject> {

        private final TypeDefinition typeDefinition;
        private final Map<NamedTypeReference, GeneratedInterface> generatedInterfaces;

        public TypeDefinitionGenerator(
                TypeDefinition typeDefinition,
                Map<NamedTypeReference, GeneratedInterface> generatedInterfaces) {
            this.typeDefinition = typeDefinition;
            this.generatedInterfaces = generatedInterfaces;
        }

        @Override
        public GeneratedObject visitObject(ObjectTypeDefinition objectTypeDefinition) {
            List<GeneratedInterface> superInterfaces = typeDefinition._extends().stream()
                    .map(generatedInterfaces::get)
                    .collect(Collectors.toList());
            if (generatedInterfaces.containsKey(typeDefinition.name())) {
                // Add own interface to super interfaces and ignoreOwnFields when generating object
                List<GeneratedInterface> superInterfacesWithOwnInterface = new ArrayList<>(superInterfaces);
                superInterfacesWithOwnInterface.add(generatedInterfaces.get(typeDefinition.name()));
                return ObjectGenerator.generate(
                        superInterfacesWithOwnInterface, typeDefinition.name(), objectTypeDefinition, true);
            } else {
                return ObjectGenerator.generate(
                        superInterfaces, typeDefinition.name(), objectTypeDefinition, false);
            }
        }

        @Override
        public GeneratedObject visitUnion(UnionTypeDefinition unionTypeDefinition) {
            return null;
        }

        @Override
        public GeneratedObject visitAlias(AliasTypeDefinition aliasTypeDefinition) {
            return null;
        }

        @Override
        public GeneratedObject visitEnum(EnumTypeDefinition enumTypeDefinition) {
            return null;
        }

        @Override
        public GeneratedObject visitUnknown(String s) {
            return null;
        }
    }
}
