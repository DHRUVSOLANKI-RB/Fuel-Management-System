package com.example.fuelmanagementsystem.newWork;

import java.io.Serializable;

public class VehicleData implements Serializable {
    private String vhName;

    public VehicleData(String vhName) {
        this.vhName = vhName;
    }

    public String getVhName() {
        return vhName;
    }

    public void setVhName(String vhName) {
        this.vhName = vhName;
    }
}
