package net.samuelcampos.usbdrivedectector.events;

/**
 * 
 * @author samuelcampos
 */
public interface IUSBDriveListener {
    
    public void usbDriveConnected(USBStorageEvent ev);
    
    public void usbDriveRemoved(USBStorageEvent ev);
}
