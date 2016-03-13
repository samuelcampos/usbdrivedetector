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

import net.samuelcampos.usbdrivedectector.detectors.AbstractStorageDeviceDetector;
import net.samuelcampos.usbdrivedectector.events.DeviceEventType;
import net.samuelcampos.usbdrivedectector.events.IUSBDriveListener;
import net.samuelcampos.usbdrivedectector.events.USBStorageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author samuelcampos
 */
public class USBDeviceDetectorManager {

    private static final Logger logger = LoggerFactory
            .getLogger(USBDeviceDetectorManager.class);

    private static final long DEFAULT_POLLING_INTERVAL = 10 * 1000;

    private long currentPollingInterval = DEFAULT_POLLING_INTERVAL;

    private Set<USBStorageDevice> connectedDevices;
    private final ArrayList<IUSBDriveListener> listeners;
    private Timer timer;

    /**
     * Creates a new USBDeviceDetectorManager setting the polling interval to
     * the default polling interval of 10 seconds.
     */
    public USBDeviceDetectorManager() {
        this(DEFAULT_POLLING_INTERVAL);
    }

    /**
     * Creates a new USBDeviceDetectorManager
     * <p>
     * The polling interval is used as the update frequency for any attached
     * listeners.
     * </p>
     * <p>
     * Polling doesn't happen until at least one listener is attached.
     * </p>
     *
     * @param pollingInterval the interval in milliseconds to poll for the USB
     * storage devices on the system.
     */
    public USBDeviceDetectorManager(long pollingInterval) {
        listeners = new ArrayList<IUSBDriveListener>();

        connectedDevices = new HashSet<USBStorageDevice>();

    }

    /**
     * Start polling to update listeners at the specified polling interval.
     *
     * @param pollingInterval the interval in milliseconds to poll for the USB
     * storage devices on the system.
     */
    public synchronized void start(long pollingInterval) {
        currentPollingInterval = pollingInterval;
        stop();
        start();
    }

    /**
     * Start polling to update listeners
     * <p>
     * This method only needs to be called if {@link #stop() stop()} has been
     * called after listeners have been added.
     * </p>
     */
    public synchronized void start() {
        if (timer != null) {
            timer = new Timer();
            timer.scheduleAtFixedRate(new ListenerTask(), 0, currentPollingInterval);
        }

    }

    /**
     * Forces the polling to stop, even if there are still listeners attached
     */
    public synchronized void stop() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    /**
     * Adds an IUSBDriveListener.
     * <p>
     * The polling timer is automatically started as needed when a listener is
     * added.
     * </p>
     *
     * @param listener the listener to be updated with the attached drives
     * @return true if the listener was not in the list and was successfully
     * added
     */
    public synchronized boolean addDriveListener(IUSBDriveListener listener) {
        if (listeners.contains(listener)) {
            return false;
        }

        listeners.add(listener);
        start();
        return true;
    }

    /**
     * Removes an IUSBDriveListener.
     * <p>
     * The polling timer is automatically stopped if this is the last listener
     * being removed.
     * </p>
     *
     * @param listener the listener to remove
     * @return true if the listener existed in the list and was successfully
     * removed
     */
    public synchronized boolean removeDriveListener(IUSBDriveListener listener) {
        boolean removed = listeners.remove(listener);
        if (listeners.isEmpty()) {
            stop();
        }

        return removed;
    }

    /**
     * Gets a list of currently attached USB storage devices.
     * <p>
     * This method has no effect on polling or listeners being updated
     * </p>
     *
     * @return list of attached USB storage devices.
     */
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
        /*
         Make this thread safe, so we deal with a copy of listeners so any 
         listeners being added or removed don't cause a ConcurrentModificationException.
         Also allows listeners to remove themselves while processing the event
         */
        ArrayList<IUSBDriveListener> listenersCopy;
        synchronized (listeners) {
            listenersCopy = (ArrayList<IUSBDriveListener>) listeners.clone();
        }
        for (IUSBDriveListener listener : listenersCopy) {
            try {
                listener.usbDriveEvent(event);
            } catch (Exception ex) {
                logger.error("An IUSBDriveListener threw an exception", ex);
            }
        }
    }

    private class ListenerTask extends TimerTask {

        @Override
        public void run() {
            try {
                logger.trace("Polling refresh task is running");

                List<USBStorageDevice> actualConnectedDevices = AbstractStorageDeviceDetector.getInstance().getRemovableDevices();

                updateState(actualConnectedDevices);
            } catch (Exception e) {
                logger.error("Error while refreshing device list", e);
            }
        }

    }
}
