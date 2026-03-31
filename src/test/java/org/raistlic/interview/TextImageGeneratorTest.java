package org.raistlic.interview;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class TextImageGeneratorTest {
    @Test
    void generatesImageFileInProjectRoot() throws Exception {
        Path outputFile = Path.of("generated-text.png");
        Files.deleteIfExists(outputFile);

        new TextImageGenerator().generate("hello");

        assertTrue(Files.exists(outputFile));

        Files.deleteIfExists(outputFile);
    }
}
