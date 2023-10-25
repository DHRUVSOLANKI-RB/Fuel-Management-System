package com.example.fuelmanagementsystem;

public class Command {

    public static final String header = ("AA");
    public static final String write_length = ("11");
    public static final String footer = ("55");
    public static final String tag_read_code = ("00");
    public static final String tag_write_code = ("01");
    public static final String reserved = ("FF FF FF");
    public static final String protocol_version = ("01");
    public static final String read_command = ("AA 01 00 00 55");
    public static final String nack_read = ("AA 02 80 04 7C 55");
    public static final String ack_read = ("AA 02 80 03 7D 55");
    public static final String nack_write = ("AA 02 81 04 7B 55");
    /*public static final String emptytagresponse_read = ("AA 02 80 03 7D 55 AA 02 80 14 6C 55");*/
    public static final String emptytagresponse_read = ("AA 02 80 14 6C 55");
    /*public static final String emptytagresponse_write = ("AA 02 81 03 7C 55 AA 02 81 14 6B 55");*/
    public static final String emptytagresponse_write = ("AA 02 81 14 6B 55");
    public static final String success_write = ("AA 02 81 03 7C 55 AA 02 81 13 6C 55");
    public static final String empty_data = "00 00 00 00 00 00 00 00 00 00 00 00";
//    public static final String write_command_empty =("AA 11 01 00 00 00 00 00 00 00 00 00 00 00 00 FF FF FF 01 01 55");
    public static final String write_command_empty =(header+write_length+tag_write_code+empty_data+reserved+protocol_version+"01"+footer);


}
