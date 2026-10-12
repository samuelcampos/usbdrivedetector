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

    /*
     * The powershell command returns the connected USB drives in csv format.
     * 
     * e.g.
     * 
     * "DeviceID","VolumeName","VolumeSerialNumber"
     * "D:","SD CARD","3ED06668"
     * "E:","SD CARD2","3ED06669"
     */
    static final String CMD_LOGICAL_DISK = "Get-CIMInstance -query 'select * from Win32_LogicalDisk where drivetype=2' | "
            + "Select-Object DeviceID, VolumeName, VolumeSerialNumber | " + "ConvertTo-Csv -NoTypeInformation";

    protected WindowsStorageDeviceDetector(final CommandExecutor commandExecutor) {
        super(commandExecutor);
    }

    @Override
    public List<USBStorageDevice> getStorageDevices() {
        final ArrayList<USBStorageDevice> listDevices = new ArrayList<>();

        try (final OutputProcessor commandOutputProcessor = commandExecutor.executeCommand(CMD_LOGICAL_DISK)) {
            commandOutputProcessor.processOutput(outputLine -> {

            // Split on "\",\"" and not just "," so that lines with extra comma in volume name don't split.
        	final String[] parts = outputLine.split("\",\"");

                if (parts.length > 1 && !parts[0].isEmpty() && !isPartOfHeader(parts) && !parts[0].equals(parts[parts.length - 1])) {
                    
                    String deviceId = unquoteCsvField(parts[0]);
                    String volumeName = unquoteCsvField(parts[1]);
                    final String uuid = unquoteCsvField(parts[parts.length - 1]);

                    final String rootPath = deviceId + File.separatorChar;
 
                    if (volumeName.isEmpty())
                    {
                    	// if the volume label is blank, lets fall-back on
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

    /**
     * Remove the double quotes from the csv field allowing for the edge case where
     * the field name itsself contains double quotes.
     *
     * e.g. 
     * 
     *  "My USB Drive" -> My USB Drive 
     * 
     *  ""Bob's" USB Drive" -> "Bob's" USB Drive
     * 
     * @param field the field to unquote
     * @return the unquoted field
     */
    private static String unquoteCsvField(String field) {
        String result = field;

        if (result.startsWith("\"")) 
        {
            result = result.substring(1);
        }

        if (result.endsWith("\"")) 
        {
            result = result.substring(0, result.length() - 1);
        }

        // CSV escaped quote
        result = result.replace("\"\"", "\"");

        return result;
    }

    private boolean isPartOfHeader(String[] parts) {
        return parts[0].startsWith("\"DeviceID\"");
    }

	private String getDeviceName(final String rootPath) {
        final File f = new File(rootPath);
        final FileSystemView v = FileSystemView.getFileSystemView();
        String name = v.getSystemDisplayName(f);

        // Remove the " (E:)" from the name
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
