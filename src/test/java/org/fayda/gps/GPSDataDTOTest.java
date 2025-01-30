package org.fayda.gps;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GPSDataDTOTest {

    @Test
    void testParseValidGPGGA() {
        GPSDataDTO gpsData = new GPSDataDTO();
        String validGPGGA = "$GPGGA,123519,4807.038,N,01131.000,E,1,08,0.9,545.4,M,46.9,M,,*47";

        gpsData.parseSentence(validGPGGA);

        assertTrue(gpsData.isDataValid(), "Data should be valid for a correct GPGGA sentence");
        assertEquals("48.117300", gpsData.getLatitude(), "Latitude should be correctly parsed");
        assertEquals("11.516667", gpsData.getLongitude(), "Longitude should be correctly parsed");
        assertEquals("545.4", gpsData.getAltitude(), "Altitude should match the GPGGA sentence");
    }

    @Test
    void testParseInvalidGPGGA() {
        GPSDataDTO gpsData = new GPSDataDTO();
        String invalidGPGGA = "$GPGGA,invalid,data,,,,";

        gpsData.parseSentence(invalidGPGGA);

        assertFalse(gpsData.isDataValid(), "Data should be invalid for incomplete GPGGA sentence");
    }

    @Test
    void testCoordinateConversion() {
        GPSDataDTO gpsData = new GPSDataDTO();
        String coordinate = "4807.038";
        String direction = "N";

        String parsedCoordinate = gpsData.parseCoordinate(coordinate, direction);

        assertEquals("48.117300", parsedCoordinate, "Coordinate conversion should be correct");
    }

    @Test
    void testToStringMethod() {
        GPSDataDTO gpsData = new GPSDataDTO();
        gpsData.setLatitude("48.117300");
        gpsData.setLongitude("11.516667");
        gpsData.setAltitude("545.4");
        gpsData.setDataValid(true);

        String expected = "GPSDataDTO {latitude='48.117300', longitude='11.516667', altitude='545.4', dataValid=true}";
        assertEquals(expected, gpsData.toString(), "toString() output should match the expected format");
    }
}
