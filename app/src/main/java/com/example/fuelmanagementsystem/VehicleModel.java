package com.example.fuelmanagementsystem;

public class VehicleModel {

    private String regisno;
    private String fuellimit;


    public VehicleModel(String regisno,String fuellimit) {
        this.regisno = regisno;
        this.fuellimit = fuellimit;

    }


    public String getRegisno() {
        return regisno;
    }

    public String getFuellimit() {return fuellimit; }

    public void setRegisno(String regisno) {
        this.regisno = regisno;
    }


}
