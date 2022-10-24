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

package com.fern.codegen.utils;

import com.fern.ir.model.auth.AuthScheme;
import com.fern.ir.model.commons.WithDocs;
import com.fern.ir.model.services.http.HttpHeader;
import org.apache.commons.lang3.StringUtils;

public final class AuthSchemeUtils {

    private AuthSchemeUtils() {}

    public static String getAuthSchemeCamelCaseName(AuthScheme authScheme) {
        return authScheme.visit(AuthSchemeCamelCaseName.INSTANCE);
    }

    public static String getAuthSchemePascalCaseName(AuthScheme authScheme) {
        return StringUtils.capitalize(authScheme.visit(AuthSchemeCamelCaseName.INSTANCE));
    }

    private static final class AuthSchemeCamelCaseName implements AuthScheme.Visitor<String> {
        private static final AuthSchemeCamelCaseName INSTANCE = new AuthSchemeCamelCaseName();

        @Override
        public String visitBearer(WithDocs value) {
            return "bearer";
        }

        @Override
        public String visitBasic(WithDocs value) {
            return "basic";
        }

        @Override
        public String visitHeader(HttpHeader value) {
            return value.getName().getCamelCase();
        }

        @Override
        public String _visitUnknown(Object unknown) {
            throw new RuntimeException("Encountered unknown authScheme: " + unknown);
        }
    }
}
