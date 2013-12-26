package net.samuelcampos.usbdrivedectector;

import java.io.File;

/**
 * This class represents a USB Storage Device on the computer
 * 
 * @author samuelcampos
 */
public class USBStorageDevice {
	private File rootDirectory;
	private String deviceName;
	
	public USBStorageDevice(File rootDirectory, String deviceName){
		if(rootDirectory == null || !rootDirectory.isDirectory()){
			throw new IllegalArgumentException("Invalid root file!");
		}
		
		this.rootDirectory = rootDirectory;
		this.deviceName = deviceName;
	}

	public File getRootDirectory() {
		return rootDirectory;
	}

	public String getDeviceName() {
		return deviceName;
	}

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.rootDirectory != null ? this.rootDirectory.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final USBStorageDevice other = (USBStorageDevice) obj;
        if (this.rootDirectory != other.rootDirectory && (this.rootDirectory == null || !this.rootDirectory.equals(other.rootDirectory))) {
            return false;
        }
        return true;
    }

	@Override
	public String toString() {
		return "RemovableDevice [Root=" + rootDirectory + ", Device Name=" + deviceName
				+ "]";
	}	
}
