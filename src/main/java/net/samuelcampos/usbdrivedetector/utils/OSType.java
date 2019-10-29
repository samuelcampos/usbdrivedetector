package net.samuelcampos.usbdrivedetector.utils;

public enum OSType {
    WINDOWS,
    MAC_OS,
    LINUX;

    public static OSType getOSType(String OSName) {
        if (OSName.startsWith("win")) {
            return WINDOWS;
        }

        if (OSName.startsWith("linux")) {
            return LINUX;
        }

        if (OSName.startsWith("mac")) {
            return MAC_OS;
        }

        throw new UnsupportedOperationException("Your Operative System (" + OSName + ") is not supported!");
    }
}
