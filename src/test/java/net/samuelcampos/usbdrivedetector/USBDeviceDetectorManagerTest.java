package net.samuelcampos.usbdrivedetector;

import net.samuelcampos.usbdrivedetector.events.IUSBDriveListener;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class USBDeviceDetectorManagerTest {

    public USBDeviceDetectorManager detectorManager;

    @BeforeEach
    public void beforeEach() {
        detectorManager = new USBDeviceDetectorManager();
    }

    @AfterEach
    public void afterEach() throws Exception {
        detectorManager.close();
    }

    @Test
    public void checkListenerRegistration() {
        IUSBDriveListener listener = event -> {
            throw new UnsupportedOperationException("Unexpected invocation");
        };

        assertTrue(detectorManager.addDriveListener(listener), "Must successfully register listener for the first time");
        assertFalse(detectorManager.addDriveListener(listener), "Can't register same listener twice");

        assertTrue(detectorManager.removeDriveListener(listener), "Must successfully deregister listener");
        assertFalse(detectorManager.removeDriveListener(listener), "Can't deregister same listener twice");
    }
}