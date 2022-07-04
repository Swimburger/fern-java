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

package com.fern.java.jersey;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fern.java.immutables.StagedBuilderImmutablesStyle;
import java.util.UUID;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import org.immutables.value.Value;

public final class DefaultExceptionMapper implements ExceptionMapper<Exception> {

    private static final String a = "bla";

    @Override
    public Response toResponse(Exception exception) {
        return Response.status(500).entity(DefaultExceptionBody.create()).build();
    }

    @Value.Immutable
    @StagedBuilderImmutablesStyle
    interface DefaultExceptionBody {

        @JsonProperty(a)
        default String type() {
            return "unknown";
        }

        default String errorInstanceId() {
            return UUID.randomUUID().toString();
        }

        static DefaultExceptionBody create() {
            return ImmutableDefaultExceptionBody.builder().build();
        }
    }
}
