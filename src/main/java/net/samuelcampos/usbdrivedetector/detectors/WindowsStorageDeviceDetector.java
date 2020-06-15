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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.filechooser.FileSystemView;

import lombok.extern.slf4j.Slf4j;
import net.samuelcampos.usbdrivedetector.USBStorageDevice;
import net.samuelcampos.usbdrivedetector.process.CommandExecutor;

/**
 *
 * @author samuelcampos
 */
@Slf4j
public class WindowsStorageDeviceDetector extends AbstractStorageDeviceDetector {

    private static final String WMIC_PATH = System.getenv("WINDIR") + "\\System32\\wbem\\wmic.exe";

    /**
     * wmic logicaldisk where drivetype=2 get description,deviceid,volumename
     */
    private static final String CMD_WMI_ARGS = "logicaldisk where drivetype=2 get DeviceID,VolumeSerialNumber,VolumeName";
    private static final String CMD_WMI_USB = WMIC_PATH + " " + CMD_WMI_ARGS;

    protected WindowsStorageDeviceDetector() {
        super();
    }

    @Override
    public List<USBStorageDevice> getStorageDevices() {
        final ArrayList<USBStorageDevice> listDevices = new ArrayList<>();

        try (CommandExecutor commandExecutor = new CommandExecutor(CMD_WMI_USB)) {
            commandExecutor.processOutput(outputLine -> {

        	final String[] parts = outputLine.split(" ");

                if(parts.length > 1 && !parts[0].isEmpty() && !parts[0].equals("DeviceID") && !parts[0].equals(parts[parts.length - 1])) {
                	final String rootPath = parts[0] + File.separatorChar;
                    final String uuid = parts[parts.length - 1];
                    String volumeName = parseVolumeName(parts);
                    if (volumeName.isEmpty())
                    {
                    	// if the volume label via WMIC is blank, lets fall-back on 
                    	// FileSystemView.getSystemDisplayName(File) to derive a user-friendly
                    	// localised generic name (eg. 'USB Drive')
                    	volumeName = getDeviceName(rootPath);
                    }
                    
                    getUSBDevice(rootPath, volumeName, rootPath, uuid)
                            .ifPresent(listDevices::add);
                }
            });

        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }

        return listDevices;
    }

    private String parseVolumeName(String[] parts) {
    	String volumeLabel = "";
    	// in our array of strings, the device ID will be at first position
    	// the volume serial number will be in the final position, and all 
    	// other elements in the array will either be empty string (multiple 
    	// occurrences), or the volume label (zero or one occurrence)
    	for (int i = 1; i < parts.length-1; i++) {
			if (!parts[i].isEmpty())
			{
				volumeLabel = parts[i];
			}
		}
    	
    	// note: if there is NO label on the volume then none will be found in 
    	// the array and empty string will be returned
		return volumeLabel;
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
}
