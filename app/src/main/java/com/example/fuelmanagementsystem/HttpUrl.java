package com.example.fuelmanagementsystem;

public class HttpUrl {

    public String  HttpURL_main = "http://www.fueleye.com";
    //public String  HttpURL_main = "https://85d1-122-160-48-7.ngrok-free.app";
    public String HttpURL_login = HttpURL_main + "/android_login";
    public String HttpURL_fmsToken = HttpURL_main + "/e_app_login_req";
    public String HttpURL_vehicle_list = HttpURL_main + "/e_app_get_vehicle_list";
    public String HttpURL_searched_vehicle_list = HttpURL_main + "/e_app_get_searched_vehicle_list";
    public String HttpURL_vehicle_verify = HttpURL_main + "/e_app_verify_vehicle_list";
    public String HttpURL_vehicle_update = HttpURL_main + "/e_app_update_vehicle_list";
    public String HttpURL_vehicle_logout = HttpURL_main + "/e_app_expireEfuelSession";

}
