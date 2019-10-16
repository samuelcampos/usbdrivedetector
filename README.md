# USB Drive Detector

[![Build Status][travis-image]][travis-url]  |
[![version][maven-version]][maven-url]

A Java library to get a list of all usb storage devices connected to the computer. It works on the three main operating systems (Windows, Linux and OS X).

### Maven dependency

To include this library in your project just use:
```xml
<dependency>
    <groupId>net.samuelcampos</groupId>
    <artifactId>usbdrivedetector</artifactId>
    <version>2.0.4</version>
</dependency>
```

### Usage examples

```java
USBDeviceDetectorManager driveDetector = new USBDeviceDetectorManager();

// Display all the USB storage devices currently connected
driveDetector.getRemovableDevices().forEach(System.out::println);

// Add an event listener to be notified when an USB storage device is connected or removed
driveDetector.addDriveListener(System.out::println);
```

[travis-url]: https://travis-ci.org/samuelcampos/usbdrivedetector
[travis-image]: https://travis-ci.org/samuelcampos/usbdrivedetector.svg?branch=master

[maven-url]: https://search.maven.org/#artifactdetails%7Cnet.samuelcampos%7Cusbdrivedetector%7C2.0.4%7C
[maven-version]: https://img.shields.io/maven-central/v/net.samuelcampos/usbdrivedetector.svg?style=flat

