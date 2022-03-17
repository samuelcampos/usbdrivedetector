## Version [ERROR] Failed to execute goal org.codehaus.mojo:build-helper-maven-plugin:3.3.0:regex-property (default-cli) on project usbdrivedetector: The parameters 'regex', 'name', 'value' for goal org.codehaus.mojo:build-helper-maven-plugin:3.3.0:regex-property are missing or invalid -> [Help 1]
[ERROR] 
[ERROR] To see the full stack trace of the errors, re-run Maven with the -e switch.
[ERROR] Re-run Maven using the -X switch to enable full debug logging.
[ERROR] 
[ERROR] For more information about the errors and possible solutions, please read the following articles:
[ERROR] [Help 1] http://cwiki.apache.org/confluence/display/MAVEN/PluginParameterException

- Update release job
- Fix release job
- Add tests (#39)
- Update GitHub actions
- Update dependencies + prepare release
- fix unmount for most recent macOS versions
- Update Windows device detector code to derive the volume label via th… (#31)
- Use a USBDeviceDetectorManager to manager the listener task. Fixes #29 (#34)
- Fix typo #28
- Fix version in README.md
- Updated version in pom.xml and README.md
- Fix unmount method in Linux and OSX to always try with or without sudo.
- Fix unmount method in Linux and OSX to always try with or without sudo.
- Start new iteration
- Release 2.1.1
- - Fix SimpleTest unmount (check if collections is empty to avoid outofbounds exception);
- Fix minor concurrency issue; Enhance error logs
- Reduce pooling watch interval and refactor some code
- Prepare next iteration cycle




