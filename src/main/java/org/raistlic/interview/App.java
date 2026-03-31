package org.raistlic.interview;

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
import java.util.Random;
import java.util.function.Supplier;
import javax.imageio.ImageIO;

public final class App {
    private static final int SNAPSHOT_COUNT = 5;
    private static final Path SNAPSHOT_DIRECTORY = Path.of("src", "main", "resources", "snapshot");
    private static final String FONT_RESOURCE = "/font.otf";
    private static final Dimension IMAGE_SIZE = new Dimension(640, 240);
    private static final Insets IMAGE_PADDING = new Insets(32, 32, 32, 32);
    private static final Color FOREGROUND_COLOR = new Color(34, 34, 34);
    private static final Color BACKGROUND_COLOR = new Color(250, 245, 239);

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
            BufferedImage image = generator.generate(
                randomText(random),
                FOREGROUND_COLOR,
                BACKGROUND_COLOR,
                IMAGE_SIZE,
                IMAGE_PADDING,
                fontSupplier
            );
            ImageIO.write(image, "png", SNAPSHOT_DIRECTORY.resolve("snapshot-" + index + ".png").toFile());
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
}
