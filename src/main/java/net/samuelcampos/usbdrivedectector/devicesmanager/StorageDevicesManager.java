package net.samuelcampos.usbdrivedectector.devicesmanager;

import java.util.List;
import javax.swing.filechooser.FileSystemView;
import net.samuelcampos.usbdrivedectector.USBStorageDevice;

/**
 * This class is prepared to:
 * <ul>
 * <li>Windows (XP or newer)</li>
 * <li>Linux</li>
 * <li>Mac OS X</li>
 * </ul>
 *
 * @author samuelcampos
 */
public abstract class StorageDevicesManager {

    protected final FileSystemView fsView;

    private static final String OSName = System.getProperty("os.name")
            .toLowerCase();
    
    // private static final String OSVersion = System.getProperty("os.version");
    // private static final String OSArch = System.getProperty("os.arch");

    /**
     * {@link StorageDevicesManager} instance. <br/>
     * This instance is created (Thread-Safe) when the JVM loads the class.
     */
    private static final StorageDevicesManager instance;

    static {
        if (OSName.startsWith("win")) {
            instance = new WindowsDevicesManager();
        } else if (OSName.startsWith("linux")) {
            instance = null;
        } else if (OSName.startsWith("mac")) {
            instance = new OSXRemovableDevicesManager();
        }
        else {
            instance = null;
        }
    }
    
    public static StorageDevicesManager getInstance () {
        return instance;
    }

    public StorageDevicesManager() {
        fsView = FileSystemView.getFileSystemView();
    }

    /**
     * 
     * @return the list of the 
     */
    public abstract List<USBStorageDevice> getRemovableDevices();
}
