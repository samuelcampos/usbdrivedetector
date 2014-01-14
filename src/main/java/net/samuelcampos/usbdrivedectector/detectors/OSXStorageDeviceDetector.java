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
 *
 * @author samuelcampos
 */
public class OSXStorageDeviceDetector extends AbstractStorageDeviceDetector {

    private static final Logger logger = Logger
            .getLogger(OSXStorageDeviceDetector.class);

    private static final String osXDetectUSBCommand = "system_profiler SPUSBDataType";
    private static final Pattern macOSXPattern = Pattern.compile("^.*Mount Point: (.+)$");

    private final CommandLineExecutor commandExecutor;

    public OSXStorageDeviceDetector() {
        super();

        commandExecutor = new CommandLineExecutor();
    }

    @Override
    public List<USBStorageDevice> getRemovableDevices() {
        ArrayList<USBStorageDevice> listDevices = new ArrayList<USBStorageDevice>();

        try {
            /**
             * system_profiler SPUSBDataType | grep "BSD Name:\|Mount Point:"
             */
            commandExecutor.executeCommand(osXDetectUSBCommand);

            String outputLine;
            
            while ((outputLine = commandExecutor.readOutputLine()) != null) {
                Matcher matcher = macOSXPattern.matcher(outputLine);

                if (matcher.matches()) {
                    addUSBDevice(listDevices, matcher.group(1));
                }
            }

        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            try {
                commandExecutor.close();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }

        return listDevices;
    }
}
