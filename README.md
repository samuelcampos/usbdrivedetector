# USB Drive Detector

[![version][maven-version]][maven-url]

A Java library to get a list of all usb storage devices connected to the computer and has the capability of unmount them. It works on the three main operating systems (Windows, Linux and OS X).

### Maven dependency

To include this library in your project, add the following on your `pom.xml`:

```xml
<project>
    <dependencies>
        <!-- New dependency -->
        <dependency>
            <groupId>net.samuelcampos</groupId>
            <artifactId>usbdrivedetector</artifactId>
            <version>2.2.0</version>
        </dependency>
    </dependencies>
    
    <repositories>
        <!-- New Maven repo -->
        <repository>
            <id>github</id>
            <url>https://maven.pkg.github.com/samuelcampos/usbdrivedetector</url>
        </repository>
    </repositories>
</project>
```

### Usage examples

```java
USBDeviceDetectorManager driveDetector = new USBDeviceDetectorManager();

// Display all the USB storage devices currently connected
driveDetector.getRemovableDevices().forEach(System.out::println);

// Add an event listener to be notified when an USB storage device is connected or removed
driveDetector.addDriveListener(System.out::println);

// Unmount a device
driveDetector.unmountStorageDevice(driveDetector.getRemovableDevices().get(0));
```

Once you invoke `addDriveListener`, your application keep running because it will internally create an `ScheduledExecutorService`.
To finish your application, just invoke the `close` method;

```java
    // Shutdown an initialized USBDeviceDetectorManager
    driveDetector.close();
```

[maven-url]: https://search.maven.org/artifact/net.samuelcampos/usbdrivedetector/
[maven-version]: https://img.shields.io/maven-central/v/net.samuelcampos/usbdrivedetector.svg?style=flat
