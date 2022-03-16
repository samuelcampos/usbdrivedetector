package net.samuelcampos.usbdrivedetector.utils;

import lombok.experimental.UtilityClass;

import java.io.File;
import java.io.IOException;
import java.net.URL;

@UtilityClass
public class TestUtils {

    public File getFileFromClassPath(String fileName) throws IOException
    {
        ClassLoader classLoader = TestUtils.class.getClassLoader();
        URL resource = classLoader.getResource(fileName);

        if (resource == null) {
            throw new IllegalArgumentException("file is not found!");
        } else {
            return new File(resource.getFile());
        }
    }
}
