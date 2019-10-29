package net.samuelcampos.usbdrivedetector.unmounters;

import lombok.extern.slf4j.Slf4j;
import net.samuelcampos.usbdrivedetector.USBStorageDevice;
import net.samuelcampos.usbdrivedetector.process.CommandExecutor;

import java.io.IOException;

@Slf4j
public class OSXStorageDeviceUnmounter extends AbstractStorageDeviceUnmounter {

    @Override
    public void unmount(final USBStorageDevice usbStorageDevice) {
        String unmoundCommand = "diskutil unmountDisk " + usbStorageDevice.getDevice();

        try (CommandExecutor commandExecutor = new CommandExecutor(unmoundCommand)) {
            commandExecutor.processOutput(log::trace);

            log.debug("Device successfully unmount: {}", usbStorageDevice);
        } catch (IOException e) {
            log.error("Unable to unmount device: {}", usbStorageDevice, e);
        }
    }
}
