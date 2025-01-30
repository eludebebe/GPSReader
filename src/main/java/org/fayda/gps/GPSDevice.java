package org.fayda.gps;

import com.fazecast.jSerialComm.SerialPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.List;
import java.util.Scanner;

public class GPSDevice {

    private static final Logger logger = LoggerFactory.getLogger(GPSDevice.class);

    final List<GPSDeviceConfig> deviceConfigs;

    private GPSDeviceConfig matchedConfig;

    private int totalSentences = 0;
    private int validSentences = 0;

    /**
     * Constructor: tries to find a matching device right away
     */
    public GPSDevice(List<GPSDeviceConfig> deviceConfigs) {
        this.deviceConfigs = deviceConfigs;
        this.matchedConfig = findMatchingConfig(deviceConfigs);

        if (this.matchedConfig == null) {
            logger.warn("No matching GPS device found on any COM port.");
        } else {
            logger.info("Matched GPS Device at construction: {}", matchedConfig.getGpsDevice());
        }
    }

    /**
     * Return true if a GPS device is matched & found
     */
    public boolean isDeviceFound() {
        return matchedConfig != null;
    }

    /**
     * Attempt to find a matching device config by enumerating COM ports
     */
    private GPSDeviceConfig findMatchingConfig(List<GPSDeviceConfig> deviceConfigs) {
        for (GPSDeviceConfig config : deviceConfigs) {
            SerialPort serialPort = COMPortDeviceMatcher.findMatchingCOMPort(config.getGpsDevice());
            if (serialPort != null) {
                logger.info("Found matching device: {} on port: {}",
                        config.getGpsDevice().getName(), serialPort.getSystemPortName());
                return config;
            }
        }
        return null;
    }

    /**
     * Tries to fetch the current location. If matchedConfig was null,
     * we re-check to see if the device is now plugged in.
     */
    public GPSDataDTO getCurrentLocation() {
        if (!isDeviceFound()) {
            logger.warn("No GPS device found previously. Attempting to re-match now...");
            this.matchedConfig = findMatchingConfig(this.deviceConfigs);

            if (!isDeviceFound()) {
                logger.error("Still no GPS device found. Cannot fetch location.");
                return null;
            } else {
                logger.info("Re-matched GPS Device: {}", matchedConfig.getGpsDevice());
            }
        }

        GPSDataDTO gpsData = new GPSDataDTO();
        boolean isDataFetched = fetchLocationData(gpsData);

        if (isDataFetched) {
            return gpsData;
        } else {
            logger.error("No valid GPS data captured.");
            return null;
        }
    }

    /**
     * Actually open the port, read data lines, parse, etc.
     */
    private boolean fetchLocationData(GPSDataDTO gpsDto) {
        SerialPort serialPort = COMPortDeviceMatcher.findMatchingCOMPort(matchedConfig.getGpsDevice());
        if (serialPort == null) {
            logger.error("No GPS device connected. Please check the connection and try again.");
            matchedConfig = null;
            return false;
        }

        serialPort.setBaudRate(matchedConfig.getBaudRate());
        if (!serialPort.openPort()) {
            logger.error("Failed to open port. Ensure no other application is using it.");
            matchedConfig = null;
            return false;
        }

        logger.info("Port opened successfully. Listening for GPS data...");
        boolean isDataFound = false;

        try (InputStream inputStream = serialPort.getInputStream();
             Scanner scanner = new Scanner(inputStream)) {

            gpsDto.setDataValid(false);

            Thread.sleep(matchedConfig.getStabilizationTimeMs());
            logger.info("Device stabilization complete.");

            long startTime = System.currentTimeMillis();

            while (scanner.hasNextLine()
                    && (System.currentTimeMillis() - startTime) < matchedConfig.getGpsDataFetchTimeoutMs()) {

                String line = scanner.nextLine().trim();
                totalSentences++;

                logger.debug("Raw GPS Sentence: {}", line);

                if (!line.startsWith("$")) {
                    continue; // Not an NMEA sentence
                }

                gpsDto.parseSentence(line);

                if (gpsDto.isDataValid()) {
                    validSentences++;
                    isDataFound = true;
                    break;
                } else {
                    logger.warn("Parsed sentence but data invalid: {}", line);
                }
            }

        } catch (Exception e) {
            logger.error("Exception while reading GPS data", e);
            matchedConfig = null;
            return false;
        } finally {
            serialPort.closePort();
            logger.info("Port closed.");
        }

        logFinalMetrics(gpsDto);
        return isDataFound;
    }

    private void logFinalMetrics(GPSDataDTO gpsDto) {
        logger.info("Final Metrics:");
        logger.info("  Latitude  : {}", gpsDto.getLatitude());
        logger.info("  Longitude : {}", gpsDto.getLongitude());
        logger.info("  Altitude  : {}", gpsDto.getAltitude());
        logger.info("  Data Valid: {}", gpsDto.isDataValid());
    }
}
