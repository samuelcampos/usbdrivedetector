package net.samuelcampos.usbdrivedetector.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OSUtilsTest {

    @Test
    public void testGetOsType() {
        System.setProperty("os.name", "unknown");
        assertThrows(UnsupportedOperationException.class, OSUtils::getOsType);

        System.setProperty("os.name", "Mac OS X");
        assertEquals(OSType.MAC_OS, OSUtils.getOsType());

        System.setProperty("os.name", "Windows NT");
        assertEquals(OSType.WINDOWS, OSUtils.getOsType());

        System.setProperty("os.name", "Windows 8.1");
        assertEquals(OSType.WINDOWS, OSUtils.getOsType());
    }
}