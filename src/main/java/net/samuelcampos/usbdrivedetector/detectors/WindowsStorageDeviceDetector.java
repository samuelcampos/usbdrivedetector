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

import com.sun.jna.Native;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.ptr.IntByReference;
import lombok.extern.slf4j.Slf4j;
import net.samuelcampos.usbdrivedetector.USBStorageDevice;
import net.samuelcampos.usbdrivedetector.process.CommandExecutor;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Windows storage device detector using JNA Win32 APIs.
 *
 * @author samuelcampos
 */
@Slf4j
public class WindowsStorageDeviceDetector extends AbstractStorageDeviceDetector {

    /**
     * Stops Windows from showing a "There is no disk in the drive" dialog when querying an empty removable drive
     * (e.g. a card reader without a card). Not defined by JNA.
     */
    private static final int SEM_FAILCRITICALERRORS = 0x0001;

    private final Kernel32 kernel32;

    protected WindowsStorageDeviceDetector(final CommandExecutor commandExecutor, final Kernel32 kernel32) {
        super(commandExecutor);
        this.kernel32 = kernel32;
    }

    @Override
    public List<USBStorageDevice> getStorageDevices() {
        final ArrayList<USBStorageDevice> listDevices = new ArrayList<>();

        // SetErrorMode returns the previous mode, so the flag is added to it rather than replacing it.
        final int previousErrorMode = kernel32.SetErrorMode(SEM_FAILCRITICALERRORS);
        kernel32.SetErrorMode(previousErrorMode | SEM_FAILCRITICALERRORS);
        try {
            final File[] drives = File.listRoots();
            log.debug("Found {} root drives to check", drives.length);

            for (File drive : drives) {
                final String drivePath = drive.getAbsolutePath();
                try {
                    getStorageDevice(drivePath).ifPresent(listDevices::add);
                } catch (Exception e) {
                    log.warn("Error processing drive {}", drivePath, e);
                }
            }
        } finally {
            kernel32.SetErrorMode(previousErrorMode);
        }

        log.debug("Found {} removable storage devices", listDevices.size());
        return listDevices;
    }

    private Optional<USBStorageDevice> getStorageDevice(final String drivePath) {
        final int driveType = kernel32.GetDriveType(drivePath);
        log.trace("Drive {} has type: {}", drivePath, driveType);

        if (driveType != WinBase.DRIVE_REMOVABLE) {
            return Optional.empty();
        }

        final char[] volumeNameBuffer = new char[WinDef.MAX_PATH + 1];
        final IntByReference serialNumber = new IntByReference();
        final IntByReference maximumComponentLength = new IntByReference();
        final IntByReference fileSystemFlags = new IntByReference();
        final char[] fileSystemNameBuffer = new char[WinDef.MAX_PATH + 1];

        final boolean success = kernel32.GetVolumeInformation(
                drivePath,
                volumeNameBuffer, volumeNameBuffer.length,
                serialNumber,
                maximumComponentLength,
                fileSystemFlags,
                fileSystemNameBuffer, fileSystemNameBuffer.length);

        if (!success) {
            log.debug("GetVolumeInformation failed for {}, error code: {}", drivePath, Native.getLastError());
            return Optional.empty();
        }

        String volumeName = Native.toString(volumeNameBuffer).trim();
        final String serial = String.format("%08X", serialNumber.getValue());

        // If the volume label is blank, fall back to a localised generic name (e.g. 'USB Drive').
        if (volumeName.isEmpty()) {
            volumeName = getDeviceName(drivePath);
        }

        log.debug("Drive {}: volumeName='{}', serialNumber='{}'", drivePath, volumeName, serial);

        return getUSBDevice(drivePath, volumeName, drivePath, serial);
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
