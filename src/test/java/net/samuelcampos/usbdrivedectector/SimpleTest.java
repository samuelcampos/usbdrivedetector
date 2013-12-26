package net.samuelcampos.usbdrivedectector;

import net.samuelcampos.usbdrivedectector.devicesmanager.StorageDevicesManager;

/**
 *
 * @author samuelcampos
 */
public class SimpleTest {
    public static void main(String[] args) {
		StorageDevicesManager rmManager = StorageDevicesManager
				.getInstance();

		for (USBStorageDevice rmDevice : rmManager.getRemovableDevices()) {

			System.out.println(rmDevice);
		}

	}
}
