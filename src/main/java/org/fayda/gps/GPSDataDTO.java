package org.fayda.gps;

/**
 * DTO class for parsing and holding GPS data.
 */
public class GPSDataDTO {

    private String latitude;
    private String longitude;
    private String altitude;
    private boolean dataValid;


    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getAltitude() {
        return altitude;
    }

    public boolean isDataValid() {
        return dataValid;
    }

    // Setters
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setAltitude(String altitude) {
        this.altitude = altitude;
    }

    public void setDataValid(boolean dataValid) {
        this.dataValid = dataValid;
    }

    /**
     * Parses an NMEA sentence and updates the DTO fields.
     *
     * @param sentence The raw NMEA sentence to parse.
     */
    public void parseSentence(String sentence) {
        if (sentence == null || sentence.isEmpty() || !sentence.startsWith("$")) {
            System.err.println("[ERROR] Invalid GPS sentence: " + sentence);
            return;
        }

        String[] parts = sentence.split(",");
        switch (parts[0]) {
            case "$GPGGA":
            case "$GNGGA": // GLONASS equivalent
                parseGPGGA(parts);
                break;
            default:
//                System.out.println("[INFO] Skipping unsupported sentence type: " + parts[0]);
        }
    }

    /**
     * Parses the GPGGA (Global Positioning System Fix Data) NMEA sentence.
     *
     * @param parts The split parts of the GPGGA sentence.
     */
    private void parseGPGGA(String[] parts) {
        try {
            if (parts.length > 9) {
                latitude = parseCoordinate(parts[2], parts[3]); // Latitude and direction (N/S)
                longitude = parseCoordinate(parts[4], parts[5]); // Longitude and direction (E/W)
                altitude = parts[9]; // Altitude in meters

                String fixStatus = parts[6]; // Fix status (0, 1, 2)
                String satellites = parts[7]; // Number of satellites in use
                System.out.println("[INFO] Fix Status: " + fixStatus + ", Satellites: " + satellites);

                dataValid = "1".equals(fixStatus) && latitude != null && longitude != null && altitude != null && !altitude.isEmpty();
            } else {
                System.err.println("[ERROR] Incomplete GPGGA sentence: " + String.join(",", parts));
                dataValid = false;
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Exception parsing GPGGA: " + e.getMessage());
            dataValid = false;
        }
    }


    /**
     * Converts NMEA coordinates into decimal degrees.
     *
     * @param coordinate The raw NMEA coordinate (e.g., "4807.038").
     * @param direction  The direction (N/S/E/W).
     * @return The converted coordinate in decimal degrees or null if invalid.
     */
    String parseCoordinate(String coordinate, String direction) {
        if (coordinate == null || direction == null || coordinate.isEmpty()) {
            return null;
        }
        try {
            int degreesLength = (direction.equalsIgnoreCase("N") || direction.equalsIgnoreCase("S")) ? 2 : 3;
            double degrees = Double.parseDouble(coordinate.substring(0, degreesLength));
            double minutes = Double.parseDouble(coordinate.substring(degreesLength)) / 60.0;
            double decimalDegrees = degrees + minutes;

            if (direction.equalsIgnoreCase("S") || direction.equalsIgnoreCase("W")) {
                decimalDegrees *= -1;
            }
            return String.format("%.6f", decimalDegrees);
        } catch (Exception e) {
            System.err.println("[ERROR] Error parsing coordinate: " + coordinate + ", Direction: " + direction);
            return null;
        }
    }

    @Override
    public String toString() {
        return "GPSDataDTO {" +
                "latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", altitude='" + altitude + '\'' +
                ", dataValid=" + dataValid +
                '}';
    }
}
