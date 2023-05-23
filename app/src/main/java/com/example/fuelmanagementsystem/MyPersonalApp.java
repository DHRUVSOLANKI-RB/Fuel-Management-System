package com.example.fuelmanagementsystem;

//Use an Application class at the beginning of your app. That allows a proper file and log handling.
//
//        Code below creates a log file at the following location:
//
//        /ExternalStorage/MyPersonalAppFolder/logs/logcat_XXX.txt
//        XXX is the current time in milliseconds. Every time you run your app, a new logcat_XXX.txt file will be created.

import android.app.Application;
import android.os.Environment;

import java.io.File;
import java.io.IOException;

public class MyPersonalApp extends Application {

    /**
     * Called when the application is starting, before any activity, service, or receiver objects (excluding content providers) have been created.
     */
    public void onCreate() {

        super.onCreate();

        if ( isExternalStorageWritable() ) {

            File appDirectory = new File( Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Tag_Management" );
            File logDirectory = new File( appDirectory + "/Logs" );
            File logFile = new File( logDirectory, "Tag_Management_logcat_" + System.currentTimeMillis() + ".txt" );

            // create app folder
            if ( !appDirectory.exists() ) {
                appDirectory.mkdir();
            }

            // create log folder
            if ( !logDirectory.exists() ) {
                logDirectory.mkdir();
            }

            // clear the previous logcat and then write the new one to the file
            try {
                Process process = Runtime.getRuntime().exec("logcat -c");
                process = Runtime.getRuntime().exec("logcat -f " + logFile);
            } catch ( IOException e ) {
                e.printStackTrace();
            }

        } else if ( isExternalStorageReadable() ) {
            // only readable
        } else {
            // not accessible
        }
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if ( Environment.MEDIA_MOUNTED.equals( state ) ) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if ( Environment.MEDIA_MOUNTED.equals( state ) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals( state ) ) {
            return true;
        }
        return false;
    }
}