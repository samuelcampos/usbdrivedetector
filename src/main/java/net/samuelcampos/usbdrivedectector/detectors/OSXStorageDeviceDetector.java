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
package net.samuelcampos.usbdrivedectector.detectors;

import net.samuelcampos.usbdrivedectector.USBStorageDevice;
import net.samuelcampos.usbdrivedectector.process.CommandExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author samuelcampos
 */
public class OSXStorageDeviceDetector extends AbstractStorageDeviceDetector {

    private static final Logger logger = LoggerFactory.getLogger(OSXStorageDeviceDetector.class);

    /**
     * system_profiler SPUSBDataType | grep "BSD Name:\|Mount Point:"
     */
    private static final String CMD_SYSTEM_PROFILER_USB = "system_profiler SPUSBDataType";
    private static final Pattern macOSXPattern_MOUNT = Pattern.compile("^.*Mount Point: (.+)$");
    
    private static final String CMD_DF = "df -l";
    private static final String CMD_DISKUTIL = "diskutil info ";
    
    private static final String DISK_PREFIX = "/dev/disk";
   
    private static final String INFO_MOUNTPOINT = "Mount Point";
    private static final String INFO_PROTOCOL = "Protocol";
    private static final String INFO_USB = "USB";
    private static final String INFO_NAME = "Volume Name";
    
    private static final int MACOS_SIERRA = 12;
    private static final int MACOS_ELCAPITAN = 11;
    private static final int MACOSX_MOUNTAINLION = 8;
    
    private int macosVersion;
    
    

    protected OSXStorageDeviceDetector() {
        super();
        
        String version = System.getProperty("os.version");
        String[] versionParts = version.split("\\.");
        if(versionParts.length > 1){
        	try{
        		macosVersion = Integer.parseInt(versionParts[1]);
        	}
        	catch(NumberFormatException nfe){
        		logger.error(nfe.getMessage(), nfe);
        	}
        }
    
    }

    
    @Override
    public List<USBStorageDevice> getStorageDevicesDevices() {
        final ArrayList<USBStorageDevice> listDevices = new ArrayList<>();
        
        if(macosVersion >= MACOSX_MOUNTAINLION){
        	try (CommandExecutor commandExecutor = new CommandExecutor(CMD_DF)) {
        		
        		commandExecutor.processOutput((String outputLine) -> {
                    String[] parts = outputLine.split("\\s");
                    String device = parts[0];

                    if(device.startsWith(DISK_PREFIX)){
                    	DiskInfo disk = new DiskInfo(device);
                    	readDiskInfo(disk);
                    	
                    	if(disk.isUSB){
                    		listDevices.add(new USBStorageDevice(new File(disk.mountPoint), disk.name));
                    	}
                    }
        			
        		});

        	} catch (IOException e) {
        		logger.error(e.getMessage(), e);
        	}
        }
        else{
        	try (CommandExecutor commandExecutor = new CommandExecutor(CMD_SYSTEM_PROFILER_USB)) {
        		commandExecutor.processOutput(outputLine -> {
        			final Matcher matcher = macOSXPattern_MOUNT.matcher(outputLine);

        			if (matcher.matches()) {
        				listDevices.add(getUSBDevice(matcher.group(1)));
        			}
        		});

        	} catch (IOException e) {
        		logger.error(e.getMessage(), e);
        	}
        }

        return listDevices;
    }


	private void readDiskInfo(DiskInfo disk) {
		// TODO Auto-generated method stub
		
		String command = CMD_DISKUTIL +  disk.device;

		try (CommandExecutor commandExecutor = new CommandExecutor(command)) {
			
    		commandExecutor.processOutput(outputLine -> {
    			
    			String[] parts = outputLine.split(":");
    			
    			if(parts.length > 1){
    				if(INFO_MOUNTPOINT.equals(parts[0].trim())){
    					disk.mountPoint = parts[1].trim();
    				}
    				else if(INFO_PROTOCOL.equals(parts[0].trim())){
    					disk.isUSB = INFO_USB.equals(parts[1].trim());
    				}
    				else if(INFO_NAME.equals(parts[0].trim())){
    					disk.name = parts[1].trim();
    				}
    			}
    			
    			
    		});

    	} catch (IOException e) {
    		logger.error(e.getMessage(), e);
    	}
		
	}
	
	private class DiskInfo{
		
		public DiskInfo(String device){
			this.device = device;
			mountPoint = "";
			name = "";
			isUSB = false;
		}
		
		String device;
		String mountPoint;
		String name;
		boolean isUSB;
		
	}
}
