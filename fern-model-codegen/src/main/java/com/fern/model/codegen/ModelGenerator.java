package com.fern.model.codegen;

import com.fern.AliasTypeDefinition;
import com.fern.EnumTypeDefinition;
import com.fern.NamedTypeReference;
import com.fern.ObjectTypeDefinition;
import com.fern.Type;
import com.fern.TypeDefinition;
import com.fern.UnionTypeDefinition;
import com.fern.model.codegen.alias.AliasGenerator;
import com.fern.model.codegen.config.PluginConfig;
import com.fern.model.codegen.enums.EnumGenerator;
import com.fern.model.codegen.interfaces.GeneratedInterface;
import com.fern.model.codegen.interfaces.InterfaceGenerator;
import com.fern.model.codegen.object.ObjectGenerator;
import com.fern.model.codegen.union.UnionGenerator;
import com.squareup.javapoet.JavaFile;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class ModelGenerator {

    private final List<TypeDefinition> typeDefinitions;
    private final Map<NamedTypeReference, TypeDefinition> typeDefinitionsByName;
    private final PluginConfig pluginConfig;

    public ModelGenerator(List<TypeDefinition> typeDefinitions, PluginConfig pluginConfig) {
        this.typeDefinitions = typeDefinitions;
        this.typeDefinitionsByName = typeDefinitions.stream()
                .collect(Collectors.toUnmodifiableMap(TypeDefinition::name, Function.identity()));
        this.pluginConfig = pluginConfig;
    }

    public void buildModelSubproject() {
        List<JavaFile> javaFiles = generateJavaFiles();
        javaFiles.forEach(javaFile -> {
            try {
                javaFile.writeToFile(new File(pluginConfig.outputDirectoryName()));
            } catch (IOException e) {
                throw new RuntimeException("Failed to write generated java file: " + javaFile.typeSpec.name, e);
            }
        });
    }

    private List<JavaFile> generateJavaFiles() {
        Map<NamedTypeReference, TypeDefinition> typeDefinitionsByName = typeDefinitions.stream()
                .collect(Collectors.toUnmodifiableMap(TypeDefinition::name, Function.identity()));
        Map<NamedTypeReference, GeneratedInterface> generatedInterfaces = getGeneratedInterfaces();
        List<GeneratedFile<?>> generatedFiles = typeDefinitions.stream()
                .map(typeDefinition -> typeDefinition
                        .shape()
                        .accept(new TypeDefinitionGenerator(
                                typeDefinition, generatedInterfaces, typeDefinitionsByName)))
                .collect(Collectors.toList());
        return generatedFiles.stream().map(GeneratedFile::file).collect(Collectors.toList());
    }

    private Map<NamedTypeReference, GeneratedInterface> getGeneratedInterfaces() {
        Set<NamedTypeReference> interfaceCandidates = typeDefinitions.stream()
                .map(TypeDefinition::_extends)
                .flatMap(List::stream)
                .collect(Collectors.toSet());
        return interfaceCandidates.stream().collect(Collectors.toMap(Function.identity(), namedTypeReference -> {
            TypeDefinition typeDefinition = typeDefinitionsByName.get(namedTypeReference);
            ObjectTypeDefinition objectTypeDefinition = typeDefinition
                    .shape()
                    .getObject()
                    .orElseThrow(() -> new IllegalStateException("Non-objects cannot be extended. Fix type "
                            + typeDefinition.name().name() + " located in file"
                            + typeDefinition.name().filepath()));
            InterfaceGenerator interfaceGenerator = new InterfaceGenerator(objectTypeDefinition, namedTypeReference);
            return interfaceGenerator.generate();
        }));
    }

    private static final class TypeDefinitionGenerator implements Type.Visitor<GeneratedFile<?>> {

        private final TypeDefinition typeDefinition;
        private final Map<NamedTypeReference, GeneratedInterface> generatedInterfaces;
        private final Map<NamedTypeReference, TypeDefinition> typeDefinitionsByName;

        TypeDefinitionGenerator(
                TypeDefinition typeDefinition,
                Map<NamedTypeReference, GeneratedInterface> generatedInterfaces,
                Map<NamedTypeReference, TypeDefinition> typeDefinitionsByName) {
            this.typeDefinition = typeDefinition;
            this.generatedInterfaces = generatedInterfaces;
            this.typeDefinitionsByName = typeDefinitionsByName;
        }

        @Override
        public GeneratedFile<?> visitObject(ObjectTypeDefinition objectTypeDefinition) {
            Optional<GeneratedInterface> selfInterface =
                    Optional.ofNullable(generatedInterfaces.get(typeDefinition.name()));
            ObjectGenerator objectGenerator = new ObjectGenerator(
                    typeDefinition.name(),
                    objectTypeDefinition,
                    new ArrayList<>(generatedInterfaces.values()),
                    selfInterface);
            return objectGenerator.generate();
        }

        @Override
        public GeneratedFile<?> visitUnion(UnionTypeDefinition unionTypeDefinition) {
            UnionGenerator unionGenerator =
                    new UnionGenerator(typeDefinition.name(), unionTypeDefinition, typeDefinitionsByName);
            return unionGenerator.generate();
        }

        @Override
        public GeneratedFile<?> visitAlias(AliasTypeDefinition aliasTypeDefinition) {
            AliasGenerator aliasGenerator = new AliasGenerator(aliasTypeDefinition, typeDefinition.name());
            return aliasGenerator.generate();
        }

        @Override
        public GeneratedFile<?> visitEnum(EnumTypeDefinition enumTypeDefinition) {
            EnumGenerator enumGenerator = new EnumGenerator(typeDefinition.name(), enumTypeDefinition);
            return enumGenerator.generate();
        }

        @Override
        public GeneratedFile<?> visitUnknown(String unknownType) {
            throw new RuntimeException("Encountered unknown Type: " + unknownType);
        }
    }
}
