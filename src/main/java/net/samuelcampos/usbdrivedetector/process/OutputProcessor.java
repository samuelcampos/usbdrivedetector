package net.samuelcampos.usbdrivedetector.process;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.Predicate;

@Slf4j
@RequiredArgsConstructor
public abstract class OutputProcessor implements Closeable {

    private final BufferedReader input;

    public void processOutput(final Consumer<String> method) throws IOException {
        String outputLine;
        while ((outputLine = this.readOutputLine()) != null) {
            method.accept(outputLine);
        }
    }

    public boolean checkOutput(final Predicate<String> method) throws IOException{
        String outputLine;
        while ((outputLine = this.readOutputLine()) != null) {
            if (method.test(outputLine)) {
                return true;
            }
        }

        return false;
    }

    private String readOutputLine() throws IOException {
        if(input == null) {
            throw new IllegalStateException("You need to call 'executeCommand' method first");
        }

        final String outputLine = input.readLine();

        if(outputLine != null) {
            return outputLine.trim();
        }

        return null;
    }

    protected void closeInput() {
        if (input != null) {
            try {
                input.close();
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
    }
}
