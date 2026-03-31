package org.raistlic.interview;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import org.junit.jupiter.api.Test;

class TextImageGeneratorTest {
    @Test
    void generatesBufferedImage() throws Exception {
        BufferedImage image = new TextImageGenerator().generate("hello");

        assertNotNull(image);
        assertTrue(image.getWidth() > 0);
        assertTrue(image.getHeight() > 0);
    }
}
