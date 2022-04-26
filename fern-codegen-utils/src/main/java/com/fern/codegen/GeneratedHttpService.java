package com.fern.codegen;

import com.fern.immutables.StagedBuilderStyle;
import com.services.http.HttpService;
import org.immutables.value.Value;

@Value.Immutable
@StagedBuilderStyle
public interface GeneratedHttpService extends GeneratedFile {

    HttpService httpService();

    static ImmutableGeneratedHttpService.FileBuildStage builder() {
        return ImmutableGeneratedHttpService.builder();
    }
}

/**
 * PREFIX = com.fern
 * MODEL_FOLDER = ir-java-client
 * build.gradle ----> generated
 * src/
 *   main/
 *     java/
 *       com/
 *         fern/
 *           interfaces/
 *             <filepath-as-package>
 *               <CapitalizedCamelCaseTypeName>.java
 *           types/
 *             <filepath-as-package>
 *               <CapitalizedCamelCaseTypeName>.java
 *           errors/
 *             <filepath-as-package>
 *               <CapitalizedCamelCaseErrorName>.java
 *           services/
 *             rest/
 *               <filepath-as-package>
 *                 <CapitalizedCamelCaseServiceName>.java
 *                 <CapitalizedCamelCaseServiceName + Request>.java
 *                 <CapitalizedCamelCaseServiceName + Response>.java
 **/
