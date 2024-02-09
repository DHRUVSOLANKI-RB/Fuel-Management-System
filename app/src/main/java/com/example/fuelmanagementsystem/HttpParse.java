package com.example.fuelmanagementsystem;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Juned on 3/3/2017.
 */

public class HttpParse {

    String FinalHttpData = "";
    String Result;
    BufferedWriter bufferedWriter;
    OutputStream outputStream;
    BufferedReader bufferedReader;
    StringBuilder stringBuilder = new StringBuilder();
    URL url;

    public String getRequest(HashMap<String, String> Data, String HttpUrlHolder) {

        try {
            url = new URL(HttpUrlHolder+FinalDataParseGet(Data));

            System.out.println("get url- "+url);

            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

            //InputStream in = httpURLConnection.getInputStream();

            bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            FinalHttpData = bufferedReader.readLine();

//            httpURLConnection.setReadTimeout(14000);
//
//            httpURLConnection.setConnectTimeout(14000);
//
//            httpURLConnection.setRequestMethod("GET");
//
//            httpURLConnection.setDoInput(true);



        } catch (Exception e) {
            e.printStackTrace();
        }

        return FinalHttpData;
    }

    public String postRequest(HashMap<String, String> Data, String HttpUrlHolder) {

        try {
            url = new URL(HttpUrlHolder);

            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

            httpURLConnection.setReadTimeout(14000);

            httpURLConnection.setConnectTimeout(14000);

            httpURLConnection.setRequestMethod("GET");

            httpURLConnection.setDoInput(true);

            httpURLConnection.setDoOutput(true);

            outputStream = httpURLConnection.getOutputStream();

            bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));

            bufferedWriter.write(FinalDataParse(Data));

            bufferedWriter.flush();

            bufferedWriter.close();

            outputStream.close();

            System.out.println("Result-" + httpURLConnection.getResponseCode());

            bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            FinalHttpData = bufferedReader.readLine();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return FinalHttpData;
    }

    public String FinalDataParseGet(HashMap<String, String> hashMap2) throws UnsupportedEncodingException {


        for (Map.Entry<String, String> map_entry : hashMap2.entrySet()) {

            System.out.println(URLEncoder.encode(map_entry.getValue(), "UTF-8"));
            System.out.println(map_entry.getValue());
            //System.out.println("String's Length -" + stringBuilder.length());
            if (stringBuilder.length() == 0){
                stringBuilder.append("?");
            }
            else{
                stringBuilder.append("&");
            }

            stringBuilder.append(URLEncoder.encode(map_entry.getKey(), "UTF-8"));

            stringBuilder.append("=");

            stringBuilder.append(URLEncoder.encode(map_entry.getValue(), "UTF-8"));

        }

        Result = stringBuilder.toString();

        System.out.println("Result URL- " + Result);

        return Result;
    }

    public String FinalDataParse(HashMap<String, String> hashMap2) throws UnsupportedEncodingException {

        for (Map.Entry<String, String> map_entry : hashMap2.entrySet()) {

            stringBuilder.append("&");

            stringBuilder.append(URLEncoder.encode(map_entry.getKey(), "UTF-8"));

            stringBuilder.append("=");

            stringBuilder.append(URLEncoder.encode(map_entry.getValue(), "UTF-8"));

        }

        Result = stringBuilder.toString();

        System.out.println("Result URL- " + Result);

        return Result;
    }
}