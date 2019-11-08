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

import lombok.extern.slf4j.Slf4j;
import net.samuelcampos.usbdrivedetector.USBStorageDevice;
import net.samuelcampos.usbdrivedetector.process.CommandExecutor;

@Slf4j
public class LinuxStorageDeviceUnmounter extends AbstractStorageDeviceUnmounter {

	@Override
	public void unmount(USBStorageDevice usbStorageDevice) throws IOException {
		try {
			unmount("umount " + usbStorageDevice.getDevice());
		} catch (IOException e) {
			unmount("sudo umount " + usbStorageDevice.getDevice());
		}
	}

	private void unmount(String command) throws IOException {
		try (CommandExecutor commandExecutor = new CommandExecutor(command)) {
			// Some times you need to wait until it is fully unmounted
			Thread.sleep(1000L);
		} catch (InterruptedException e) {
			log.error("Unable to unmount device", e);
		}
	}

}
