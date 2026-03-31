package org.raistlic.interview;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

class AppTest {
    @Test
    void mainRuns() {
        assertDoesNotThrow(() -> App.main(new String[0]));
    }
}
