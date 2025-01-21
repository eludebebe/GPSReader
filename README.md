Usage Sample


GPSDevice gpsDevice = new GPSDevice("GPS"); //Give some tag name of device name for easy port discovery
GPSDataDTO dto = gpsDevice.getCurrentLocation();

for(dto!=null) {
  //dto.getLatitude();
  //dto.getLongitude();
}
