package org.fayda.gps;

import com.fazecast.jSerialComm.SerialPort;
import org.fayda.gps.constants.KnownGPSDevices.GPSDevice;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class COMPortDeviceMatcher {

    /**
     * Finds the COM port that matches a specific GPS device (VID and PID).
     *
     * @param device The GPS device to match.
     * @return The matching COM port, or null if no match is found.
     */
    public static SerialPort findMatchingCOMPort(GPSDevice device) {
        try {
            List<String[]> connectedDevices = getUSBDevices();

            SerialPort[] ports = SerialPort.getCommPorts();
            for (SerialPort port : ports) {
                for (String[] connectedDevice : connectedDevices) {
                    String vid = connectedDevice[0];
                    String pid = connectedDevice[1];

                    if (vid.equalsIgnoreCase(device.getVid()) && pid.equalsIgnoreCase(device.getPid())) {
                        System.out.printf("Matched Device: %s (VID: %s, PID: %s) on Port: %s%n",
                                device.getName(), device.getVid(), device.getPid(), port.getSystemPortName());
                        return port;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error while matching COM port:");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Retrieves a list of connected USB devices with their VID and PID.
     *
     * @return A list of VID/PID pairs for connected devices.
     */
    private static List<String[]> getUSBDevices() {
        List<String[]> devices = new ArrayList<>();
        try {
            Process process = Runtime.getRuntime().exec("wmic path Win32_PnPEntity get Name,DeviceID");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            boolean headerSkipped = false;

            while ((line = reader.readLine()) != null) {
                if (!headerSkipped) {
                    headerSkipped = true;
                    continue;
                }

                if (line.contains("VID_")) {
                    Optional<String> vid = extractValue(line, "VID_", 4);
                    Optional<String> pid = extractValue(line, "PID_", 4);

                    if (vid.isPresent() && pid.isPresent()) {
                        devices.add(new String[]{vid.get(), pid.get()});
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error retrieving USB devices:");
            e.printStackTrace();
        }
        return devices;
    }

    /**
     * Helper method to extract a specific value (e.g., VID or PID) from a line.
     *
     * @param line The line to parse.
     * @param key  The key to search for (e.g., "VID_" or "PID_").
     * @param length The length of the value to extract.
     * @return An Optional containing the extracted value if found.
     */
    private static Optional<String> extractValue(String line, String key, int length) {
        try {
            int startIndex = line.indexOf(key) + key.length();
            return Optional.of(line.substring(startIndex, startIndex + length));
        } catch (Exception e) {
            System.err.println("Error extracting value for key: " + key);
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
