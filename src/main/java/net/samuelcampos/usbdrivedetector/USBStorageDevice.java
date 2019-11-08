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
package net.samuelcampos.usbdrivedetector;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.File;
import javax.swing.filechooser.FileSystemView;

/**
 * Class to represent a USB Storage Device connected on the computer
 *
 * @author samuelcampos
 */
@Getter
@ToString
@EqualsAndHashCode
public class USBStorageDevice {
	private final File rootDirectory;
	private final String deviceName;
	private final String device;
	private final String uuid;

	public USBStorageDevice(final File rootDirectory, String deviceName, final String device, final String uuid) {
		if (rootDirectory == null || !rootDirectory.isDirectory()) {
			throw new IllegalArgumentException("Invalid root file!");
		}

		this.rootDirectory = rootDirectory;

		if (deviceName == null || deviceName.isEmpty()) {
			deviceName = rootDirectory.getName();
		}
		this.device = device;
		this.deviceName = deviceName;
		this.uuid = uuid;
	}

	/**
	 * Check if it is possible to read in this device.
	 *
	 * @see File#canRead()
	 *
	 * @return <b>true</b> if it is possible to perform read operations in this
	 *         device, <b>false</b> otherwise.
	 */
	public boolean canRead() {
		return rootDirectory.canRead();
	}

	/**
	 * Check if it is possible to write in this device.
	 *
	 * @see File#canWrite()
	 *
	 * @return <b>true</b> if it is possible to perform write operations in this
	 *         device, <b>false</b> otherwise.
	 */
	public boolean canWrite() {
		return rootDirectory.canWrite();
	}

	/**
	 * Check if the actual user has execute permissions in this drive.
	 *
	 * @see File#canWrite()
	 *
	 * @return <b>true</b> if it is possible to perform execute operations in this
	 *         device, <b>false</b> otherwise.
	 */
	public boolean canExecute() {
		return rootDirectory.canExecute();
	}

	/**
	 * @see FileSystemView#getSystemDisplayName(java.io.File)
	 *
	 * @return the name of the root of this device as it would be displayed in a
	 *         system file browser.
	 */
	public String getSystemDisplayName() {
		return FileSystemView.getFileSystemView().getSystemDisplayName(rootDirectory);
	}
}
