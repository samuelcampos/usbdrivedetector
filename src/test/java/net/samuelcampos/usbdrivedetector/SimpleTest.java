package net.samuelcampos.usbdrivedetector;

import java.io.IOException;

import net.samuelcampos.usbdrivedetector.events.IUSBDriveListener;
import net.samuelcampos.usbdrivedetector.events.USBStorageEvent;

/**
 *
 * @author samuelcampos
 */
public class SimpleTest implements IUSBDriveListener {
	public static void main(String[] args) throws IOException {
		System.out.println("Start Test");
		
		USBDeviceDetectorManager driveDetector = new USBDeviceDetectorManager();
		driveDetector.getRemovableDevices().forEach(System.out::println);

		SimpleTest sTest = new SimpleTest();

		driveDetector.addDriveListener(sTest);

		try {
			Thread.sleep(20000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if (!driveDetector.getRemovableDevices().isEmpty()) {
			driveDetector.unmountStorageDevice(driveDetector.getRemovableDevices().get(0));
		}

		try {
			Thread.sleep(20000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		System.out.println("Test finished");
	}

	private SimpleTest() {

	}

	@Override
	public void usbDriveEvent(USBStorageEvent event) {
		System.out.println(event);
	}
}
