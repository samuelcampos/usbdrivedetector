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

import lombok.extern.slf4j.Slf4j;
import net.samuelcampos.usbdrivedetector.USBStorageDevice;
import net.samuelcampos.usbdrivedetector.process.CommandExecutor;

import java.io.IOException;

@Slf4j
public class OSXStorageDeviceUnmounter extends AbstractStorageDeviceUnmounter {

	@Override
	public void unmount(final USBStorageDevice usbStorageDevice) {
		String unmoundCommand = "diskutil unmountDisk " + usbStorageDevice.getDevice();

		try (CommandExecutor commandExecutor = new CommandExecutor(unmoundCommand)) {
			commandExecutor.processOutput(log::trace);

			log.debug("Device successfully unmount: {}", usbStorageDevice);
		} catch (IOException e) {
			log.error("Unable to unmount device: {}", usbStorageDevice, e);
		}
	}
}
