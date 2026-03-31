package org.raistlic.interview;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

class AppTest {
    private static final Path SNAPSHOT_DIRECTORY = Path.of("src", "main", "resources", "snapshot");

    @Test
    void mainGeneratesFiveSnapshotImages() throws Exception {
        deleteSnapshotDirectory();

        App.main(new String[0]);

        try (Stream<Path> files = Files.list(SNAPSHOT_DIRECTORY)) {
            assertEquals(5, files.count());
        }
        for (int index = 1; index <= 5; index++) {
            assertTrue(Files.exists(SNAPSHOT_DIRECTORY.resolve("snapshot-" + index + ".png")));
        }

        deleteSnapshotDirectory();
    }

    private static void deleteSnapshotDirectory() throws IOException {
        if (!Files.exists(SNAPSHOT_DIRECTORY)) {
            return;
        }
        try (Stream<Path> paths = Files.walk(SNAPSHOT_DIRECTORY)) {
            paths.sorted(Comparator.reverseOrder()).forEach(path -> {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException exception) {
                    throw new RuntimeException(exception);
                }
            });
        }
    }
}
