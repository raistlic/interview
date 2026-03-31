package org.raistlic.interview;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

class AppTest {
    private static final Path SNAPSHOT_DIRECTORY = Path.of("src", "main", "resources", "snapshot");

    @Test
    void mainGeneratesFiveSnapshotImagesAndMetadata() throws Exception {
        App.main(new String[0]);

        try (Stream<Path> files = Files.list(SNAPSHOT_DIRECTORY)) {
            assertEquals(10, files.count());
        }
        for (int index = 1; index <= 5; index++) {
            assertTrue(Files.exists(SNAPSHOT_DIRECTORY.resolve("snapshot-" + index + ".png")));
            Path metadataFile = SNAPSHOT_DIRECTORY.resolve("snapshot-" + index + ".json");
            assertTrue(Files.exists(metadataFile));
            String metadata = Files.readString(metadataFile);
            assertTrue(metadata.contains("\"dimension\""));
            assertTrue(metadata.contains("\"padding\""));
            assertTrue(metadata.contains("\"foregroundColor\""));
            assertTrue(metadata.contains("\"backgroundColor\""));
        }
    }
}
