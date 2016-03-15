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

import net.samuelcampos.usbdrivedectector.USBStorageDevice;
import net.samuelcampos.usbdrivedectector.process.CommandExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author samuelcampos
 */
public class OSXStorageDeviceDetector extends AbstractStorageDeviceDetector {

    private static final Logger logger = LoggerFactory.getLogger(OSXStorageDeviceDetector.class);

    /**
     * system_profiler SPUSBDataType | grep "BSD Name:\|Mount Point:"
     */
    private static final String CMD_SYSTEM_PROFILER_USB = "system_profiler SPUSBDataType";
    private static final Pattern macOSXPattern = Pattern.compile("^.*Mount Point: (.+)$");

    protected OSXStorageDeviceDetector() {
        super();
    }

    @Override
    public List<USBStorageDevice> getRemovableDevices() {
        ArrayList<USBStorageDevice> listDevices = new ArrayList<>();

        try (CommandExecutor commandExecutor = new CommandExecutor(CMD_SYSTEM_PROFILER_USB)){
            commandExecutor.processOutput((String outputLine) -> {
                Matcher matcher = macOSXPattern.matcher(outputLine);

                if (matcher.matches()) {
                    addUSBDevice(listDevices, matcher.group(1));
                }
            });

        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

        return listDevices;
    }
}
