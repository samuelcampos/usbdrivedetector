package net.samuelcampos.usbdrivedectector.detectors;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import net.samuelcampos.usbdrivedectector.USBStorageDevice;
import org.apache.log4j.Logger;

/**
 *
 * @author samuelcampos
 */
public class WindowsStorageDeviceDetector extends AbstractStorageDeviceDetector {

    private static final Logger logger = Logger
            .getLogger(WindowsStorageDeviceDetector.class);

    /**
     * wmic logicaldisk where drivetype=2 get description,deviceid,volumename
     */
    private static final String windowsDetectUSBCommand = "wmic logicaldisk where drivetype=2 get deviceid";

    public WindowsStorageDeviceDetector() {

    }

    @Override
    public List<USBStorageDevice> getRemovableDevices() {
        ArrayList<USBStorageDevice> listDevices = new ArrayList<USBStorageDevice>();

        BufferedReader in = null;
        Process process = null;

        try {
            if (logger.isTraceEnabled()) {
                logger.trace("Running command: " + windowsDetectUSBCommand);
            }

            process = Runtime.getRuntime().exec(windowsDetectUSBCommand);

            in = new BufferedReader(new InputStreamReader(
                    process.getInputStream()));

            String line;
            while ((line = in.readLine()) != null) {
                line = line.trim();

                if (!line.isEmpty() && !"DeviceID".equals(line)) {

                    File root = new File(line + File.separatorChar);

                    if (logger.isTraceEnabled()) {
                        logger.trace("Device found: " + root.getAbsolutePath());
                    }

                    if (root.canRead() && root.canWrite()) {
                        USBStorageDevice device = new USBStorageDevice(root,
                                fsView.getSystemDisplayName(root));
                        listDevices.add(device);
                    }
                }
            }

        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }

            if (process != null) {
                process.destroy();
            }
        }

        return listDevices;
    }

    /**
     * Returns the list of the removable devices actually connected to the computer. <br/>
     * This method was effectively tested on:
     * <ul>
     * <li>Windows 7 (English)</li>
     * </ul>
     *
     * @deprecated replaced by {@link #getWindowsRemovableDevicesCommand()}
     *
     * @return the list of removable devices
     */
    @SuppressWarnings("unused")
    private ArrayList<USBStorageDevice> getWindowsRemovableDevicesList() {

        /**
         * TODO: How to put this working in all languages?
         */
        String fileSystemDesc = "Removable Disk";

        ArrayList<USBStorageDevice> listDevices = new ArrayList<USBStorageDevice>();

        File[] roots = File.listRoots();

        if (roots == null) {
            // TODO: raise an error?
            return listDevices;
        }

        for (File root : roots) {
            if (root.canRead() && root.canWrite() && fsView.isDrive(root)
                    && !fsView.isFloppyDrive(root)) {

                if (fileSystemDesc.equalsIgnoreCase(fsView
                        .getSystemTypeDescription(root))) {
                    USBStorageDevice device = new USBStorageDevice(root,
                            fsView.getSystemDisplayName(root));
                    listDevices.add(device);
                }

                System.out.println(fsView.getSystemDisplayName(root) + " - "
                        + fsView.getSystemTypeDescription(root));

                /*
                 * FileSystemView.getSystemTypeDescription();
                 * 
                 * Windows (8): Windows (7): "Removable Disk" Windows (XP):
                 * Linux (Ubuntu): OSX (10.7):
                 */
            }
        }

        return listDevices;
    }
}
