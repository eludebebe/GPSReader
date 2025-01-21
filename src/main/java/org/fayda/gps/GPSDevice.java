package org.fayda.gps;


import com.fazecast.jSerialComm.SerialPort;

import java.io.InputStream;
import java.util.Scanner;

public class GPSDevice {

//   String[] gpsDeviceTage = new String[] {"GPS","Receiver","GNSS","u-blox"};
   String gpsDeviceTag="GPS";
    private SerialPort getConnectedGPSDevicePort(){
        System.out.println("GPS Device Handler, Checking Connected GPS Port for Device Tag:"+gpsDeviceTag);
        SerialPort[] ports = SerialPort.getCommPorts();
        for (int i = 0; i < ports.length; i++) {
            if(ports[i].getPortDescription()!=null){
                    if(ports[i].getPortDescription().toLowerCase().contains(gpsDeviceTag.toLowerCase())){
                        System.out.println("GPS Device Found On: "+ports[i].getSystemPortName() +", Name: "+ports[i].getPortDescription());
                        return ports[i];
                    }

            }
        }
        System.out.println("ERROR: GPS Device Port Not Found");

       return null;
    }

    public GPSDevice(String deviceTag){
        gpsDeviceTag=deviceTag;
    }

    public GPSDataDTO getCurrentLocation() {
        GPSDataDTO dto = new GPSDataDTO();
        if (getCurrentLocation(dto)) return dto;
        return null;
    }
    private   boolean getCurrentLocation(GPSDataDTO gpsDto){
        // List all available serial ports
        SerialPort serialPort = getConnectedGPSDevicePort();

        if(serialPort==null){
            return false;
        }

        serialPort.setBaudRate(9600); // Standard baud rate for GPS devices

        if (serialPort.openPort()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }


            // Read data from the port
            InputStream inputStream = serialPort.getInputStream();
            Scanner scanner = new Scanner(inputStream);

//        System.out.println("Listening for GPS data...");
            gpsDto.setDataValid(false);

            while (!gpsDto.isDataValid()) {
                if (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    System.out.println(line);
                    gpsDto.parseSentence(line);

                }
            }
            serialPort.closePort();
        } else {
            System.out.println("Failed to open port.");
            return false;
        }

        return gpsDto.isDataValid();
    }

    public static void main(String[] args) {
        GPSDevice gpsDevice = new GPSDevice("GPS");
        GPSDataDTO dto = new GPSDataDTO();
        for(int i=0;i<10;i++) {
            if (gpsDevice.getCurrentLocation(dto)) {
                System.out.println("Location: " + dto.getAltitude() + " " + dto.getLatitude() + " " + dto.getLongitude());
            }
        }
    }

}
