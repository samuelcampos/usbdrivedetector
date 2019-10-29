package net.samuelcampos.usbdrivedetector.unmounters;

import java.io.IOException;
import com.github.tuupertunut.powershelllibjava.PowerShell;
import com.github.tuupertunut.powershelllibjava.PowerShellExecutionException;
import net.samuelcampos.usbdrivedetector.USBStorageDevice;

public class WindowsStorageDeviceUnmounter extends AbstractStorageDeviceUnmounter {
    private static final String CMD_UNMOUNT_1 = "$ErrorActionPreference = 'SilentlyContinue'";
    private static final String CMD_UNMOUNT_2 = "(New-Object -comObject Shell.Application).Namespace(17).ParseName('X:').InvokeVerb('Eject')";

    @Override
    public void unmount(USBStorageDevice usbStorageDevice) throws IOException {
		try (PowerShell psSession = PowerShell.open()) {
			psSession.executeCommands(CMD_UNMOUNT_1, CMD_UNMOUNT_2.replace("X:", usbStorageDevice.getDevice()));
			// Some times you need to wait until it is fully unmounted
			Thread.sleep(1000L);
		} catch (PowerShellExecutionException | InterruptedException e) {
			throw new IOException(e);
		}
    }
}
