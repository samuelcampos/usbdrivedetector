package net.samuelcampos.usbdrivedectector;

import net.samuelcampos.usbdrivedectector.detectors.AbstractStorageDeviceDetector;
import net.samuelcampos.usbdrivedectector.events.IUSBDriveListener;
import net.samuelcampos.usbdrivedectector.events.USBStorageEvent;

/**
 *
 * @author samuelcampos
 */
public class SimpleTest implements IUSBDriveListener{
    public static void main(String[] args) {
		AbstractStorageDeviceDetector rmManager = AbstractStorageDeviceDetector
				.getInstance();

		for (USBStorageDevice rmDevice : rmManager.getRemovableDevices()) {

			System.out.println(rmDevice);
		}
        
        USBDeviceDetectorManager driveDetector = new USBDeviceDetectorManager();
        SimpleTest sTest = new SimpleTest();
        
        driveDetector.addDriveListener(sTest);
	}
    
    private SimpleTest () {
        
    }

    @Override
    public void usbDriveEvent(USBStorageEvent event) {
        System.out.println(event);
    }
}
