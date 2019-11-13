/*
 * Copyright 2019 samuelcampos.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.samuelcampos.usbdrivedetector.unmounters;

import java.io.IOException;
import net.samuelcampos.usbdrivedetector.USBStorageDevice;
import net.samuelcampos.usbdrivedetector.utils.OSUtils;

public abstract class AbstractStorageDeviceUnmounter {

    /**
     * {@link AbstractStorageDeviceUnmounter} instance. <br/>
     * This instance is created (Thread-Safe) when the JVM loads the class.
     */
    private static AbstractStorageDeviceUnmounter instance;

    public static synchronized AbstractStorageDeviceUnmounter getInstance() {
		if (instance == null) {
			switch (OSUtils.getOsType()) {
				case WINDOWS:
					instance = new WindowsStorageDeviceUnmounter();
					break;

				case LINUX:
					instance = new LinuxStorageDeviceUnmounter();
					break;

				case MAC_OS:
					instance = new OSXStorageDeviceUnmounter();
					break;
			}
		}

		return instance;
    }

    public abstract void unmount(USBStorageDevice usbStorageDevice) throws IOException;

}
