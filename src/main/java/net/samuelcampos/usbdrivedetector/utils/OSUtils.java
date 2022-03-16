/*
 * Copyright 2019 samuelcampos.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.samuelcampos.usbdrivedetector.utils;

import lombok.experimental.UtilityClass;

import static net.samuelcampos.usbdrivedetector.utils.OSType.*;

@UtilityClass
public final class OSUtils {

    public static OSType getOsType() {
        String osNameLower = System.getProperty("os.name").toLowerCase();

        if (osNameLower.startsWith("win")) {
            return WINDOWS;
        }

        if (osNameLower.startsWith("linux")) {
            return LINUX;
        }

        if (osNameLower.startsWith("mac")) {
            return MAC_OS;
        }

        throw new UnsupportedOperationException("Your Operative System (" + osNameLower + ") is not supported!");
    }

    public String getOsVersion() {
        return System.getProperty("os.version");
    }
}
