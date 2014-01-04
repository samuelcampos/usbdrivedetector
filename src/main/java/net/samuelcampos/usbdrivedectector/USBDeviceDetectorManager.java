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

/**
 * @author samuelcampos
 */
public class USBDeviceDetectorManager {

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

    private void updateState(List<USBStorageDevice> actualConnectedDevices) {
        USBStorageEvent event;

        synchronized(this) {
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
        
        for(USBStorageDevice dev : actualConnectedDevices) {
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
            List<USBStorageDevice> actualConnectedDevices = AbstractStorageDeviceDetector.getInstance().getRemovableDevices();

            updateState(actualConnectedDevices);
        }

    }
}
