package com.example.fuelmanagementsystem;

public class HttpUrl {


    // public String  HttpURL_main = "https://3671-122-160-48-7.in.ngrok.io";
    public String  HttpURL_main = "http://www.fueleye.com";
    /*public String  HttpURL_main = "http://3.7.237.203";*/
    public String HttpURL_login = HttpURL_main + "/android_login";
    public String HttpURL_fmsToken = HttpURL_main + "/e_fuel_login_req";
    public String HttpURL_vehicle_list = HttpURL_main + "/get_vehicle_list";
    public String HttpURL_vehicle_verify = HttpURL_main + "/verify_vehicle_list";
    public String HttpURL_vehicle_update = HttpURL_main + "/update_vehicle_list";
    public String HttpURL_vehicle_logout = HttpURL_main + "/expire_token_onSignout";

}
