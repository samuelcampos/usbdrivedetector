package net.samuelcampos.usbdrivedetector.utils;

import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.io.InputStream;

@UtilityClass
public class TestUtils {

    public InputStream getInputStreamClassPath(String fileName) throws IOException
    {
        ClassLoader classLoader = TestUtils.class.getClassLoader();
        InputStream resource = classLoader.getResourceAsStream(fileName);

        if (resource == null) {
            throw new IllegalArgumentException("file is not found!");
        } else {
            return resource;
        }
    }
}
