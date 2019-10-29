/*
 * Copyright 2014 samuelcampos.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.samuelcampos.usbdrivedetector.detectors;

import lombok.extern.slf4j.Slf4j;
import net.samuelcampos.usbdrivedetector.USBStorageDevice;
import net.samuelcampos.usbdrivedetector.process.CommandExecutor;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author samuelcampos
 */
@Slf4j
public class WindowsStorageDeviceDetector extends AbstractStorageDeviceDetector {

    private static final String WMIC_PATH_WIN8 = "wmic";
    // Window 10 broke compatibility by removing the wbem dir from his PATH
    private static final String WMIC_PATH_WIN10 = System.getenv("WINDIR") + "\\System32\\wbem\\wmic";

    /**
     * wmic logicaldisk where drivetype=2 get description,deviceid,volumename
     */
    private static final String CMD_WMI_ARGS = "logicaldisk where drivetype=2 get deviceid, VolumeSerialNumber";
    private static final String CMD_WMI_USB;

    static {
        String wmicPath;
        if (Float.parseFloat(System.getProperty("os.version")) >= 6.2) {
            wmicPath = WMIC_PATH_WIN8;
        } else {
            wmicPath = WMIC_PATH_WIN10;
        }
        CMD_WMI_USB = wmicPath + " " + CMD_WMI_ARGS;
    }

    protected WindowsStorageDeviceDetector() {
        super();
    }

    @Override
    public List<USBStorageDevice> getStorageDevicesDevices() {
        final ArrayList<USBStorageDevice> listDevices = new ArrayList<>();

        try (CommandExecutor commandExecutor = new CommandExecutor(CMD_WMI_USB)) {
            commandExecutor.processOutput(outputLine -> {

        	final String[] parts = outputLine.split(" ");

                if(parts.length > 1 && !parts[0].isEmpty() && !parts[0].equals("DeviceID") && !parts[0].equals(parts[parts.length - 1])) {
                	final String rootPath = parts[0] + File.separatorChar;
                    final String uuid = parts[parts.length - 1];
                    USBStorageDevice device = getUSBDevice(rootPath, getDeviceName(rootPath), rootPath, uuid);
                    if (device != null) {
                        listDevices.add(device);
                    }
                }
            });

        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }

        return listDevices;
    }

    private String getDeviceName(final String rootPath) {
        final File f = new File(rootPath);
        final FileSystemView v = FileSystemView.getFileSystemView();
        String name = v.getSystemDisplayName(f);

        if (name != null) {
            int idx = name.lastIndexOf('(');
            if (idx != -1) {
                name = name.substring(0, idx);
            }

            name = name.trim();
            if (name.isEmpty()) {
                name = null;
            }
        }
        return name;
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
//    @SuppressWarnings("unused")
//    private ArrayList<USBStorageDevice> getWindowsRemovableDevicesList() {
//
//        /**
//         * TODO: How to put this working in all languages?
//         */
//        String fileSystemDesc = "Removable Disk";
//
//        ArrayList<USBStorageDevice> listDevices = new ArrayList<USBStorageDevice>();
//
//        File[] roots = File.listRoots();
//
//        if (roots == null) {
//            // TODO: raise an error?
//            return listDevices;
//        }
//
//        for (File root : roots) {
//            if (root.canRead() && root.canWrite() && fsView.isDrive(root)
//                    && !fsView.isFloppyDrive(root)) {
//
//                if (fileSystemDesc.equalsIgnoreCase(fsView
//                        .getSystemTypeDescription(root))) {
//                    USBStorageDevice device = new USBStorageDevice(root,
//                            fsView.getSystemDisplayName(root));
//                    listDevices.add(device);
//                }
//
//                System.out.println(fsView.getSystemDisplayName(root) + " - "
//                        + fsView.getSystemTypeDescription(root));
//
//                /*
//                 * FileSystemView.getSystemTypeDescription();
//                 *
//                 * Windows (8): Windows (7): "Removable Disk" Windows (XP):
//                 * Linux (Ubuntu): OSX (10.7):
//                 */
//            }
//        }
//
//        return listDevices;
//    }
}
