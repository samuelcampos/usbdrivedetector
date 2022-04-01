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
        victim = new WindowsStorageDeviceDetector("C:\\WINDOWS", commandExecutor);
    }

    @Test
    public void testGetStorageDevicesWithoutRemovableDevices() throws IOException {
        // given
        when(commandExecutor.executeCommand(argThat(new Contains("wmic"))))
                .thenAnswer(ignore -> new InputStreamProcessor(TestUtils.getInputStreamClassPath("windows/wmic-empty.txt")));

        // when
        List<USBStorageDevice> devicesList = victim.getStorageDevices();

        // then
        assertTrue(devicesList.isEmpty());

        verify(commandExecutor, times(1)).executeCommand("C:\\WINDOWS\\System32\\wbem\\wmic.exe logicaldisk where drivetype=2 get DeviceID,VolumeSerialNumber,VolumeName");
    }


    @Test
    public void testGetStorageDevices() throws IOException {
        // given
        String systemDrive = System.getenv("SystemDrive"); // The system drive is used to emulate a removable drive
        File systemDriveFile = new File(systemDrive + File.separatorChar);

        when(commandExecutor.executeCommand(argThat(new Contains("wmic"))))
                //.thenAnswer(ignore -> new InputStreamProcessor(TestUtils.getInputStreamClassPath("windows/wmic.txt")));
                .thenAnswer(ignore -> new InputStreamProcessor(new ByteArrayInputStream(
                        ("DeviceID  VolumeName  VolumeSerialNumber\n" +
                        systemDrive + "                    3029A30E").getBytes(StandardCharsets.UTF_8)
                )));

        // when
        List<USBStorageDevice> devicesList = victim.getStorageDevices();

        // then
        assertEquals(1, devicesList.size());
        assertEquals(systemDriveFile.getPath(), devicesList.get(0).getRootDirectory().getPath());
        assertTrue(FileSystemView.getFileSystemView().getSystemDisplayName(systemDriveFile).startsWith(devicesList.get(0).getDeviceName()));
        assertEquals(systemDriveFile.getPath(), devicesList.get(0).getDevice());
        assertEquals("3029A30E", devicesList.get(0).getUuid());


        verify(commandExecutor, times(1)).executeCommand("C:\\WINDOWS\\System32\\wbem\\wmic.exe logicaldisk where drivetype=2 get DeviceID,VolumeSerialNumber,VolumeName");
    }

}