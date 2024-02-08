package com.example.fuelmanagementsystem.newWork;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.example.fuelmanagementsystem.R;
import com.example.fuelmanagementsystem.Utils.VolleySingleton;
import com.example.fuelmanagementsystem.VehicleAdapter;
import com.example.fuelmanagementsystem.VehicleModel;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewActivity extends AppCompatActivity {

    private Context mContext;
    ArrayList<VehicleModel> vehicleModelArrayList ;
    private VehicleAdapter vhAdapter;
    private String url ="http://fueleye.com/get_vehicle_list?data=n3bCxQ";
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);
        mContext = this;
        recyclerView = findViewById(R.id.recyclerView);
        vehicleModelArrayList = new ArrayList<>();

        getData();

        vhAdapter = new VehicleAdapter(mContext,vehicleModelArrayList,null,null);
        recyclerView.setAdapter(vhAdapter);

    }

    private void getData() {
         StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
//                    Toast.makeText(mContext, response, Toast.LENGTH_SHORT).show();

                        String[] fms_data_array = response.split(",");
                        Log.e("datalength", fms_data_array.length + "");
                        System.out.println(Arrays.toString(fms_data_array));
                        vehicleModelArrayList = new ArrayList<>();
                        for (String fms_data : fms_data_array) {
                            String dn_regisno = fms_data.substring(0, 10);
                            String dn_fuellimit = fms_data.substring(10);
                            System.out.println(dn_regisno + " " + dn_fuellimit);
                            dn_regisno = dn_regisno.replaceAll("@", "");
                            vehicleModelArrayList.add(new VehicleModel(dn_regisno,dn_fuellimit));
                        }
                        Log.e("vehicleModelArrayList", new Gson().toJson(vehicleModelArrayList));
                        Toast.makeText(this, vehicleModelArrayList.size() + "", Toast.LENGTH_SHORT).show();
                       vhAdapter.notifyDataSetChanged();
                    vhAdapter = new VehicleAdapter(mContext,vehicleModelArrayList,null,null);
                    recyclerView.setAdapter(vhAdapter);
                }, error ->{
            Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show();

        }
        );
//        {
//            @Override
//            protected Map<String,String> getParams(){
//                Map<String,String> params = new HashMap<>();
//                params.put("data","n3bCxQ");
//                return params;
//            }
//
//        };
        Log.e("stringRequest",stringRequest.toString());
        VolleySingleton.getInstance(mContext).addToRequestQueue(stringRequest);
    }
}