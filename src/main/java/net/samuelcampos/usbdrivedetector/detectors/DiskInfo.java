/*
 * Copyright 2017 samuelcampos.net
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


public class DiskInfo {
    private String device;
    private String mountPoint;
    private String name;
    private String uuid;
    private boolean isUSB;

    public DiskInfo(final String device) {
        this.device = device;
        this.mountPoint = "";
        this.name = "";
        this.uuid = "";
        this.isUSB = false;
    }

    public String getUUID() {
	return uuid;
    }

    public void setUUID(String uuid) {
	this.uuid = uuid;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getMountPoint() {
        return mountPoint;
    }

    public void setMountPoint(String mountPoint) {
        this.mountPoint = mountPoint;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isUSB() {
        return isUSB;
    }

    public void setUSB(boolean USB) {
        isUSB = USB;
    }
}
