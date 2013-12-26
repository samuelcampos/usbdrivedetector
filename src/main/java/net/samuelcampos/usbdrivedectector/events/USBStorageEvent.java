package net.samuelcampos.usbdrivedectector.events;

import net.samuelcampos.usbdrivedectector.USBStorageDevice;

/**
 *
 * @author samuelcampos
 */
public class USBStorageEvent {
    private final USBStorageDevice storageDevice;
    private final DeviceEventType eventType;
    
    public USBStorageEvent(USBStorageDevice storageDevice, DeviceEventType eventType) {
        this.storageDevice = storageDevice;
        this.eventType = eventType;
    }
    
    public USBStorageDevice getStorageDevice() {
        return storageDevice;
    }

    public DeviceEventType getEventType() {
        return eventType;
    }
}
