package net.samuelcampos.usbdrivedetector.unmounters;

import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import net.samuelcampos.usbdrivedetector.process.CommandExecutor;
import net.samuelcampos.usbdrivedetector.process.OutputProcessor;

@Slf4j
public abstract class LinuxAndOSXStorageDeviceUnmounterBase extends AbstractStorageDeviceUnmounter {
    private final CommandExecutor commandExecutor = new CommandExecutor();

    protected void unmount(String unmoundCommand) {
        try (final OutputProcessor commandOutputProcessor = commandExecutor.executeCommand(unmoundCommand)) {
            Thread.sleep(1000L);
            commandOutputProcessor.processOutput(log::trace);
            log.debug("Device successfully unmount: {}", unmoundCommand);
        } catch (IOException | InterruptedException e) {
            log.error("Unable to unmount device: {}", unmoundCommand, e);
        }
    }
}
