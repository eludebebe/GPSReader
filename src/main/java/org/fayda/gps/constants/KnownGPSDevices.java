package org.fayda.gps.constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Constants class to store the list of known GPS devices by name, VID, and PID.
 */
public final class KnownGPSDevices {

    /**
     * Inner class to represent a GPS device with its name, VID, and PID.
     */
    public static final class GPSDevice {
        private final String name;
        private final String vid;
        private final String pid;

        public GPSDevice(String name, String vid, String pid) {
            this.name = name;
            this.vid = vid;
            this.pid = pid;
        }

        public String getName() {
            return name;
        }

        public String getVid() {
            return vid;
        }

        public String getPid() {
            return pid;
        }

        @Override
        public String toString() {
            return "GPSDevice{" +
                    "name='" + name + '\'' +
                    ", vid='" + vid + '\'' +
                    ", pid='" + pid + '\'' +
                    '}';
        }
    }

    // Private constructor to prevent instantiation
    private KnownGPSDevices() {}

    /**
     * Returns a list of known GPS devices.
     *
     * @return List of GPSDevice objects representing known devices.
     */
    public static List<GPSDevice> getKnownDevices() {
        List<GPSDevice> knownDevices = new ArrayList<>();
        knownDevices.add(new GPSDevice("Prolific USB-to-Serial", "067B", "23A3")); // Example GPS Device
        knownDevices.add(new GPSDevice("Prolific Legacy Device", "067B", "2303")); // Another Device
        // Add more devices as needed
        knownDevices.add(new GPSDevice("u-blox GNSS Receiver", "1546", "01A7"));

        return Collections.unmodifiableList(knownDevices); // Return as unmodifiable list
    }
}
