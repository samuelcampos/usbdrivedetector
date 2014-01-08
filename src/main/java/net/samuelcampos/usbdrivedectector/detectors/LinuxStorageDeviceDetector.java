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
 *
 * @author samuelcampos
 */
public class LinuxStorageDeviceDetector extends AbstractStorageDeviceDetector {
    
    private static final Logger logger = Logger
			.getLogger(LinuxStorageDeviceDetector.class);

    private static final String linuxDetectUSBCommand = "system_profiler SPUSBDataType";
    private static final Pattern linuxPattern = Pattern.compile("^.+Mount Point: (.+)$");
    
    public LinuxStorageDeviceDetector() {
        super();   
    }

    @Override
    public List<USBStorageDevice> getRemovableDevices() {
        ArrayList<USBStorageDevice> listDevices = new ArrayList<USBStorageDevice>();

		BufferedReader in = null;
		Process process = null;

		try {
			if (logger.isTraceEnabled()) {
				logger.trace("Running command: " + linuxDetectUSBCommand);
			}

            /**
             * system_profiler SPUSBDataType | grep "BSD Name:\|Mount Point:"
             */
			process = Runtime.getRuntime().exec(linuxDetectUSBCommand);

			in = new BufferedReader(new InputStreamReader(
					process.getInputStream()));

			String line;
			while ((line = in.readLine()) != null) {
                Matcher matcher = linuxPattern.matcher(line);
				
                if(matcher.matches()) {
                    File root = new File(matcher.group(1));
                    
                    if (logger.isTraceEnabled()) {
						logger.trace("Device found: " + root.getPath());
					}
                    
                    
                    USBStorageDevice device = new USBStorageDevice(root,
								fsView.getSystemDisplayName(root));
						listDevices.add(device);
                        
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

			if (process != null)
				process.destroy();
		}

		return listDevices;
    }
}
