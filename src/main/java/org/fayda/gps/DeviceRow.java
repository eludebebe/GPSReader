package org.fayda.gps;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class DeviceRow {
    private final StringProperty portName;
    private final StringProperty description;
    private final StringProperty status;

    public DeviceRow(String portName, String description, String status) {
        this.portName = new SimpleStringProperty(portName);
        this.description = new SimpleStringProperty(description);
        this.status = new SimpleStringProperty(status);
    }

    public String getPortName() {
        return portName.get();
    }

    public StringProperty portNameProperty() {
        return portName;
    }

    public String getDescription() {
        return description.get();
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public String getStatus() {
        return status.get();
    }

    public StringProperty statusProperty() {
        return status;
    }

    public void setStatus(String status) {
        this.status.set(status);
    }
}
