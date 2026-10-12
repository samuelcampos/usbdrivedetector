package net.samuelcampos.usbdrivedetector.detectors;

import net.samuelcampos.usbdrivedetector.USBStorageDevice;
import net.samuelcampos.usbdrivedetector.process.CommandExecutor;
import net.samuelcampos.usbdrivedetector.process.InputStreamProcessor;
import net.samuelcampos.usbdrivedetector.utils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.Contains;

import javax.swing.filechooser.FileSystemView;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

public class WindowsStorageDeviceDetectorTest {

    CommandExecutor commandExecutor;
    WindowsStorageDeviceDetector victim;

    @BeforeEach
    public void beforeEach() {
        commandExecutor = mock(CommandExecutor.class);

        //System.setenv("WINDIR");
        victim = new WindowsStorageDeviceDetector(commandExecutor);
    }

    @Test
    public void testGetStorageDevicesWithoutRemovableDevices() throws IOException {
        // given
        when(commandExecutor.executeCommand(argThat(new Contains("Get-CIMInstance"))))
                .thenAnswer(ignore -> new InputStreamProcessor(TestUtils.getInputStreamClassPath("windows/powershell-empty.txt")));

        // when
        List<USBStorageDevice> devicesList = victim.getStorageDevices();

        // then
        assertTrue(devicesList.isEmpty());

        verify(commandExecutor, times(1)).executeCommand(WindowsStorageDeviceDetector.CMD_LOGICAL_DISK);
    }


    @Test
    public void testGetStorageDevices() throws IOException {
        // given
        String systemDrive = System.getenv("SystemDrive"); // The system drive is used to emulate a removable drive
        File systemDriveFile = new File(systemDrive + File.separatorChar);

        when(commandExecutor.executeCommand(argThat(new Contains("Get-CIMInstance"))))
                //.thenAnswer(ignore -> new InputStreamProcessor(TestUtils.getInputStreamClassPath("windows/wmic.txt")));
                // We have DeviceID and VolumeSerialNumber but an empty volume name
                .thenAnswer(ignore -> new InputStreamProcessor(new ByteArrayInputStream(
                        ("\"DeviceID\",\"VolumeName\",\"VolumeSerialNumber\"\n" +
                                "\"" + systemDrive + "\"," + "\"\",\"3029A30E\"").getBytes(StandardCharsets.UTF_8)
                )));

        // when
        List<USBStorageDevice> devicesList = victim.getStorageDevices();

        // then
        assertEquals(1, devicesList.size());
        assertEquals(systemDriveFile.getPath(), devicesList.get(0).getRootDirectory().getPath());
        // assert that in the case of an empty volume name, we fall back on the system display name.
        assertTrue(FileSystemView.getFileSystemView().getSystemDisplayName(systemDriveFile).startsWith(devicesList.get(0).getDeviceName()));
        assertEquals(systemDriveFile.getPath(), devicesList.get(0).getDevice());
        assertEquals("3029A30E", devicesList.get(0).getUuid());


        verify(commandExecutor, times(1)).executeCommand(WindowsStorageDeviceDetector.CMD_LOGICAL_DISK);
    }

    @Test
    public void testGetStorageDevicesWithVolumeName() throws IOException {
        String systemDrive = System.getenv("SystemDrive");

        when(commandExecutor.executeCommand(argThat(new Contains("Get-CIMInstance"))))
                .thenAnswer(ignore -> new InputStreamProcessor(new ByteArrayInputStream(
                        ("\"DeviceID\",\"VolumeName\",\"VolumeSerialNumber\"\n" +
                         "\"" + systemDrive + "\",\"My USB Drive\",\"3029A30E\"")
                                .getBytes(StandardCharsets.UTF_8)
                )));

        List<USBStorageDevice> devicesList = victim.getStorageDevices();

        assertEquals(1, devicesList.size());
        assertEquals("My USB Drive", devicesList.get(0).getDeviceName());
    }
    
    @Test
    public void testGetMultipleStorageDevices() throws IOException {
        String systemDrive = System.getenv("SystemDrive");
        when(commandExecutor.executeCommand(argThat(new Contains("Get-CIMInstance"))))
                .thenAnswer(ignore -> new InputStreamProcessor(new ByteArrayInputStream(
                        (
                        "\"DeviceID\",\"VolumeName\",\"VolumeSerialNumber\"\n" +
                                "\"" + systemDrive + "\",\"USB1\",\"1111\"\n" +
                                "\"" + systemDrive + "\",\"USB2\",\"2222\""
                        ).getBytes(StandardCharsets.UTF_8)
                )));

        List<USBStorageDevice> devicesList = victim.getStorageDevices();

        assertEquals(2, devicesList.size());

        assertEquals("USB1", devicesList.get(0).getDeviceName());
        assertEquals("1111", devicesList.get(0).getUuid());

        assertEquals("USB2", devicesList.get(1).getDeviceName());
        assertEquals("2222", devicesList.get(1).getUuid());
    }
    
    @Test
    public void testGetStorageDevicesWithCommaInVolumeName() throws IOException {
        String systemDrive = System.getenv("SystemDrive");

        when(commandExecutor.executeCommand(argThat(new Contains("Get-CIMInstance"))))
                .thenAnswer(ignore -> new InputStreamProcessor(new ByteArrayInputStream(
                        ("\"DeviceID\",\"VolumeName\",\"VolumeSerialNumber\"\n" +
                         "\"" + systemDrive + "\",\"Backup, Photos, Videos\",\"3029A30E\"")
                                .getBytes(StandardCharsets.UTF_8)
                )));

        List<USBStorageDevice> devicesList = victim.getStorageDevices();

        assertEquals(1, devicesList.size());
        assertEquals("Backup, Photos, Videos", devicesList.get(0).getDeviceName());
    }
    
    @Test
    public void testGetStorageDevicesWithDoubleQuotesInVolumeName() throws IOException {
        String systemDrive = System.getenv("SystemDrive");

        when(commandExecutor.executeCommand(argThat(new Contains("Get-CIMInstance"))))
                .thenAnswer(ignore -> new InputStreamProcessor(new ByteArrayInputStream(
                        ("\"DeviceID\",\"VolumeName\",\"VolumeSerialNumber\"\n" +
                         "\"" + systemDrive + "\",\"Bob's \"\"Important\"\" Backup\",\"3029A30E\"")
                                .getBytes(StandardCharsets.UTF_8)
                )));

        List<USBStorageDevice> devicesList = victim.getStorageDevices();

        assertEquals(1, devicesList.size());
        assertEquals("Bob's \"Important\" Backup", devicesList.get(0).getDeviceName());
        assertEquals("3029A30E", devicesList.get(0).getUuid());
    }    
}
