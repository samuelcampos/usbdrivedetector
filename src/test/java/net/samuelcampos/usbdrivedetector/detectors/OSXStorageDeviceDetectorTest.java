package net.samuelcampos.usbdrivedetector.detectors;

import net.samuelcampos.usbdrivedetector.USBStorageDevice;
import net.samuelcampos.usbdrivedetector.process.CommandExecutor;
import net.samuelcampos.usbdrivedetector.process.InputStreamProcessor;
import net.samuelcampos.usbdrivedetector.utils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.StartsWith;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

public class OSXStorageDeviceDetectorTest {

    CommandExecutor commandExecutor;
    OSXStorageDeviceDetector victim;

    @BeforeEach
    public void beforeEach() {
        commandExecutor = mock(CommandExecutor.class);

        System.setProperty("os.version", "10.16.7");
        victim = new OSXStorageDeviceDetector(commandExecutor);
    }

    @Test
    public void testGetStorageDevicesWithoutRemovableDevices() throws IOException {
        // given
        when(commandExecutor.executeCommand("df -l"))
                .thenAnswer(ignore -> new InputStreamProcessor(TestUtils.getInputStreamClassPath("macos/df-l.txt")));

        when(commandExecutor.executeCommand(argThat(new StartsWith("diskutil info "))))
                .thenAnswer(ignore -> new InputStreamProcessor(TestUtils.getInputStreamClassPath("macos/diskutil-non-usb.txt")));

        // when
        List<USBStorageDevice> devicesList = victim.getStorageDevices();

        // then
        assertTrue(devicesList.isEmpty());

        verify(commandExecutor, times(1)).executeCommand("df -l");
        verify(commandExecutor, times(1)).executeCommand("diskutil info /dev/disk1s5");
        verify(commandExecutor, times(1)).executeCommand("diskutil info /dev/disk1s1");
        verify(commandExecutor, times(1)).executeCommand("diskutil info /dev/disk1s4");
    }
}