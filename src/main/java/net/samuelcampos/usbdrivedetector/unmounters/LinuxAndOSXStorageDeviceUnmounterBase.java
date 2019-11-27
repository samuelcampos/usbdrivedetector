package net.samuelcampos.usbdrivedetector.unmounters;

import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import net.samuelcampos.usbdrivedetector.process.CommandExecutor;

@Slf4j
public abstract class LinuxAndOSXStorageDeviceUnmounterBase extends AbstractStorageDeviceUnmounter {
    protected void unmount(String unmoundCommand) {
        try (CommandExecutor commandExecutor = new CommandExecutor(unmoundCommand)) {
            Thread.sleep(1000L);
            commandExecutor.processOutput(log::trace);
            log.debug("Device successfully unmount: {}", unmoundCommand);
        } catch (IOException | InterruptedException e) {
            log.error("Unable to unmount device: {}", unmoundCommand, e);
        }
    }
}
