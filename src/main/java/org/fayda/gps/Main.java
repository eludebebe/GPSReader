package org.fayda.gps;

import com.fazecast.jSerialComm.SerialPort;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.fayda.gps.constants.KnownGPSDevices;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main extends Application {

    private TableView<DeviceRow> deviceTable;
    private TextArea logArea;
    private Label statusLabel;
    private Label latitudeLabel;
    private Label longitudeLabel;
    private Label altitudeLabel;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("GPS Device Manager");

         VBox root = new VBox(10);
        root.setPadding(new Insets(10));

         statusLabel = new Label("No device selected.");
        statusLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

         deviceTable = new TableView<>();
        configureDeviceTable();

         VBox locationBox = new VBox(5);
        locationBox.setPadding(new Insets(10));
        locationBox.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1; -fx-border-radius: 5;");

        latitudeLabel = new Label("Latitude: --");
        longitudeLabel = new Label("Longitude: --");
        altitudeLabel = new Label("Altitude: --");

        locationBox.getChildren().addAll(new Label("Location Data:"), latitudeLabel, longitudeLabel, altitudeLabel);

         logArea = new TextArea();
        logArea.setEditable(false);
        logArea.setPromptText("Logs will appear here...");

         Button refreshButton = new Button("Refresh Devices");
        Button connectButton = new Button("Connect");
        Button disconnectButton = new Button("Disconnect");

         refreshButton.setOnAction(e -> refreshDevices());
        connectButton.setOnAction(e -> connectToDevice());
        disconnectButton.setOnAction(e -> disconnectDevice());

         HBox buttonBox = new HBox(10, refreshButton, connectButton, disconnectButton);

         root.getChildren().addAll(
                statusLabel,
                deviceTable,
                locationBox,
                buttonBox,
                new Label("Logs:"),
                logArea
        );

        primaryStage.setScene(new Scene(root, 600, 500));
        primaryStage.show();

        // Initial Device Refresh
        refreshDevices();
    }

    /**
     * Configures the device table with columns.
     */
    private void configureDeviceTable() {
        TableColumn<DeviceRow, String> portNameColumn = new TableColumn<>("Port Name");
        portNameColumn.setCellValueFactory(data -> data.getValue().portNameProperty());

        TableColumn<DeviceRow, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(data -> data.getValue().descriptionProperty());

        TableColumn<DeviceRow, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(data -> data.getValue().statusProperty());

        deviceTable.getColumns().addAll(portNameColumn, descriptionColumn, statusColumn);
    }

    /**
     * Refreshes the list of connected devices.
     */
    private void refreshDevices() {
        log("Refreshing connected devices...");

        // Fetch devices from GPSDevice (mocked here)
        List<DeviceRow> devices = fetchDevices();

        deviceTable.getItems().clear();
        deviceTable.getItems().addAll(devices);

        log("Devices refreshed.");
    }

    /**
     * Connect to a selected device and fetch location.
     */
    private void connectToDevice() {
        DeviceRow selectedDevice = deviceTable.getSelectionModel().getSelectedItem();
        if (selectedDevice == null) {
            log("No device selected for connection.");
            return;
        }

        log("Attempting to connect to device: " + selectedDevice.getPortName());

        try {
            // Replace with actual GPSDevice logic
            SerialPort serialPort = findPortByName(selectedDevice.getPortName());
            if (serialPort != null && serialPort.openPort()) {
                // Successfully opened the port
                selectedDevice.setStatus("Connected");
                statusLabel.setText("Connected to: " + selectedDevice.getDescription());
                log("Successfully connected to device: " + selectedDevice.getDescription());

                // Fetch location data
                List<GPSDeviceConfig> deviceConfigs = Arrays.asList(
                        new GPSDeviceConfig(
                                new KnownGPSDevices.GPSDevice("Prolific USB-to-Serial", "067B", "23A3"),
                                4800,
                                10000,
                                60000
                        ),
                        new GPSDeviceConfig(
                                new KnownGPSDevices.GPSDevice("u-blox GNSS Receiver", "1546", "01A7"),
                                9600,
                                15000,
                                30000
                        )
                );

                GPSDevice gpsDevice = new GPSDevice(deviceConfigs);
                GPSDataDTO location = gpsDevice.getCurrentLocation();

                if (location != null && location.isDataValid()) {
                    latitudeLabel.setText("Latitude: " + location.getLatitude());
                    longitudeLabel.setText("Longitude: " + location.getLongitude());
                    altitudeLabel.setText("Altitude: " + location.getAltitude());
                    log("Location data retrieved successfully.");
                } else {
                    log("Failed to retrieve valid location data.");
                }

                serialPort.closePort();
            } else {
                log("Failed to connect to device: " + selectedDevice.getDescription());
            }
        } catch (Exception e) {
            log("Error connecting to device: " + e.getMessage());
        }

        deviceTable.refresh();
    }

    /**
     * Disconnect from the selected device.
     */
    private void disconnectDevice() {
        DeviceRow selectedDevice = deviceTable.getSelectionModel().getSelectedItem();
        if (selectedDevice == null) {
            log("No device selected for disconnection.");
            return;
        }

        log("Disconnecting device: " + selectedDevice.getPortName());
        selectedDevice.setStatus("Available");
        statusLabel.setText("No device connected.");

        latitudeLabel.setText("Latitude: --");
        longitudeLabel.setText("Longitude: --");
        altitudeLabel.setText("Altitude: --");

        log("Device disconnected.");
        deviceTable.refresh();
    }

    /**
     * Logs messages to the log area.
     */
    private void log(String message) {
        logArea.appendText(message + "\n");
    }

    /**
     * Mock method to fetch connected devices.
     * Replace with actual logic to fetch connected devices using GPSDevice.
     */
    private List<DeviceRow> fetchDevices() {
        List<DeviceRow> devices = new ArrayList<>();
        devices.add(new DeviceRow("COM3", "Prolific USB-to-Serial", "Available"));
        devices.add(new DeviceRow("COM5", "u-blox GNSS Receiver", "Offline"));
        return devices;
    }

    /**
     * Finds a SerialPort by its name.
     */
    private SerialPort findPortByName(String portName) {
        SerialPort[] ports = SerialPort.getCommPorts();
        for (SerialPort port : ports) {
            if (port.getSystemPortName().equals(portName)) {
                return port;
            }
        }
        return null;
    }
}
