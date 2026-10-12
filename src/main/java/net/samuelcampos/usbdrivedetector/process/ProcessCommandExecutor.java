package net.samuelcampos.usbdrivedetector.process;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class ProcessCommandExecutor implements CommandExecutor {

    @Override
    public OutputProcessor executeCommand(final String command) throws IOException {
        if (log.isTraceEnabled()) {
            log.trace("Running command: {}", command);
        }

        Process process = Runtime.getRuntime().exec(command);

        return new CommandOutputProcessor(command, process);
    }
}
