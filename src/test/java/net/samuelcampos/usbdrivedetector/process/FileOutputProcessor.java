package net.samuelcampos.usbdrivedetector.process;

import java.io.*;

public class FileOutputProcessor extends OutputProcessor {

    public FileOutputProcessor(File file) throws FileNotFoundException {
        super(new BufferedReader(new FileReader(file)));
    }

    @Override
    public void close() throws IOException {
        super.closeInput();
    }
}
