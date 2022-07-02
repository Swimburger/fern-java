package com.fern.java.client.cli;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import au.com.origin.snapshots.junit5.SnapshotExtension;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ExtendWith(SnapshotExtension.class)
public class CliEteTest {

    private static final Logger log = LoggerFactory.getLogger(CliEteTest.class);

    private Expect expect;

    @SuppressWarnings("StreamResourceLeak")
    @SnapshotName("basic")
    @Test
    public void test_basic() throws IOException {
        Path currentPath = Paths.get("").toAbsolutePath();
        Path basicFernProjectPath = currentPath.endsWith("cli")
                ? currentPath.resolve(Paths.get("src/eteTest/basic"))
                : currentPath.resolve(Paths.get("cli/src/eteTest/basic"));
        fernLocalGenerate(basicFernProjectPath);
        List<Path> paths = Files.walk(basicFernProjectPath.resolve(Paths.get("api/generated-java")))
                .collect(Collectors.toList());
        boolean filesGenerated = false;
        for (Path path : paths) {
            if (path.toFile().isDirectory()) {
                continue;
            }
            try {
                filesGenerated = true;
                if (path.getFileName().toString().endsWith("jar")) {
                    expect.scenario(path.toString()).toMatchSnapshot(path.toString());
                } else {
                    String fileContents = Files.readString(path);
                    expect.scenario(path.toString()).toMatchSnapshot(fileContents);
                }
            } catch (IOException e) {
                throw new RuntimeException("Failed to read file: " + path);
            }
        }
        if (!filesGenerated) {
            throw new RuntimeException("Failed to generate any files!");
        }
    }

    private static void fernLocalGenerate(Path projectPath) {
        int exitCode;
        try {
            ProcessBuilder pb = new ProcessBuilder("fern", "generate", "--local")
                    .directory(projectPath.toFile());

            Map<String, String> env = pb.environment();
            env.put("NODE_ENV", "development");

            Process process = pb.start();
            StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream());
            StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream());
            errorGobbler.start();
            outputGobbler.start();
            exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("Failed to run fern generate!");
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to run fern generate!", e);
        }
    }
}
