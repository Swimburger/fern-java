package com.fern.codegen;

import com.fern.immutables.StagedBuilderStyle;
import com.fern.types.services.http.HttpService;
import java.util.List;
import org.immutables.value.Value;

@Value.Immutable
@StagedBuilderStyle
public interface GeneratedHttpServiceServer extends IGeneratedFile {

    HttpService httpService();

    static ImmutableGeneratedHttpServiceServer.FileBuildStage builder() {
        return ImmutableGeneratedHttpServiceServer.builder();
    }
}
