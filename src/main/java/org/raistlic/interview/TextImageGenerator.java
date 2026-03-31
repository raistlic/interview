package org.raistlic.interview;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public final class TextImageGenerator {
    private static final String FONT_RESOURCE = "/font.otf";

    public BufferedImage generate(String text) throws IOException, FontFormatException {
        Font font = loadFont().deriveFont(Font.PLAIN, 72f);
        BufferedImage measurementImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D measurementGraphics = measurementImage.createGraphics();
        try {
            measurementGraphics.setFont(font);
            FontMetrics metrics = measurementGraphics.getFontMetrics();
            int width = Math.max(1, metrics.stringWidth(text));
            int height = Math.max(1, metrics.getHeight());

            BufferedImage image = new BufferedImage(width + 48, height + 48, BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics = image.createGraphics();
            try {
                graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                graphics.setColor(Color.WHITE);
                graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
                graphics.setColor(Color.BLACK);
                graphics.setFont(font);
                FontMetrics drawMetrics = graphics.getFontMetrics();
                graphics.drawString(text, 24, 24 + drawMetrics.getAscent());
            } finally {
                graphics.dispose();
            }

            return image;
        } finally {
            measurementGraphics.dispose();
        }
    }

    private Font loadFont() throws IOException, FontFormatException {
        try (InputStream inputStream = getClass().getResourceAsStream(FONT_RESOURCE)) {
            if (inputStream == null) {
                throw new IOException("Missing font resource: " + FONT_RESOURCE);
            }
            return Font.createFont(Font.TRUETYPE_FONT, inputStream);
        }
    }
}
