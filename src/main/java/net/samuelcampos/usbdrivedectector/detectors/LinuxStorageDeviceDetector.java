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
package net.samuelcampos.usbdrivedectector.detectors;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.samuelcampos.usbdrivedectector.USBStorageDevice;
import net.samuelcampos.usbdrivedectector.process.CommandLineExecutor;
import org.apache.log4j.Logger;

/**
 * Tested on Linux Ubuntu 13.10
 *
 * @author samuelcampos
 */
public class LinuxStorageDeviceDetector extends AbstractStorageDeviceDetector {

    private static final Logger logger = Logger
            .getLogger(LinuxStorageDeviceDetector.class);

    private static final String linuxDetectUSBCommand1 = "df";
    private static final Pattern command1Pattern = Pattern.compile("^(\\/[^ ]+)[^%]+%[ ]+(.+)$");
    private static final String linuxDetectUSBCommand2 = "udevadm info -q property -n ";
    private static final String strDeviceVerifier = "ID_USB_DRIVER=usb-storage";

    private final CommandLineExecutor commandExecutor1, commandExecutor2;

    public LinuxStorageDeviceDetector() {
        super();

        commandExecutor1 = new CommandLineExecutor();
        commandExecutor2 = new CommandLineExecutor();
    }

    private boolean isUSBStorage(String device) {
        String verifyCommand = linuxDetectUSBCommand2 + device;

        try {
            commandExecutor2.executeCommand(verifyCommand);

            String outputLine;
            while ((outputLine = commandExecutor2.readOutputLine()) != null) {
                if (strDeviceVerifier.equals(outputLine)) {
                    return true;
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            try {
                commandExecutor2.close();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }

        return false;
    }

    @Override
    public List<USBStorageDevice> getRemovableDevices() {
        ArrayList<USBStorageDevice> listDevices = new ArrayList<USBStorageDevice>();

        try {
            commandExecutor1.executeCommand(linuxDetectUSBCommand1);

            String outputLine;
            while ((outputLine = commandExecutor1.readOutputLine()) != null) {
                Matcher matcher = command1Pattern.matcher(outputLine);

                if (matcher.matches()) {
                    String device = matcher.group(1);
                    String rootPath = matcher.group(2);

                    if (isUSBStorage(device)) {
                        addUSBDevice(listDevices, rootPath);
                    }
                }
            }

        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            try {
                commandExecutor1.close();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }

        return listDevices;
    }
}
