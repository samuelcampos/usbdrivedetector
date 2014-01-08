package net.samuelcampos.usbdrivedectector.events;

/**
 * Interface to implement by the classes who want to receive notifications when 
 * there are devices Connected or Removed of the computer.
 * 
 * @author samuelcampos
 */
public interface IUSBDriveListener {
    
    public void usbDriveEvent(USBStorageEvent event);
}
