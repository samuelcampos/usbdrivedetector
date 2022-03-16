package net.samuelcampos.usbdrivedetector.process;

import lombok.extern.slf4j.Slf4j;

import java.io.*;

@Slf4j
public class CommandOutputProcessor extends OutputProcessor {

    private final String command;
    private final Process process;

    public CommandOutputProcessor(String command, Process process) {
        super(new BufferedReader(new InputStreamReader(process.getInputStream())));
        this.command = command;
        this.process = process;
    }

    @Override
    public void close() throws IOException {
        try {
            int exitValue = process.waitFor();

            if (exitValue != 0) {
                log.warn("Abnormal command '{}' termination. Exit value: {}", command, exitValue);
            }
        } catch (InterruptedException e) {
            log.error("Error while waiting for command '{}' to complete", command, e);
        }

        super.closeInput();

        process.destroy();
    }
}
