package com.fern.model.codegen;

import com.StagedBuilderStyle;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableNotFoundError.class)
@StagedBuilderStyle
public abstract class NotFoundError extends WebApplicationException {
    public abstract String a();

    @Override
    public Response getResponse() {
        return Response.status(500).entity(this).build();
    }

    public static ImmutableNotFoundError.ABuildStage builder() {
        return NotFoundError.builder();
    }
}
