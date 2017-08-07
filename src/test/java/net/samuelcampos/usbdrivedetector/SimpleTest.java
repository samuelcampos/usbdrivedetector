package net.samuelcampos.usbdrivedetector;

import net.samuelcampos.usbdrivedetector.events.IUSBDriveListener;
import net.samuelcampos.usbdrivedetector.events.USBStorageEvent;

/**
 *
 * @author samuelcampos
 */
public class SimpleTest implements IUSBDriveListener{
	public static void main(String[] args) {
		System.out.println("Start Test");
		USBDeviceDetectorManager driveDetector = new USBDeviceDetectorManager();

		for(USBStorageDevice device : driveDetector.getRemovableDevices()){
			System.out.println(device);
		}

		SimpleTest sTest = new SimpleTest();

		driveDetector.addDriveListener(sTest);

		try {
			Thread.sleep(60000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		System.out.println("Test finished");
	}

	private SimpleTest () {

	}

	@Override
	public void usbDriveEvent(USBStorageEvent event) {
		System.out.println(event);
	}
}
