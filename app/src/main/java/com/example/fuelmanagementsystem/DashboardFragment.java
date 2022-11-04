package com.example.fuelmanagementsystem;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


public class DashboardFragment extends Fragment {


//    HttpsTrustManager httpsTrustManager = new HttpsTrustManager();
    String user_mail;
    SwipeRefreshLayout swipeRefreshLayoutdashboard;
    private RecyclerView vehicledashboardRV;
    private ArrayList<VehicleModel> vehicleModelArrayList;
    public static final String MyPREFERENCES = "MyPrefs";
//    SharedPreferences fms_uuid;
    SharedPreferences sp_login;
    SharedPreferences sp_connected;
    SharedPreferences.Editor editor;
    SwipeRefreshLayout swipeRefreshLayout;
    HashMap<String, String> hashMap = new HashMap<>();
    HttpParse httpParse = new HttpParse();
    HttpUrl httpUrl = new HttpUrl();
    ProgressDialog progressDialog;
    String user_id = "", finalResult = "",user_type = "";
    TextView textView;
    String access_token = "";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        textView = (TextView) root.findViewById(R.id.textView);

        sp_login = getActivity().getSharedPreferences(UserLoginActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        sp_connected = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        editor = sp_login.edit();
        vehicledashboardRV = root.findViewById(R.id.vehicle_list);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                access_token = sp_login.getString("token","");
//                tvAppend(textView,"\n"+access_token);
//                vehicle_list_api();
            }
        }, 5000);




        return root;
    }







    private void tvAppend(TextView tv, CharSequence text) {
        final TextView ftv = tv;
        final CharSequence ftext = text;
        ftv.append(ftext);
    }
}