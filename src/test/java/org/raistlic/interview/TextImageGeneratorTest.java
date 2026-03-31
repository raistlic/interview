package org.raistlic.interview;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.Test;

class TextImageGeneratorTest {
    private static final Path SNAPSHOT_DIRECTORY = Path.of("src", "main", "resources", "snapshot");
    private static final String FONT_RESOURCE = "/font.otf";
    private final TextImageGenerator generator = new TextImageGenerator();

    @Test
    void generatesBufferedImage() throws Exception {
        BufferedImage image = generator.generate(
            "hello",
            Color.BLACK,
            Color.WHITE,
            new Dimension(300, 160),
            new Insets(12, 16, 12, 16),
            defaultFontSupplier()
        );

        assertNotNull(image);
        assertEquals(300, image.getWidth());
        assertEquals(160, image.getHeight());
        assertEquals(Color.WHITE.getRGB(), image.getRGB(0, 0));
    }

    @Test
    void rejectsNullText() {
        NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> generator.generate(null, Color.BLACK, Color.WHITE, new Dimension(300, 160), new Insets(12, 16, 12, 16), defaultFontSupplier())
        );

        assertEquals("text", exception.getMessage());
    }

    @Test
    void rejectsNullForegroundColor() {
        NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> generator.generate("hello", null, Color.WHITE, new Dimension(300, 160), new Insets(12, 16, 12, 16), defaultFontSupplier())
        );

        assertEquals("foregroundColor", exception.getMessage());
    }

    @Test
    void rejectsNullBackgroundColor() {
        NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> generator.generate("hello", Color.BLACK, null, new Dimension(300, 160), new Insets(12, 16, 12, 16), defaultFontSupplier())
        );

        assertEquals("backgroundColor", exception.getMessage());
    }

    @Test
    void rejectsNullDimension() {
        NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> generator.generate("hello", Color.BLACK, Color.WHITE, null, new Insets(12, 16, 12, 16), defaultFontSupplier())
        );

        assertEquals("dimension", exception.getMessage());
    }

    @Test
    void rejectsNullPadding() {
        NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> generator.generate("hello", Color.BLACK, Color.WHITE, new Dimension(300, 160), null, defaultFontSupplier())
        );

        assertEquals("padding", exception.getMessage());
    }

    @Test
    void rejectsNullFontSupplier() {
        NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> generator.generate("hello", Color.BLACK, Color.WHITE, new Dimension(300, 160), new Insets(12, 16, 12, 16), null)
        );

        assertEquals("fontSupplier", exception.getMessage());
    }

    @Test
    void rejectsNonPositiveWidth() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> generator.generate("hello", Color.BLACK, Color.WHITE, new Dimension(0, 160), new Insets(12, 16, 12, 16), defaultFontSupplier())
        );

        assertEquals("dimension.width must be positive", exception.getMessage());
    }

    @Test
    void rejectsNonPositiveHeight() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> generator.generate("hello", Color.BLACK, Color.WHITE, new Dimension(300, 0), new Insets(12, 16, 12, 16), defaultFontSupplier())
        );

        assertEquals("dimension.height must be positive", exception.getMessage());
    }

    @Test
    void rejectsNegativeInsets() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> generator.generate("hello", Color.BLACK, Color.WHITE, new Dimension(300, 160), new Insets(-1, 16, 12, 16), defaultFontSupplier())
        );

        assertEquals("padding values must be non-negative", exception.getMessage());
    }

    @Test
    void rejectsInsetsThatConsumeImageWidth() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> generator.generate("hello", Color.BLACK, Color.WHITE, new Dimension(20, 160), new Insets(12, 10, 12, 10), defaultFontSupplier())
        );

        assertEquals("padding must leave drawable area inside the image", exception.getMessage());
    }

    @Test
    void rejectsInsetsThatConsumeImageHeight() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> generator.generate("hello", Color.BLACK, Color.WHITE, new Dimension(300, 20), new Insets(10, 10, 10, 10), defaultFontSupplier())
        );

        assertEquals("padding must leave drawable area inside the image", exception.getMessage());
    }

    @Test
    void rejectsFontSupplierReturningNull() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> generator.generate("hello", Color.BLACK, Color.WHITE, new Dimension(300, 160), new Insets(12, 16, 12, 16), () -> null)
        );

        assertEquals("fontSupplier must provide a font", exception.getMessage());
    }

    @Test
    void matchesSnapshotImagesFromMetadata() throws Exception {
        List<Path> snapshotMetadataFiles;
        try (var paths = Files.list(SNAPSHOT_DIRECTORY)) {
            snapshotMetadataFiles = paths
                .filter(path -> path.getFileName().toString().endsWith(".json"))
                .sorted(Comparator.comparing(path -> path.getFileName().toString()))
                .toList();
        }

        assertEquals(5, snapshotMetadataFiles.size());

        for (Path metadataFile : snapshotMetadataFiles) {
            SnapshotParameters parameters = parseSnapshotParameters(Files.readString(metadataFile));
            Path imageFile = SNAPSHOT_DIRECTORY.resolve(
                metadataFile.getFileName().toString().replace(".json", ".png")
            );

            BufferedImage actual = generator.generate(
                parameters.text(),
                parameters.foregroundColor(),
                parameters.backgroundColor(),
                parameters.dimension(),
                parameters.padding(),
                snapshotFontSupplier()
            );
            BufferedImage expected = ImageIO.read(imageFile.toFile());

            assertNotNull(expected);
            assertImagesEqual(expected, actual, imageFile);
        }
    }

    private Supplier<Font> defaultFontSupplier() {
        return () -> new Font("Dialog", Font.PLAIN, 48);
    }

    private Supplier<Font> snapshotFontSupplier() {
        return () -> {
            try (InputStream inputStream = getClass().getResourceAsStream(FONT_RESOURCE)) {
                if (inputStream == null) {
                    throw new IllegalStateException("Missing font resource: " + FONT_RESOURCE);
                }
                return Font.createFont(Font.TRUETYPE_FONT, inputStream).deriveFont(Font.PLAIN, 54f);
            } catch (IOException | FontFormatException exception) {
                throw new IllegalStateException("Failed to load font resource: " + FONT_RESOURCE, exception);
            }
        };
    }

    private SnapshotParameters parseSnapshotParameters(String json) {
        return new SnapshotParameters(
            extractString(json, "text"),
            new Color(
                extractInt(json, "foregroundColor", "red"),
                extractInt(json, "foregroundColor", "green"),
                extractInt(json, "foregroundColor", "blue")
            ),
            new Color(
                extractInt(json, "backgroundColor", "red"),
                extractInt(json, "backgroundColor", "green"),
                extractInt(json, "backgroundColor", "blue")
            ),
            new Dimension(
                extractInt(json, "dimension", "width"),
                extractInt(json, "dimension", "height")
            ),
            new Insets(
                extractInt(json, "padding", "top"),
                extractInt(json, "padding", "left"),
                extractInt(json, "padding", "bottom"),
                extractInt(json, "padding", "right")
            )
        );
    }

    private String extractString(String json, String key) {
        Pattern pattern = Pattern.compile("\"" + key + "\"\\s*:\\s*\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(json);
        assertTrue(matcher.find(), "Missing string field: " + key);
        return matcher.group(1);
    }

    private int extractInt(String json, String objectKey, String valueKey) {
        Pattern pattern = Pattern.compile(
            "\"" + objectKey + "\"\\s*:\\s*\\{[^}]*\"" + valueKey + "\"\\s*:\\s*(\\d+)"
        );
        Matcher matcher = pattern.matcher(json);
        assertTrue(matcher.find(), "Missing numeric field: " + objectKey + "." + valueKey);
        return Integer.parseInt(matcher.group(1));
    }

    private void assertImagesEqual(BufferedImage expected, BufferedImage actual, Path imageFile) {
        assertEquals(expected.getWidth(), actual.getWidth(), "Width mismatch for " + imageFile.getFileName());
        assertEquals(expected.getHeight(), actual.getHeight(), "Height mismatch for " + imageFile.getFileName());

        for (int y = 0; y < expected.getHeight(); y++) {
            for (int x = 0; x < expected.getWidth(); x++) {
                assertEquals(
                    expected.getRGB(x, y),
                    actual.getRGB(x, y),
                    "Pixel mismatch for " + imageFile.getFileName() + " at (" + x + "," + y + ")"
                );
            }
        }
    }

    private record SnapshotParameters(
        String text,
        Color foregroundColor,
        Color backgroundColor,
        Dimension dimension,
        Insets padding
    ) {
    }
}
