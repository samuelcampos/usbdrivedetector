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
package net.samuelcampos.usbdrivedectector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import net.samuelcampos.usbdrivedectector.detectors.AbstractStorageDeviceDetector;
import net.samuelcampos.usbdrivedectector.events.DeviceEventType;
import net.samuelcampos.usbdrivedectector.events.IUSBDriveListener;
import net.samuelcampos.usbdrivedectector.events.USBStorageEvent;
import org.apache.log4j.Logger;

/**
 * @author samuelcampos
 */
public class USBDeviceDetectorManager {

    private static final Logger logger = Logger
            .getLogger(USBDeviceDetectorManager.class);

    private static final long defaultPoolingInterval = 10 * 1000;

    private Set<USBStorageDevice> connectedDevices;
    private List<IUSBDriveListener> listeners;
    private Timer timer;

    public USBDeviceDetectorManager() {
        this(defaultPoolingInterval);
    }

    public USBDeviceDetectorManager(long poolingInterval) {
        listeners = new ArrayList<IUSBDriveListener>();

        connectedDevices = new HashSet<USBStorageDevice>();

        timer = new Timer();
        timer.scheduleAtFixedRate(new ListenerTask(), poolingInterval, poolingInterval);
    }

    public synchronized boolean addDriveListener(IUSBDriveListener listener) {
        if (listeners.contains(listener)) {
            return false;
        }

        listeners.add(listener);

        return true;
    }

    public synchronized boolean removeDriveListener(IUSBDriveListener listener) {
        return listeners.remove(listener);
    }

    public List<USBStorageDevice> getRemovableDevices() {
        return AbstractStorageDeviceDetector.getInstance().getRemovableDevices();
    }

    private void updateState(List<USBStorageDevice> actualConnectedDevices) {
        USBStorageEvent event;

        synchronized (this) {
            Iterator<USBStorageDevice> itConnectedDevices = connectedDevices.iterator();

            while (itConnectedDevices.hasNext()) {
                USBStorageDevice device = itConnectedDevices.next();

                if (!actualConnectedDevices.contains(device)) {
                    event = new USBStorageEvent(device, DeviceEventType.REMOVED);
                    sendEventToListeners(event);

                    itConnectedDevices.remove();
                } else {
                    actualConnectedDevices.remove(device);
                }
            }

            connectedDevices.addAll(actualConnectedDevices);
        }

        for (USBStorageDevice dev : actualConnectedDevices) {
            event = new USBStorageEvent(dev, DeviceEventType.CONNECTED);
            sendEventToListeners(event);
        }
    }

    private void sendEventToListeners(USBStorageEvent event) {
        for (IUSBDriveListener listener : listeners) {
            listener.usbDriveEvent(event);
        }
    }

    private class ListenerTask extends TimerTask {

        @Override
        public void run() {
            try {
                logger.trace("Pooling refresh task is running");

                List<USBStorageDevice> actualConnectedDevices = AbstractStorageDeviceDetector.getInstance().getRemovableDevices();

                updateState(actualConnectedDevices);
            } catch (Exception e) {
                logger.error("Error while refreshing device list", e);
            }
        }

    }
}
