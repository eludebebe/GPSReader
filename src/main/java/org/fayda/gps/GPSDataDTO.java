package org.fayda.gps;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO class for parsing and holding crucial GPS data from NMEA sentences.
 */
public class GPSDataDTO {

    /** Crucial GPS Data Fields */
    private String latitude; // Latitude in NMEA format (e.g., 0854.51704,N)
    private String longitude; // Longitude in NMEA format (e.g., 03844.10915,E)
    private String altitude; // Altitude in meters as a string
    private String timeUTC; // UTC time in HHMMSS.SS format
    private boolean dataValid; // Whether the position data is valid
    private String fixType; // Type of fix (0 = No fix, 1 = 2D fix, 2 = 3D fix)
    private String speed; // Speed over ground in knots
    private String course; // Course over ground in degrees

    // Setters with conditional assignment
    public void setLatitude(String latitude) {
        if (this.latitude == null) {
            this.latitude = latitude;
        }
    }

    public void setLongitude(String longitude) {
        if (this.longitude == null) {
            this.longitude = longitude;
        }
    }

    public void setAltitude(String altitude) {
        if (this.altitude == null) {
            this.altitude = altitude;
        }
    }

    public void setTimeUTC(String timeUTC) {
        if (this.timeUTC == null) {
            this.timeUTC = timeUTC;
        }
    }

    public void setDataValid(boolean dataValid) {
        if (!this.dataValid) {
            this.dataValid = dataValid;
        }
    }

    public void setFixType(String fixType) {
        if (this.fixType == null) {
            this.fixType = fixType;
        }
    }

    public void setSpeed(String speed) {
        if (this.speed == null) {
            this.speed = speed;
        }
    }

    public void setCourse(String course) {
        if (this.course == null) {
            this.course = course;
        }
    }

    // Getters to retrieve the values of fields
    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getAltitude() {
        return altitude;
    }

    public String getTimeUTC() {
        return timeUTC;
    }


    public String getFixType() {
        return fixType;
    }

    public String getSpeed() {
        return speed;
    }

    public String getCourse() {
        return course;
    }

    /**
     * Parses a $GPGLL sentence and populates latitude, longitude, and validity fields.
     */
    private void parseGPGLL(String sentence) {
        String[] parts = sentence.split(",");
        setLatitude(parts[1] + "," + parts[2]);
        setLongitude(parts[3] + "," + parts[4]);
        setTimeUTC(parts[5]);
        setDataValid("A".equals(parts[6]));
    }

    /**
     * Parses a $GPGSA sentence to get the fix type.
     */
    private void parseGPGSA(String sentence) {
        String[] parts = sentence.split(",");
        setFixType(parts[2]);
    }

    /**
     * Parses a $GPGGA sentence to get altitude and other fields if needed.
     */
    private void parseGPGGA(String sentence) {
        String[] parts = sentence.split(",");
        setTimeUTC(parts[1]);
        setLatitude(parts[2] + "," + parts[3]);
        setLongitude(parts[4] + "," + parts[5]);
        setAltitude(parts[9]);
    }

    /**
     * Parses a $GPRMC sentence to extract speed, course, latitude, longitude, and time.
     */
    private void parseGPRMC(String sentence) {
        String[] parts = sentence.split(",");
        setTimeUTC(parts[1]);
        setDataValid("A".equals(parts[2]));
        setLatitude(parts[3] + "," + parts[4]);
        setLongitude(parts[5] + "," + parts[6]);
        setSpeed(parts[7]);
        setCourse(parts[8]);
    }

    public void parseSentence(String sentence){
//        System.out.println("Parsing: "+sentence);
        if (sentence.startsWith("$GPRMC")) {
            parseGPRMC(sentence);
        } else if (sentence.startsWith("$GPGGA")) {
            parseGPGGA(sentence);
        } else if (sentence.startsWith("$GPGSA")) {
            parseGPGSA(sentence);
        }
    }

    public boolean isDataValid() {

        if (dataValid) {
            if (altitude != null && latitude != null && fixType != null && !fixType.equalsIgnoreCase("0")) {
                return true;
            }
        }
        return false;
    }
}
