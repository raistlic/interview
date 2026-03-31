package org.raistlic.interview;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Objects;
import java.util.function.Supplier;

public final class TextImageGenerator {
    public BufferedImage generate(
        String text,
        Color foregroundColor,
        Color backgroundColor,
        Dimension dimension,
        Insets padding,
        Supplier<Font> fontSupplier
    ) {
        Objects.requireNonNull(text, "text");
        Objects.requireNonNull(foregroundColor, "foregroundColor");
        Objects.requireNonNull(backgroundColor, "backgroundColor");
        Objects.requireNonNull(dimension, "dimension");
        Objects.requireNonNull(padding, "padding");
        Objects.requireNonNull(fontSupplier, "fontSupplier");

        if (dimension.width <= 0) {
            throw new IllegalArgumentException("dimension.width must be positive");
        }
        if (dimension.height <= 0) {
            throw new IllegalArgumentException("dimension.height must be positive");
        }
        if (padding.top < 0 || padding.left < 0 || padding.bottom < 0 || padding.right < 0) {
            throw new IllegalArgumentException("padding values must be non-negative");
        }
        if (padding.left + padding.right >= dimension.width || padding.top + padding.bottom >= dimension.height) {
            throw new IllegalArgumentException("padding must leave drawable area inside the image");
        }

        Font font = fontSupplier.get();
        if (font == null) {
            throw new IllegalArgumentException("fontSupplier must provide a font");
        }

        BufferedImage image = new BufferedImage(dimension.width, dimension.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        try {
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            graphics.setColor(backgroundColor);
            graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
            graphics.setColor(foregroundColor);
            graphics.setFont(font);
            graphics.drawString(text, padding.left, padding.top + graphics.getFontMetrics().getAscent());
            return image;
        } finally {
            graphics.dispose();
        }
    }
}
