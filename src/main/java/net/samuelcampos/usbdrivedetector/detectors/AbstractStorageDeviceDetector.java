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

//import com.sun.deploy.config.OSType;
import lombok.extern.slf4j.Slf4j;
import net.samuelcampos.usbdrivedetector.USBStorageDevice;
import net.samuelcampos.usbdrivedetector.utils.OSType;
import net.samuelcampos.usbdrivedetector.utils.OSUtils;

import java.io.File;
import java.util.List;

/**
 * This class is prepared to:
 * <ul>
 * <li>Windows (XP or newer)</li>
 * <li>Mac OS X (10.7 or newer)</li>
 * </ul>
 *
 * @author samuelcampos
 */
@Slf4j
public abstract class AbstractStorageDeviceDetector {

    /**
     * {@link AbstractStorageDeviceDetector} instance. <br/>
     * This instance is created (Thread-Safe) when the JVM loads the class.
     */
    private static AbstractStorageDeviceDetector instance;

    public static synchronized AbstractStorageDeviceDetector getInstance() {
        if (instance == null) {
            switch (OSType.getOSType(OSUtils.OS_NAME)) {
                case WINDOWS:
                    instance = new WindowsStorageDeviceDetector();
                    break;

                case LINUX:
                    instance = new LinuxStorageDeviceDetector();
                    break;

                case MAC_OS:
                    instance = new OSXStorageDeviceDetector();
                    break;
            }
        }

        return instance;
    }

    protected AbstractStorageDeviceDetector() {
    }

    /**
     * Returns the all storage devices currently connected to the computer.
     *
     * @return the list of the USB storage devices
     */
    public abstract List<USBStorageDevice> getStorageDevicesDevices();

    static USBStorageDevice getUSBDevice(final String rootPath) {
        return getUSBDevice(rootPath, null);
    }

    static USBStorageDevice getUSBDevice(final String rootPath, final String deviceName) {
	    return getUSBDevice(rootPath, deviceName, null);
    }

    static USBStorageDevice getUSBDevice(final String rootPath, final String deviceName, final String device) {
	    return getUSBDevice(rootPath, deviceName, device, null);
    }

    static USBStorageDevice getUSBDevice(final String rootPath, final String deviceName, final String device, final String uuid) {
        final File root = new File(rootPath);

        if (!root.isDirectory()) {
            // Sometimes commands returns an invalid directory
            log.trace("Invalid root found: {}", root);
            return null;
        }

        log.trace("Device found: {}", root.getPath());

        try {
            return new USBStorageDevice(root, deviceName, device, uuid);
        } catch (IllegalArgumentException e) {
            log.debug("Could not add Device: {}", e.getMessage(), e);
        }

        return null;
    }

}
