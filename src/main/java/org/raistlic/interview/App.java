package org.raistlic.interview;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;
import java.util.function.Supplier;
import javax.imageio.ImageIO;

public final class App {
    private static final int SNAPSHOT_COUNT = 5;
    private static final Path SNAPSHOT_DIRECTORY = Path.of("src", "main", "resources", "snapshot");
    private static final String FONT_RESOURCE = "/font.otf";

    private App() {
    }

    public static void main(String[] args) throws IOException, FontFormatException {
        generateSnapshots();
    }

    static void generateSnapshots() throws IOException, FontFormatException {
        Files.createDirectories(SNAPSHOT_DIRECTORY);

        TextImageGenerator generator = new TextImageGenerator();
        Supplier<Font> fontSupplier = createFontSupplier();
        Random random = new Random();

        for (int index = 1; index <= SNAPSHOT_COUNT; index++) {
            SnapshotParameters parameters = randomParameters(random);
            BufferedImage image = generator.generate(
                parameters.text(),
                parameters.foregroundColor(),
                parameters.backgroundColor(),
                parameters.dimension(),
                parameters.padding(),
                fontSupplier
            );
            Path imageFile = SNAPSHOT_DIRECTORY.resolve("snapshot-" + index + ".png");
            Path metadataFile = SNAPSHOT_DIRECTORY.resolve("snapshot-" + index + ".json");
            ImageIO.write(image, "png", imageFile.toFile());
            Files.writeString(metadataFile, parameters.toJson(), StandardCharsets.UTF_8);
        }
    }

    private static Supplier<Font> createFontSupplier() {
        return () -> {
            try (InputStream inputStream = App.class.getResourceAsStream(FONT_RESOURCE)) {
                if (inputStream == null) {
                    throw new IllegalStateException("Missing font resource: " + FONT_RESOURCE);
                }
                return Font.createFont(Font.TRUETYPE_FONT, inputStream).deriveFont(Font.PLAIN, 54f);
            } catch (IOException | FontFormatException exception) {
                throw new IllegalStateException("Failed to load font resource: " + FONT_RESOURCE, exception);
            }
        };
    }

    private static String randomText(Random random) {
        return "snapshot-" + Integer.toHexString(random.nextInt()).replace('-', 'a');
    }

    private static SnapshotParameters randomParameters(Random random) {
        Dimension dimension = new Dimension(randomBetween(random, 320, 960), randomBetween(random, 160, 480));
        Insets padding = randomInsets(random, dimension);
        return new SnapshotParameters(
            randomText(random),
            randomColor(random),
            randomColor(random),
            dimension,
            padding
        );
    }

    private static int randomBetween(Random random, int min, int max) {
        return min + random.nextInt(max - min + 1);
    }

    private static Color randomColor(Random random) {
        return new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

    private static Insets randomInsets(Random random, Dimension dimension) {
        int maxHorizontalInset = Math.max(0, (dimension.width - 2) / 2);
        int maxVerticalInset = Math.max(0, (dimension.height - 2) / 2);
        int left = random.nextInt(maxHorizontalInset + 1);
        int right = random.nextInt(Math.max(1, dimension.width - left - 1));
        right = Math.min(right, maxHorizontalInset);
        if (left + right >= dimension.width) {
            right = Math.max(0, dimension.width - left - 1);
        }
        int top = random.nextInt(maxVerticalInset + 1);
        int bottom = random.nextInt(Math.max(1, dimension.height - top - 1));
        bottom = Math.min(bottom, maxVerticalInset);
        if (top + bottom >= dimension.height) {
            bottom = Math.max(0, dimension.height - top - 1);
        }
        return new Insets(top, left, bottom, right);
    }

    private record SnapshotParameters(
        String text,
        Color foregroundColor,
        Color backgroundColor,
        Dimension dimension,
        Insets padding
    ) {
        private String toJson() {
            return """
                {
                  "text": "%s",
                  "foregroundColor": {"red": %d, "green": %d, "blue": %d},
                  "backgroundColor": {"red": %d, "green": %d, "blue": %d},
                  "dimension": {"width": %d, "height": %d},
                  "padding": {"top": %d, "left": %d, "bottom": %d, "right": %d}
                }
                """.formatted(
                text,
                foregroundColor.getRed(),
                foregroundColor.getGreen(),
                foregroundColor.getBlue(),
                backgroundColor.getRed(),
                backgroundColor.getGreen(),
                backgroundColor.getBlue(),
                dimension.width,
                dimension.height,
                padding.top,
                padding.left,
                padding.bottom,
                padding.right
            );
        }
    }
}
