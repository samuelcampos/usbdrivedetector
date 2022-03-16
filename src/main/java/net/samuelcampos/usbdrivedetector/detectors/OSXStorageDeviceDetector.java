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
import net.samuelcampos.usbdrivedetector.process.OutputProcessor;
import net.samuelcampos.usbdrivedetector.utils.OSUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author samuelcampos
 */
@Slf4j
public class OSXStorageDeviceDetector extends AbstractStorageDeviceDetector {

    /**
     * system_profiler SPUSBDataType | grep "BSD Name:\|Mount Point:"
     */
    private static final String CMD_SYSTEM_PROFILER_USB = "system_profiler SPUSBDataType";
    private static final Pattern macOSXPattern_MOUNT = Pattern.compile("^.*Mount Point: (.+)$");

	private static final String CMD_DF = "df -l";
	private static final String CMD_DISKUTIL = "diskutil info ";

    private static final String DISK_PREFIX = "/dev/disk";

    private static final String INFO_MOUNTPOINT = "Mount Point";
    private static final String INFO_PROTOCOL = "Protocol";
    private static final String INFO_USB = "USB";
    private static final String INFO_NAME = "Volume Name";
    private static final String INFO_UUID = "Volume UUID";

    private static final int MACOSX_MOUNTAINLION = 8;

    private int macosVersion = -1;

    protected OSXStorageDeviceDetector(final CommandExecutor commandExecutor) {
        super(commandExecutor);

        final String version = OSUtils.getOsVersion();
        final String[] versionParts = version.split("\\.");

        if (versionParts.length > 1) {
        	try{
        		macosVersion = Integer.parseInt(versionParts[1]);
        	}
        	catch (NumberFormatException nfe) {
        		log.error(nfe.getMessage(), nfe);
        	}
        }

    }


    @Override
    public List<USBStorageDevice> getStorageDevices() {
        final ArrayList<USBStorageDevice> listDevices = new ArrayList<>();

        if (macosVersion >= MACOSX_MOUNTAINLION){
        	try (final OutputProcessor commandOutputProcessor = commandExecutor.executeCommand(CMD_DF)) {

				commandOutputProcessor.processOutput((String outputLine) -> {
					final String[] parts = outputLine.split("\\s");
					final String device = parts[0];

                    if (device.startsWith(DISK_PREFIX)) {
                    	final DiskInfo disk = getDiskInfo(device);

                    	if (disk.isUSB()) {
							getUSBDevice(disk.getMountPoint(), disk.getName(), disk.getDevice(), disk.getUuid())
									.ifPresent(listDevices::add);
                    	}
                    }

        		});

        	} catch (IOException e) {
        		log.error(e.getMessage(), e);
        	}
        }
        else{
        	try (final OutputProcessor commandOutputProcessor = commandExecutor.executeCommand(CMD_SYSTEM_PROFILER_USB)) {
				commandOutputProcessor.processOutput(outputLine -> {
        			final Matcher matcher = macOSXPattern_MOUNT.matcher(outputLine);

        			if (matcher.matches()) {
						getUSBDevice(matcher.group(1))
								.ifPresent(listDevices::add);
        			}
        		});

        	} catch (IOException e) {
        		log.error(e.getMessage(), e);
        	}
        }

        return listDevices;
    }


	private DiskInfo getDiskInfo(final String device) {

		final DiskInfo disk = new DiskInfo(device);
		final String command = CMD_DISKUTIL +  disk.getDevice();

		try (final OutputProcessor commandOutputProcessor = commandExecutor.executeCommand(command)) {

			commandOutputProcessor.processOutput(outputLine -> {

    			final String[] parts = outputLine.split(":");

    			if(parts.length > 1){
					switch (parts[0].trim()) {
						case INFO_MOUNTPOINT:
							disk.setMountPoint(parts[1].trim());
							break;

						case INFO_PROTOCOL:
							disk.setUSB(INFO_USB.equals(parts[1].trim()));
							break;

						case INFO_NAME:
							disk.setName(parts[1].trim());
							break;

						case INFO_UUID:
							disk.setUuid(parts[1].trim());
							break;
					}
    			}


    		});

    	} catch (IOException e) {
    		log.error(e.getMessage(), e);
    	}

		return disk;
	}
}
