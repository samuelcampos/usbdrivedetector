package net.samuelcampos.usbdrivedectector.detectors;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.samuelcampos.usbdrivedectector.USBStorageDevice;
import org.apache.log4j.Logger;

/**
 * Tested on Linux Ubuntu 13.10
 *
 * @author samuelcampos
 */
public class LinuxStorageDeviceDetector extends AbstractStorageDeviceDetector {

    private static final Logger logger = Logger
            .getLogger(LinuxStorageDeviceDetector.class);

    private static final String linuxDetectUSBCommand1 = "df";
    private static final Pattern command1Pattern = Pattern.compile("^(\\/[^ ]+)[^%]+%[ ]+(.+)$");
    private static final String linuxDetectUSBCommand2 = "udevadm info -q property -n ";
    private static final String strDeviceVerifier = "ID_USB_DRIVER=usb-storage";

    public LinuxStorageDeviceDetector() {
        super();
    }

    private boolean isUSBStorage(String device) {
        String verifyCommand = linuxDetectUSBCommand2 + device;
        
        if (logger.isTraceEnabled()) {
            logger.trace("Running command: " + verifyCommand);
        }

        BufferedReader in = null;
        Process process = null;

        try {
            process = Runtime.getRuntime().exec(verifyCommand);

            in = new BufferedReader(new InputStreamReader(
                    process.getInputStream()));

            String line;
            while ((line = in.readLine()) != null) {
                if(line.equals(strDeviceVerifier))
                    return true;
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }

            if (process != null) {
                process.destroy();
            }
        }

        return false;
    }

    @Override
    public List<USBStorageDevice> getRemovableDevices() {
        ArrayList<USBStorageDevice> listDevices = new ArrayList<USBStorageDevice>();

        BufferedReader in = null;
        Process process = null;

        try {
            if (logger.isTraceEnabled()) {
                logger.trace("Running command: " + linuxDetectUSBCommand1);
            }

            process = Runtime.getRuntime().exec(linuxDetectUSBCommand1);

            in = new BufferedReader(new InputStreamReader(
                    process.getInputStream()));

            String line;
            while ((line = in.readLine()) != null) {
                Matcher matcher = command1Pattern.matcher(line);

                if (matcher.matches()) {
                    String device = matcher.group(1);
                    String rootPath = matcher.group(2);

                    if(isUSBStorage(device)) {
                        File root = new File(rootPath);

                         if (logger.isTraceEnabled()) {
                         logger.trace("Device found: " + root.getPath());
                         }


                         USBStorageDevice usbDevice = new USBStorageDevice(root,
                         fsView.getSystemDisplayName(root));
                         listDevices.add(usbDevice);
                    }
                }
            }

        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }

            if (process != null) {
                process.destroy();
            }
        }

        return listDevices;
    }
}
