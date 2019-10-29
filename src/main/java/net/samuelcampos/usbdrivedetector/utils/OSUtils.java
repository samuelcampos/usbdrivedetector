package net.samuelcampos.usbdrivedetector.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class OSUtils {
    public static final String OS_NAME = System.getProperty("os.name").toLowerCase();
}
