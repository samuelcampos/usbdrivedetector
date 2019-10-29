package net.samuelcampos.usbdrivedetector.unmounters;

import java.io.IOException;

import lombok.extern.slf4j.Slf4j;
import net.samuelcampos.usbdrivedetector.USBStorageDevice;
import net.samuelcampos.usbdrivedetector.process.CommandExecutor;

@Slf4j
public class LinuxStorageDeviceUnmounter extends AbstractStorageDeviceUnmounter {

    @Override
    public void unmount(USBStorageDevice usbStorageDevice) throws IOException {
		try {
			unmount("umount " + usbStorageDevice.getDevice());
		} catch (IOException e) {
			unmount("sudo umount " + usbStorageDevice.getDevice());
		}
    }

    private void unmount(String command) throws IOException {
		try (CommandExecutor commandExecutor = new CommandExecutor(command)) {
			// Some times you need to wait until it is fully unmounted
			Thread.sleep(1000L);
		} catch (InterruptedException e) {
			log.error("Unable to unmount device", e);
		}
    }

}
