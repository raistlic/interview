package org.raistlic.interview;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;

class TextImageGeneratorTest {
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

    private Supplier<Font> defaultFontSupplier() {
        return () -> new Font("Dialog", Font.PLAIN, 48);
    }
}
