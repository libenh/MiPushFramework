package top.trumeet.common.utils.rom;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class OplusCheckerTest {
    @Test
    public void detectsColorOs() {
        assertTrue(OplusChecker.containsOplusRomMarker("ColorOS 15"));
    }

    @Test
    public void detectsRealmeUi() {
        assertTrue(OplusChecker.containsOplusRomMarker("realmeUI 6.0"));
    }

    @Test
    public void detectsOxygenOs() {
        assertTrue(OplusChecker.containsOplusRomMarker("OxygenOS 16"));
    }

    @Test
    public void doesNotClassifyGenericAndroidAsOplus() {
        assertFalse(OplusChecker.containsOplusRomMarker("Android 16; google/raven"));
    }

    @Test
    public void handlesEmptyValues() {
        assertFalse(OplusChecker.containsOplusRomMarker(null));
        assertFalse(OplusChecker.containsOplusRomMarker(""));
    }
}
