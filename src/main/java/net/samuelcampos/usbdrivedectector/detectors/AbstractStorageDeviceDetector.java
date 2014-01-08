package net.samuelcampos.usbdrivedectector.detectors;

import java.util.List;
import javax.swing.filechooser.FileSystemView;
import net.samuelcampos.usbdrivedectector.USBStorageDevice;

/**
 * This class is prepared to:
 * <ul>
 * <li>Windows (XP or newer)</li>
 * <li>Mac OS X (10.7 or newer)</li>
 * </ul>
 *
 * @author samuelcampos
 */
public abstract class AbstractStorageDeviceDetector {

    protected final FileSystemView fsView;

    private static final String OSName = System.getProperty("os.name")
            .toLowerCase();
    
    // private static final String OSVersion = System.getProperty("os.version");
    // private static final String OSArch = System.getProperty("os.arch");

    /**
     * {@link AbstractStorageDeviceDetector} instance. <br/>
     * This instance is created (Thread-Safe) when the JVM loads the class.
     */
    private static final AbstractStorageDeviceDetector instance;

    static {
        if (OSName.startsWith("win")) {
            instance = new WindowsStorageDeviceDetector();
        } else if (OSName.startsWith("linux")) {
            instance = new LinuxStorageDeviceDetector();
        } else if (OSName.startsWith("mac")) {
            instance = new OSXStorageDeviceDetector();
        }
        else {
            instance = null;
        }
    }
    
    public static AbstractStorageDeviceDetector getInstance () {
        if(instance == null)
            throw new UnsupportedOperationException("Your Operative System (" + OSName +") is not supported!");
        
        return instance;
    }

    public AbstractStorageDeviceDetector() {
        fsView = FileSystemView.getFileSystemView();
    }

    /**
     * Returns the storage devices connected to the computer.
     * 
     * @return the list of the USB storage devices
     */
    public abstract List<USBStorageDevice> getRemovableDevices();
}
