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

package com.fern.codegen.generator;

import com.fern.codegen.GeneratedFile;
import com.fern.codegen.GeneratorContext;
import com.fern.codegen.utils.ClassNameUtils.PackageType;
import com.fern.types.ApiAuth;
import com.fern.types.AuthScheme;
import com.fern.types.AuthSchemesRequirement;
import java.util.Optional;

public final class ClientAuthGenerator {

    private final ApiAuth apiAuth;
    private final GeneratorContext generatorContext;

    public ClientAuthGenerator(ApiAuth apiAuth, GeneratorContext generatorContext) {
        this.apiAuth = apiAuth;
        this.generatorContext = generatorContext;
    }

    public Optional<GeneratedFile> generate() {
        if (apiAuth.schemes().size() == 0) {
            return Optional.empty();
        } else if (apiAuth.schemes().size() == 1) {
            AuthScheme authScheme = apiAuth.schemes().get(0);
            authScheme.visit(new AuthSchemeToGeneratedFile(generatorContext, PackageType.CLIENT));
        } else if (apiAuth.requirement().equals(AuthSchemesRequirement.ANY)) {

        } else if (apiAuth.requirement().equals(AuthSchemesRequirement.ALL)) {

        }
        return Optional.empty();
    }
}
