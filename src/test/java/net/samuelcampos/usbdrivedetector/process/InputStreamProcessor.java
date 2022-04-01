package net.samuelcampos.usbdrivedetector.process;

import java.io.*;

public class InputStreamProcessor extends OutputProcessor {

    public InputStreamProcessor(InputStream inputStream) throws FileNotFoundException {
        super(new BufferedReader(new InputStreamReader(inputStream)));
    }

    @Override
    public void close() throws IOException {
        super.closeInput();
    }
}
