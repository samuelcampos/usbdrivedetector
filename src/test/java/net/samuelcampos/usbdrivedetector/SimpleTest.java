package net.samuelcampos.usbdrivedetector;

import net.samuelcampos.usbdrivedetector.events.IUSBDriveListener;
import net.samuelcampos.usbdrivedetector.events.USBStorageEvent;

import java.io.IOException;

/**
 *
 * @author samuelcampos
 */
public class SimpleTest implements IUSBDriveListener{
    public static void main(String[] args) throws IOException {
        System.out.println("Start Test");
		USBDeviceDetectorManager driveDetector = new USBDeviceDetectorManager();

        driveDetector.getRemovableDevices().forEach(System.out::println);

        SimpleTest sTest = new SimpleTest();

        driveDetector.addDriveListener(sTest);

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (!driveDetector.getRemovableDevices().isEmpty()) {
            driveDetector.unmountStorageDevice(driveDetector.getRemovableDevices().get(0));
        }

        System.out.println("Test finished");

        driveDetector.close();
	}
    
    private SimpleTest () {
        
    }

    @Override
    public void usbDriveEvent(USBStorageEvent event) {
        System.out.println(event);
    }
}
