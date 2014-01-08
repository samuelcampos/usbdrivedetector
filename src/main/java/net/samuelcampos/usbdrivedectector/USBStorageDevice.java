package net.samuelcampos.usbdrivedectector;

import java.io.File;

/**
 * Class to represent a USB Storage Device connected on the computer
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

    /**
     * 
     * @return the name of the USB storage device
     */
	public String getDeviceName() {
		return deviceName;
	}

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + (this.rootDirectory != null ? this.rootDirectory.hashCode() : 0);
        hash = 89 * hash + (this.deviceName != null ? this.deviceName.hashCode() : 0);
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
        return this.rootDirectory == other.rootDirectory || (this.rootDirectory != null && this.rootDirectory.equals(other.rootDirectory));
    }

    

	@Override
	public String toString() {
		return "RemovableDevice [Root=" + rootDirectory + ", Device Name=" + deviceName
				+ "]";
	}	
}
