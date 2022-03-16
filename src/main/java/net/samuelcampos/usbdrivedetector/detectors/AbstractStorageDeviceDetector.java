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

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.samuelcampos.usbdrivedetector.USBStorageDevice;
import net.samuelcampos.usbdrivedetector.process.CommandExecutor;
import net.samuelcampos.usbdrivedetector.utils.OSUtils;

import java.io.File;
import java.util.List;
import java.util.Optional;

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
@Getter
@RequiredArgsConstructor
public abstract class AbstractStorageDeviceDetector {

    /**
     * {@link AbstractStorageDeviceDetector} instance. <br/>
     * This instance is created (Thread-Safe) when the JVM loads the class.
     */
    private static AbstractStorageDeviceDetector instance;

    public static synchronized AbstractStorageDeviceDetector getInstance() {
        if (instance == null) {
            final CommandExecutor commandExecutor = new CommandExecutor();

            switch (OSUtils.getOsType()) {
                case WINDOWS:
                    instance = new WindowsStorageDeviceDetector(commandExecutor);
                    break;

                case LINUX:
                    instance = new LinuxStorageDeviceDetector(commandExecutor);
                    break;

                case MAC_OS:
                    instance = new OSXStorageDeviceDetector(commandExecutor);
                    break;
            }
        }

        return instance;
    }

    protected final CommandExecutor commandExecutor;

    /**
     * Returns the all storage devices currently connected to the computer.
     *
     * @return the list of the USB storage devices
     */
    public abstract List<USBStorageDevice> getStorageDevices();

    static Optional<USBStorageDevice> getUSBDevice(final String rootPath) {
        return getUSBDevice(rootPath, null, null, null);
    }

    static Optional<USBStorageDevice> getUSBDevice(final String rootPath, final String deviceName, final String device, final String uuid) {
        final File root = new File(rootPath);

        if (!root.isDirectory()) {
            // When a device has recently disconnected, the command may still return the old root directory of the recently removed device
            log.trace("Invalid root found: {}", root);
            return Optional.empty();
        }

        log.trace("Device found: {}", root.getPath());

        try {
            return Optional.of(new USBStorageDevice(root, deviceName, device, uuid));
        } catch (IllegalArgumentException e) {
            log.debug("Could not add Device: {}", e.getMessage(), e);
        }

        return Optional.empty();
    }

}
