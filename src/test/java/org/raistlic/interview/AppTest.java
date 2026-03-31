package org.raistlic.interview;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class AppTest {
    private static final String SNAPSHOT_DIRECTORY_PROPERTY = "interview.snapshot.dir";

    @Test
    void mainGeneratesFiveSnapshotImagesAndMetadata(@TempDir Path tempDir) throws Exception {
        System.setProperty(SNAPSHOT_DIRECTORY_PROPERTY, tempDir.toString());

        try {
            App.main(new String[0]);

            try (Stream<Path> files = Files.list(tempDir)) {
                assertEquals(10, files.count());
            }
            for (int index = 1; index <= 5; index++) {
                assertTrue(Files.exists(tempDir.resolve("snapshot-" + index + ".png")));
                Path metadataFile = tempDir.resolve("snapshot-" + index + ".json");
                assertTrue(Files.exists(metadataFile));
                String metadata = Files.readString(metadataFile);
                assertTrue(metadata.contains("\"dimension\""));
                assertTrue(metadata.contains("\"padding\""));
                assertTrue(metadata.contains("\"foregroundColor\""));
                assertTrue(metadata.contains("\"backgroundColor\""));
            }
        } finally {
            System.clearProperty(SNAPSHOT_DIRECTORY_PROPERTY);
        }
    }
}
