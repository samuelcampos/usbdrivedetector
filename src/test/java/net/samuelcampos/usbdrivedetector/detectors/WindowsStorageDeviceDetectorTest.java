package net.samuelcampos.usbdrivedetector.detectors;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.ptr.IntByReference;
import net.samuelcampos.usbdrivedetector.USBStorageDevice;
import net.samuelcampos.usbdrivedetector.process.CommandExecutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class WindowsStorageDeviceDetectorTest {

    private static final int SEM_FAILCRITICALERRORS = 0x0001;

    Kernel32 kernel32;
    WindowsStorageDeviceDetector victim;

    // A real root (e.g. "C:\" on Windows, "/" elsewhere) is used to emulate a removable drive,
    // because USBStorageDevice requires the root directory to exist.
    String rootPath;

    @BeforeEach
    public void beforeEach() {
        kernel32 = mock(Kernel32.class);
        victim = new WindowsStorageDeviceDetector(mock(CommandExecutor.class), kernel32);
        rootPath = File.listRoots()[0].getAbsolutePath();
    }

    @Test
    public void testGetStorageDevicesWithoutRemovableDevices() {
        // given
        when(kernel32.GetDriveType(anyString())).thenReturn(WinBase.DRIVE_FIXED);

        // when
        List<USBStorageDevice> devicesList = victim.getStorageDevices();

        // then
        assertTrue(devicesList.isEmpty());

        verify(kernel32, never()).GetVolumeInformation(anyString(), any(), anyInt(), any(), any(), any(), any(), anyInt());
    }

    @Test
    public void testGetStorageDevices() {
        // given
        when(kernel32.GetDriveType(rootPath)).thenReturn(WinBase.DRIVE_REMOVABLE);
        mockVolumeInformation("MY USB", 0x3029A30E);

        // when
        List<USBStorageDevice> devicesList = victim.getStorageDevices();

        // then
        assertEquals(1, devicesList.size());
        assertEquals(new File(rootPath).getPath(), devicesList.get(0).getRootDirectory().getPath());
        assertEquals("MY USB", devicesList.get(0).getDeviceName());
        assertEquals(rootPath, devicesList.get(0).getDevice());
        assertEquals("3029A30E", devicesList.get(0).getUuid());
    }

    @Test
    public void testGetStorageDevicesWhenVolumeInformationFails() {
        // given
        when(kernel32.GetDriveType(rootPath)).thenReturn(WinBase.DRIVE_REMOVABLE);
        when(kernel32.GetVolumeInformation(eq(rootPath), any(), anyInt(), any(), any(), any(), any(), anyInt()))
                .thenReturn(false);

        // when
        List<USBStorageDevice> devicesList = victim.getStorageDevices();

        // then
        assertTrue(devicesList.isEmpty());
    }

    @Test
    public void testGetStorageDevicesSuppressesAndRestoresCriticalErrorDialogs() {
        // given
        final int previousErrorMode = 0x8000;
        when(kernel32.SetErrorMode(SEM_FAILCRITICALERRORS)).thenReturn(previousErrorMode);
        when(kernel32.GetDriveType(anyString())).thenReturn(WinBase.DRIVE_FIXED);

        // when
        victim.getStorageDevices();

        // then
        verify(kernel32).SetErrorMode(previousErrorMode | SEM_FAILCRITICALERRORS);
        verify(kernel32).SetErrorMode(previousErrorMode);
    }

    private void mockVolumeInformation(final String volumeName, final int serialNumber) {
        when(kernel32.GetVolumeInformation(eq(rootPath), any(), anyInt(), any(), any(), any(), any(), anyInt()))
                .thenAnswer(invocation -> {
                    final char[] volumeNameBuffer = invocation.getArgument(1);
                    System.arraycopy(volumeName.toCharArray(), 0, volumeNameBuffer, 0, volumeName.length());
                    invocation.<IntByReference>getArgument(3).setValue(serialNumber);
                    return true;
                });
    }

}
