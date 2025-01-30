package org.fayda.gps;

import org.fayda.gps.constants.KnownGPSDevices.GPSDevice;

/**
 * Configuration class for GPS device.
 */
public class GPSDeviceConfig {

    private final GPSDevice gpsDevice;
    private final int baudRate;
    private final int stabilizationTimeMs;
    private final int gpsDataFetchTimeoutMs;

    /**
     * Constructor for GPSDeviceConfig.
     *
     * @param gpsDevice             The GPS device (VID, PID, and name).
     * @param baudRate              The baud rate for communication.
     * @param stabilizationTimeMs   Device stabilization time in milliseconds.
     * @param gpsDataFetchTimeoutMs GPS data fetch timeout in milliseconds.
     */
    public GPSDeviceConfig(GPSDevice gpsDevice, int baudRate, int stabilizationTimeMs, int gpsDataFetchTimeoutMs) {
        if (gpsDevice == null) {
            throw new IllegalArgumentException("GPSDevice cannot be null");
        }
        if (baudRate <= 0) {
            throw new IllegalArgumentException("Baud rate must be greater than 0");
        }
        if (stabilizationTimeMs < 0) {
            throw new IllegalArgumentException("Stabilization time cannot be negative");
        }
        if (gpsDataFetchTimeoutMs <= 0) {
            throw new IllegalArgumentException("GPS data fetch timeout must be greater than 0");
        }

        this.gpsDevice = gpsDevice;
        this.baudRate = baudRate;
        this.stabilizationTimeMs = stabilizationTimeMs;
        this.gpsDataFetchTimeoutMs = gpsDataFetchTimeoutMs;
    }

    public GPSDevice getGpsDevice() {
        return gpsDevice;
    }

    public int getBaudRate() {
        return baudRate;
    }

    public int getStabilizationTimeMs() {
        return stabilizationTimeMs;
    }

    public int getGpsDataFetchTimeoutMs() {
        return gpsDataFetchTimeoutMs;
    }

    @Override
    public String toString() {
        return "GPSDeviceConfig{" +
                "gpsDevice=" + gpsDevice +
                ", baudRate=" + baudRate +
                ", stabilizationTimeMs=" + stabilizationTimeMs +
                ", gpsDataFetchTimeoutMs=" + gpsDataFetchTimeoutMs +
                '}';
    }
}
