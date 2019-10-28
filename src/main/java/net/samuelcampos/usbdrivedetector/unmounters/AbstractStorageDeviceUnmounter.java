/*
 * Copyright 2014 samuelcampos.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.samuelcampos.usbdrivedetector.unmounters;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.samuelcampos.usbdrivedetector.USBStorageDevice;

public abstract class AbstractStorageDeviceUnmounter {

    private static final String OSName = System.getProperty("os.name").toLowerCase();

    protected static final Logger logger = LoggerFactory.getLogger(AbstractStorageDeviceUnmounter.class);

    /**
     * {@link AbstractStorageDeviceUnmounter} instance. <br/>
     * This instance is created (Thread-Safe) when the JVM loads the class.
     */
    private static final AbstractStorageDeviceUnmounter instance;

    static {
	if (OSName.startsWith("win")) {
	    instance = new WindowsStorageDeviceUnmounter();
	} else if (OSName.startsWith("linux")) {
	    instance = new LinuxStorageDeviceUnmounter();
	} else if (OSName.startsWith("mac")) {
	    instance = new OSXStorageDeviceUnmounter();
	} else {
	    instance = null;
	}
    }

    public static AbstractStorageDeviceUnmounter getInstance() {
	if (instance == null) {
	    throw new UnsupportedOperationException("Your Operative System (" + OSName + ") is not supported!");
	}

	return instance;
    }

    public abstract void unmount(USBStorageDevice usbStorageDevice) throws IOException;

}
