package net.samuelcampos.usbdrivedetector.process;

import com.github.tuupertunut.powershelllibjava.PowerShell;
import com.github.tuupertunut.powershelllibjava.PowerShellExecutionException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class PowerShellCommandExecutor implements CommandExecutor {

    @Override
    public OutputProcessor executeCommand(final String command) throws IOException {
        if (log.isTraceEnabled()) {
            log.trace("Running command: {}", command);
        }

        try (PowerShell psSession = PowerShell.open()) {
            String output = psSession.executeCommands(command);
            return OutputProcessor.createFrom(output);
        } catch (PowerShellExecutionException e) {
            throw new IOException(e);
        }
    }
}
