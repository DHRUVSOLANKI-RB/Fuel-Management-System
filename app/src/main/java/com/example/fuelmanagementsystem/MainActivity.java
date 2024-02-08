package com.example.fuelmanagementsystem;



import static com.example.fuelmanagementsystem.Command.empty_data;
import static com.example.fuelmanagementsystem.Command.footer;
import static com.example.fuelmanagementsystem.Command.header;
import static com.example.fuelmanagementsystem.Command.protocol_version;
import static com.example.fuelmanagementsystem.Command.read_command;
import static com.example.fuelmanagementsystem.Command.reserved;
import static com.example.fuelmanagementsystem.Command.tag_write_code;
import static com.example.fuelmanagementsystem.Command.write_command_empty;
import static com.example.fuelmanagementsystem.Command.write_length;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.fuelmanagementsystem.networkmonitor.NetworkChangeListener;
import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private RecyclerView vehicledashboardRV;
    private AppBarConfiguration mAppBarConfiguration;
    TextView signout;
    Button read;
    SwitchMaterial connect_switch;
    SwipeRefreshLayout swipeRefreshLayoutdashboard;
    private ArrayList<VehicleModel> vehicleModelArrayList;
    ArrayList<VehicleModel> newVehicleData;
    boolean isFiltered = false;

    IntentFilter intentFilter;
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();
    String device_name = "",device_vendor = "",product_id = "";
    String tag_regisno;
    String tag_fuellimit;
    String tag_uuid;
    String access_token = "";
    String mail = "";
    boolean writechk = true;
    HashMap<String, String> hashMap = new HashMap<>();
    HttpParse httpParse = new HttpParse();
    HttpUrl httpUrl = new HttpUrl();
    ProgressDialog progressDialog  ;
    ;
    String user_id = "", finalResult = "",user_type = "";

    public static final String MyPREFERENCES = "MyPrefs";
    SharedPreferences sp_login;
    SharedPreferences sp_connected;
    SharedPreferences.Editor editor_splogin;

    public final String ACTION_USB_PERMISSION = "com.example.fuelmanagementsystem.USB_PERMISSION";
    TextView textView;
    UsbManager usbManager;
    UsbDevice device;
    UsbSerialDevice serialPort;
    UsbDeviceConnection connection;
    IntentFilter filter;
    private String newline = TextUtil.newline_crlf;
    public String response = "";
    private static final int BAUD_RATE = 9600;

    ImageView clearImageView;
    EditText search_edit_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestMultiplePermissions();
        sp_login = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        sp_connected = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        editor_splogin = sp_login.edit();
        progressDialog = ProgressDialog.show(MainActivity.this, "Fetching vehicle list.\nPlease Wait...", null, true, true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                login_api();
            }
        }, 1000);

        clearImageView = findViewById(R.id.clear_search_query);
        search_edit_text = findViewById(R.id.search_edit_text);

        clearImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_edit_text.setText("");
            }
        });

        search_edit_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if(TextUtils.isEmpty(charSequence.toString())){

                    isFiltered = false;

                    clearImageView.setVisibility(View.GONE);

                    VehicleAdapter vehicleAdapter = new VehicleAdapter(MainActivity.this, vehicleModelArrayList, new VehicleAdapter.ClickListener() {
                        @Override
                        public void onPositionClicked(int position) {
                        }
                    }, writeClicklistner);

                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false);

                    vehicledashboardRV.setLayoutManager(linearLayoutManager);
                    vehicledashboardRV.setAdapter(vehicleAdapter);
                    vehicledashboardRV.setVisibility(View.VISIBLE);
                    vehicleAdapter.setOnItemClickListener(onItemClickListener);

                }else{

                    isFiltered = true;

                    clearImageView.setVisibility(View.VISIBLE);

                    newVehicleData = new ArrayList<>();

                    vehicleModelArrayList.forEach(vehicleModel -> {

                        if(vehicleModel.getRegisno().toLowerCase().contains(charSequence.toString().toLowerCase())){

                            newVehicleData.add(vehicleModel);

                            VehicleAdapter vehicleAdapter = new VehicleAdapter(MainActivity.this, newVehicleData, new VehicleAdapter.ClickListener() {
                                @Override
                                public void onPositionClicked(int position) {

                                }
                            }, writeClicklistner);

                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false);

                            vehicledashboardRV.setLayoutManager(linearLayoutManager);
                            vehicledashboardRV.setAdapter(vehicleAdapter);
                            vehicledashboardRV.setVisibility(View.VISIBLE);
                            vehicleAdapter.setOnItemClickListener(onItemClickListener);
                        }

                        if(newVehicleData.isEmpty()){
                            vehicledashboardRV.setAdapter(null);
                            vehicledashboardRV.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        usbManager = (UsbManager) getSystemService(USB_SERVICE);

        intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener, intentFilter);

        vehicledashboardRV = findViewById(R.id.vehicle_list);
        connect_switch = findViewById(R.id.connect_switch);
        swipeRefreshLayoutdashboard = findViewById(R.id.swipeRefreshlayout_dashboard);

        swipeRefreshLayoutdashboard.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh() {
                swipeRefreshLayoutdashboard.setRefreshing(false);
                vehicle_list_api();
            }
        });
        textView = (TextView) findViewById(R.id.textView);
        textView.setVisibility(View.GONE);
        filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        MainActivity.this.registerReceiver(broadcastReceiver, filter);

        mail = sp_login.getString("mail","");


        if (connection == null) {
            connect_switch.setChecked(false);
        }
        else{
            connect_switch.setChecked(true);
        }

        read = findViewById(R.id.read);
        read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                response = "";
                send(Command.read_command);
                progressDialog = ProgressDialog.show(MainActivity.this, "Reading", null, true, true);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        if (chkrsponse()) {
                            inflate_dialogue();
                        }
                    }
                }, 500);
            }
        });

        signout = findViewById(R.id.logout);
        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout_api();
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_open,R.string.navigation_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_dashboard,R.id.nav_adduser,R.id.nav_editvehicles,R.id.nav_connect).setDrawerLayout(drawerLayout).build();


    }

    UsbSerialInterface.UsbReadCallback mCallback = new UsbSerialInterface.UsbReadCallback() { //Defining a Callback which triggers whenever data is read.
        @Override
        public void onReceivedData(byte[] arg0) {
            String data = null;
            data = TextUtil.toHexString(arg0);
            StringBuilder rb = new StringBuilder();
            rb.append(response);
            rb.append(data);
            response = rb.toString();
            response = response.replaceAll("\\s", "");
        }
    };
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { //Broadcast Receiver to automatically start and stop the Serial connection.
        @Override
        public void onReceive(Context context, Intent intent) {if (intent.getAction().equals(ACTION_USB_PERMISSION)) {
            boolean granted = intent.getExtras().getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED);
            if (granted) {
                connection = usbManager.openDevice(device);
                serialPort = UsbSerialDevice.createUsbSerialDevice(device, connection);
                if (serialPort != null) {

                    if (serialPort.open()) { //Set Serial Connection Parameters.

                        serialPort.setBaudRate(BAUD_RATE);
                        serialPort.setDataBits(UsbSerialInterface.DATA_BITS_8);
                        serialPort.setStopBits(UsbSerialInterface.STOP_BITS_1);
                        serialPort.setParity(UsbSerialInterface.PARITY_NONE);
                        serialPort.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
                        serialPort.read(mCallback);

                    } else {
                        Log.d("SERIAL", "PORT NOT OPEN");
                    }
                } else {
                    Log.d("SERIAL", "PORT IS NULL");
                }
            } else {
                Log.d("SERIAL", "PERM NOT GRANTED");
            }
        } else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)) {

            onClickstart(connect_switch);

        } else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_DETACHED)) {
            if (serialPort != null) {
                stop();
            }
        }}
    };

//    Functions starts here

    public void write1(String rno,String fl){
        response ="";
        send(read_command);
        progressDialog = ProgressDialog.show(MainActivity.this, "Processing", null, true, true);

        new CountDownTimer(400, 200) {
            @Override
            public void onTick(long millisUntilFinished) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onFinish() {
                progressDialog.dismiss();
                if (chkrsponse()){
                    verify_vehicle_api(rno,fl);
                }
            }
        }.start();
    }

    public void write2(String rno,String fl){
        response = "";
        writechk = false;
        String tag_regisno_string = rno;
        String fuel_limit_string =  fl;
        StringBuilder sbregisno = new StringBuilder("");
        for (int i = 0;i<(10-(tag_regisno_string.length()));i++)
        {
            sbregisno.append("@");
        }
        tag_regisno_string = sbregisno.append(tag_regisno_string).toString();
        String dn_tag_regisno = asciiToHex(tag_regisno_string);
        String dn_tag_fuellimit = fuel_limit_LSB(fuel_limit_string);
        String reserved_code = reserved.replaceAll("\\s", "");
        String checksum = twos_complement(hextoIntSum(tag_write_code+dn_tag_regisno+dn_tag_fuellimit+reserved_code+protocol_version));

        send(header+write_length+tag_write_code+dn_tag_regisno+dn_tag_fuellimit+reserved_code+protocol_version+checksum+footer);
        tvAppend(textView,"\nTag UUID:- "+tag_uuid);

        Log.d("Write UUID",tag_uuid);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                chkrsponse_write();
            }
        }, 200);

    }

    public void login_api() {

        class GetFMSTokenClass extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected void onPostExecute(String httpResponseMsg) {

                super.onPostExecute(httpResponseMsg);



                System.out.println("httpResponseMsg- " + httpResponseMsg);
                String data_response = httpResponseMsg;
                data_response = data_response.replaceAll("\"","");
                if (data_response.equals("0"))
                {
                    new MaterialAlertDialogBuilder(MainActivity.this).setTitle("No token Found").setPositiveButton("Ok", (dialogInterface, i) -> {
                    }).show().setCanceledOnTouchOutside(false);
                }
                else if(data_response.isEmpty()){
                    new MaterialAlertDialogBuilder(MainActivity.this).setTitle("Something went wrong").setPositiveButton("Ok", (dialogInterface, i) -> {
                    }).show().setCanceledOnTouchOutside(false);
                }
                else{
                    String[] response_array = data_response.split(",");
                    access_token = response_array[1];
                    editor_splogin.putString("token",access_token);
                    editor_splogin.apply();
                    vehicle_list_api();
//                    tvAppend(textView,"\n"+access_token);
                }

            }

            @Override
            protected String doInBackground(String... params) {

//                mail = "hemesh";
                mail = sp_login.getString("mail","");
                System.out.println(mail);
                hashMap.put("data",mail);
//                tvAppend(textView,"\nMail:- "+mail);
                finalResult = httpParse.getRequest(hashMap, httpUrl.HttpURL_fmsToken);

                System.out.println(finalResult);

                return finalResult;
            }
        }

        GetFMSTokenClass getFMSTokenClass = new GetFMSTokenClass();
        getFMSTokenClass.execute();
    }

    public void vehicle_list_api() {
//        ProgressDialog progressDialog1 = ProgressDialog.show(MainActivity.this, "Fetching vehicle list...", null, true, true);
        class GetFMSDataClass extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

//               progressDialog = ProgressDialog.show(MainActivity.this, "Fetching vehicle list...", null, true, true);
            }

            @Override
            protected void onPostExecute(String httpResponseMsg) {

                super.onPostExecute(httpResponseMsg);


                String response = httpResponseMsg;
                tvAppend(textView,"\n"+httpResponseMsg);
                response = response.replaceAll("\"","");

                if(response.equals("0")){
                    new MaterialAlertDialogBuilder(MainActivity.this).setTitle("No Data Found").setPositiveButton("Ok", (dialogInterface, i) -> {
                    }).show().setCanceledOnTouchOutside(false);
                    vehicledashboardRV.setAdapter(null);
                }
                else if(response.isEmpty()){
                    new MaterialAlertDialogBuilder(MainActivity.this).setTitle("Something went wrong").setPositiveButton("Ok", (dialogInterface, i) -> {
                    }).show().setCanceledOnTouchOutside(false);
                    vehicledashboardRV.setAdapter(null);
                }
                else{
                    String[] fms_data_array = response.split(",");
                    System.out.println(fms_data_array.length);
                    System.out.println(Arrays.toString(fms_data_array));
                    vehicleModelArrayList = new ArrayList<>();
                    for(int i = 0; i<fms_data_array.length; i++){
//                        JSONObject obj_drawing = new JSONObject(json_data.getString(i));
                        String fms_data = fms_data_array[i];
                        String dn_regisno = fms_data.substring(0,10);
                        String dn_fuellimit = fms_data.substring(10);
                        System.out.println(dn_regisno + " " + dn_fuellimit);
                        dn_regisno=dn_regisno.replaceAll("@","");
                        vehicleModelArrayList.add(new VehicleModel(dn_regisno,dn_fuellimit));
                    }

                    VehicleAdapter vehicleAdapter = new VehicleAdapter(MainActivity.this, vehicleModelArrayList, new VehicleAdapter.ClickListener() {
                        @Override
                        public void onPositionClicked(int position) {
                            VehicleModel thisItem = vehicleModelArrayList.get(position);
                            //Toast.makeText(getActivity(), "ITEM PRESSED = " + thisItem.getInspection_uuid(), Toast.LENGTH_SHORT).show();
                            System.out.println("Item "+position + " clicked");
                            System.out.println("Main Activity Api Listner Clicked");
                        }
                    }, writeClicklistner);

                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false);

                    vehicledashboardRV.setLayoutManager(linearLayoutManager);
                    vehicledashboardRV.setAdapter(vehicleAdapter);
                    vehicledashboardRV.setVisibility(View.VISIBLE);
                    vehicleAdapter.setOnItemClickListener(onItemClickListener);

                }
                new Handler().postDelayed(() -> {
                    progressDialog.dismiss();
                },500);

            }

            @Override
            protected String doInBackground(String... params) {

                System.out.println(access_token);

                hashMap.put("data",access_token);
                finalResult = httpParse.getRequest(hashMap, httpUrl.HttpURL_vehicle_list);
                System.out.println(finalResult);


                return finalResult;
            }
        }

        GetFMSDataClass getFMSDataClass = new GetFMSDataClass();
        getFMSDataClass.execute();
    }

    public void verify_vehicle_api(String api_regisno,String api_fuellimit) {

        class VerifyVehicleClass extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                progressDialog = ProgressDialog.show(MainActivity.this, "Loading", null, true, true);
            }

            @Override
            protected void onPostExecute(String httpResponseMsg) {

                super.onPostExecute(httpResponseMsg);

                progressDialog.dismiss();

                System.out.println("httpResponseMsg- " + httpResponseMsg);
                String response_code = httpResponseMsg;
                response_code = response_code.replaceAll("\"","");

                if (response_code.equals("0")){
                    builder("Not Acknowleged").show();
                }
                else{
                    String[] response_code_arr = response_code.split(",");
                    tvAppend(textView,"\n" + response_code_arr[0] + " " +response_code_arr[1]);
                    String verify_code = response_code_arr[1];
                    if(verify_code.equals("2")){
                        builder("Tag is disabled").show();
                    }
                    else if(verify_code.equals("3")){
                        builder("Tag is assigned").show();
                    }
                    else if(verify_code.equals("4")){
                        builder("Tag is not available.").show();
                    }
                    else if(verify_code.equals("1")){
                        write2(api_regisno,api_fuellimit);
                        update_vehicle_api(api_regisno);
                    }
                    else if(verify_code.equals("0")){
                        write2(api_regisno,api_fuellimit);
                        update_vehicle_api(api_regisno);
                    }
                }
            }
            @Override
            protected String doInBackground(String... params) {

                String tag_regisno_string = api_regisno;
                StringBuilder sbregisno = new StringBuilder("");
                for (int i = 0;i<(10-(tag_regisno_string.length()));i++)
                {
                    sbregisno.append("@");
                }
                tag_regisno_string = sbregisno.append(tag_regisno_string).toString();

                Log.d("Verify UUID",tag_uuid);

                hashMap.put("data",access_token+tag_regisno_string+tag_uuid);
                System.out.println("Hashmap= " + access_token+tag_regisno_string+tag_uuid);
                tvAppend(textView,"\nVerify vehicle data sent:- "+access_token+" "+tag_regisno_string+" "+tag_uuid);
                finalResult = httpParse.getRequest(hashMap, httpUrl.HttpURL_vehicle_verify);

                System.out.println(finalResult);
                return finalResult;
            }
        }

        VerifyVehicleClass verifyVehicleClass = new VerifyVehicleClass();
        verifyVehicleClass.execute();
    }

    public void update_vehicle_api(String api_regisno) {

        class UpdateVehicleClass extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                progressDialog = ProgressDialog.show(MainActivity.this, "Loading", null, true, true);
            }

            @Override
            protected void onPostExecute(String httpResponseMsg) {

                super.onPostExecute(httpResponseMsg);

                progressDialog.dismiss();

                System.out.println("httpResponseMsg- " + httpResponseMsg);
                String data_response = httpResponseMsg;
                data_response = data_response.replaceAll("\"","");
                tvAppend(textView,"\nUpdate response:- " + data_response);
                if (data_response.equals("0"))
                {
                    new MaterialAlertDialogBuilder(MainActivity.this).setTitle("Something went wrong").setPositiveButton("Ok", (dialogInterface, i) -> {
                    }).show().setCanceledOnTouchOutside(false);
                }
                else if (data_response.equals("1")){
                    new MaterialAlertDialogBuilder(MainActivity.this).setTitle("Vehicle updated successfully").setPositiveButton("Ok", (dialogInterface, i) -> {
                    }).show().setCanceledOnTouchOutside(false);
                    tvAppend(textView,"\n" + "Vehicle updated successfully");
                }

            }

            @Override
            protected String doInBackground(String... params) {

                String tag_regisno_string = api_regisno;
                StringBuilder sbregisno = new StringBuilder("");
                for (int i = 0;i<(10-(tag_regisno_string.length()));i++)
                {
                    sbregisno.append("@");
                }
                tag_regisno_string = sbregisno.append(tag_regisno_string).toString();
                tvAppend(textView,"\nUpdate Vehicle data sent:- "+access_token+" "+tag_regisno_string+" "+tag_uuid);
                hashMap.put("data",access_token+tag_regisno_string+tag_uuid);
                finalResult = httpParse.getRequest(hashMap, httpUrl.HttpURL_vehicle_update);

                Log.d("Update UUID",tag_uuid);

                System.out.println(finalResult);

                return finalResult;
            }
        }

        UpdateVehicleClass updateVehicleClass = new UpdateVehicleClass();
        updateVehicleClass.execute();
    }

    public void logout_api() {

        class LogoutClass extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                progressDialog = ProgressDialog.show(MainActivity.this, "Signing Out...", null, true, true);
            }

            @Override
            protected void onPostExecute(String httpResponseMsg) {

                super.onPostExecute(httpResponseMsg);

                progressDialog.dismiss();

                System.out.println("httpResponseMsg- " + httpResponseMsg);
                String data_response = httpResponseMsg;
                tvAppend(textView,"\n"+httpResponseMsg);
                data_response = data_response.replaceAll("\"","");

                SharedPreferences sharedpreferences = getSharedPreferences(UserLoginActivity.MyPREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.clear();
                editor.apply();
                editor_splogin.clear();
                editor_splogin.apply();
                Intent intent = new Intent(MainActivity.this, UserLoginActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            protected String doInBackground(String... params) {

                hashMap.put("data",access_token+mail);
                tvAppend(textView,"\n"+access_token+mail);
                finalResult = httpParse.getRequest(hashMap, httpUrl.HttpURL_vehicle_logout );

                System.out.println(finalResult);

                return finalResult;
            }
        }

        LogoutClass logoutclass = new LogoutClass();
        logoutclass.execute();
    }

    private View.OnClickListener onItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
            int position = viewHolder.getAdapterPosition();
        }
    };
    private View.OnClickListener writeClicklistner = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            View v = view;
            View parent = (View) view.getParent();
            while (!(parent instanceof RecyclerView)){
                v=parent;
                parent = (View) parent.getParent();
            }
            int position = vehicledashboardRV.getChildAdapterPosition(v);
            tag_uuid = "";
            tag_fuellimit ="";
            tag_regisno = "";
            System.out.println("Item "+ position + " clicked");

            VehicleModel thisItem;

            if(isFiltered){
                thisItem = newVehicleData.get(position);
            }else{
                thisItem = vehicleModelArrayList.get(position);
            }
            /*VehicleModel thisItem = vehicleModelArrayList.get(position);*/

            System.out.println(thisItem.getRegisno() + " " +thisItem.getFuellimit());
            /*Toast.makeText(MainActivity.this, thisItem.getRegisno() + " " +thisItem.getFuellimit(), Toast.LENGTH_SHORT).show();*/
            write1(thisItem.getRegisno(),thisItem.getFuellimit());
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.refresh:
                clear();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void tvAppend(TextView tv, CharSequence text) {
        final TextView ftv = tv;
        final CharSequence ftext = text;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ftv.append(ftext);
            }
        });
    }

    private void  requestMultiplePermissions(){
        Dexter.withActivity(MainActivity.this)
                .withPermissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        if (report.areAllPermissionsGranted()) {

                        }

                        if (report.isAnyPermissionPermanentlyDenied()) {
                        }
                    }
                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(MainActivity.this, "Some Error! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }

    public void onClickstart(View v){
        start();
    }

    public void start(){

        try {

            //Log.e("rb-", "Start");
            HashMap<String, UsbDevice> usbDevices = usbManager.getDeviceList();

            if (!usbDevices.isEmpty()) {

                //Log.e("rb-", "if");
                boolean keep = true;
                for (Map.Entry<String, UsbDevice> entry : usbDevices.entrySet()) {
                    device = entry.getValue();
                }
                int deviceVID = device.getVendorId();
                device_name = device.getManufacturerName();
                device_vendor = String.format(Locale.US, "Vendor Id %04X", device.getVendorId());
                product_id = String.format(Locale.US, "Product Id %04X", device.getProductId());

                connect_switch.setChecked(true);

                //          PendingIntent pi = PendingIntent.getBroadcast(MainActivity.this, 0, new Intent(ACTION_USB_PERMISSION), 0);
                //          usbManager.requestPermission(device, pi);
                //          keep = false;

                PendingIntent pi = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                    pi = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_MUTABLE);
                }
                else {
                    pi = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_ONE_SHOT);
                }
                usbManager.requestPermission(device, pi);
                keep = false;
            }
            else
            {
                Log.d("rb-", "Device not conected!");
                builder("Please connect DEVICE").show();
                connect_switch.setChecked(false);
            }

        }catch (Exception e){
            e.printStackTrace();
            Log.d("checking device connection-", e.toString());
        }


    }

    public void send(String command){
        if(serialPort == null)
        {
            builder("Please Connect Device").show();
            return;
        }
        String msg;
        byte[] data;
        String string = command;
        StringBuilder sb = new StringBuilder();
        TextUtil.toHexString(sb, TextUtil.fromHexString(string));
        TextUtil.toHexString(sb, newline.getBytes());
        msg = sb.toString();
        data = TextUtil.fromHexString(msg);
        serialPort.write(data);
        tvAppend(textView, "\n\nData Sent : " + string + "\n");
    }

    public void stop(){
        serialPort.close();
        response = "";
        serialPort = null;
        connection = null;
        connect_switch.setChecked(false);
        clear();
    }

    public void clear() {
        textView.setText(" ");
    }

    MaterialAlertDialogBuilder builder(String msg)    {

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(MainActivity.this);
        builder.setMessage(msg);
        builder.setTitle("Alert !");
        builder.setCancelable(false);
        builder.setPositiveButton("ok", (DialogInterface.OnClickListener) (dialog, which) -> {

        });
        return builder;
    }

    @Override
    public void onBackPressed(){

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

            builder.setMessage("Do you want to exit ?");

            builder.setTitle("Alert !");

            builder.setCancelable(false);

            builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
                finish();
            });

            builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
                dialog.cancel();
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
    }

    public boolean chkrsponse(){

        try {

            Log.d("Read Raw Response:-", response);

            if(response.isEmpty()){

                builder("Please reconnect cable").show();
                return false;
            }

            if(response.contains("AA18")){

                Log.d("contains AA18:-", response);

                String[] array_response = response.split("AA18");

                String response_needed = "AA18" + array_response[1];

                Log.d("response_needed:-", response_needed);

                String array_checksum = response_needed.substring(4,52);

                String cmd_checksum = response_needed.substring(52,54).toLowerCase();

                Log.d("array_checksum:-", array_checksum);

                String calculated_checksum = twos_complement(hextoIntSum(array_checksum));

                Log.d("checksum:-", calculated_checksum);

                if(calculated_checksum.toLowerCase().equals(cmd_checksum)){

                    Log.d("Read Response 2:-", response_needed);

                    tag_regisno = response_needed.substring(6,26);
                    Log.d("Read Hex Registration Number:-", tag_regisno);

                    tag_fuellimit = response_needed.substring(26,30);
                    Log.d("Read Fuel Limit:-", tag_fuellimit);

                    tag_uuid = response_needed.substring(38,52);
                    Log.d("Read UUID:-", tag_uuid);

                    tag_regisno = hexToASCII(tag_regisno);
                    Log.d("Read ASCII Registration Number:-", tag_regisno);

                    tag_fuellimit = fuel_limit_value(tag_fuellimit);
                    tvAppend(textView,"\nRegisNo:- " + tag_regisno);
                    tvAppend(textView,"\nFuel Limit:- " + tag_fuellimit);
                    tvAppend(textView,"\nUUID:- " + tag_uuid);
                    if ((tag_regisno + tag_fuellimit).equals(empty_data.replaceAll("\\s", "")))
                    {
                        tvAppend(textView,"\nTag Data Empty.");
                        return false;
                    }
                }else{

                    Log.d("response:-", "Wrong checksum");

                    builder("Incorrect UID, Please try again....").show();
                    return false;
                }

            }else if(response.contains("AA028014")){

                Log.d("response:-", "Tag not present");

                builder("Tag not present").show();
                return false;

            }else{

                String response_1 = response.substring(0,12);
                Log.d("Read Response 1:-", response_1);

                if(!response_1.isEmpty()){
                    response_1 = response_1.replaceAll("\\s", "");
                }

                String emptytagresponse_read = Command.emptytagresponse_read.replaceAll("\\s", "");
                String nack_read = Command.nack_read.replaceAll("\\s", "");
                String ack_read = Command.ack_read.replaceAll("\\s", "");

                if(response_1.equals(nack_read)){

                    builder("Wrong command").show();
                    return false;

                }else if(response_1.equals(ack_read)) {

                }
            }




            /*String response_1 = response.substring(0,12);
            Log.d("Read Response 1:-", response_1);

            String response_2 = response.substring(12);
            Log.d("Read Response 2:-", response_2);


            if(!response_1.isEmpty()){
                response_1 = response_1.replaceAll("\\s", "");
            }

            if(!response_2.isEmpty()){
                response_2 = response_2.replaceAll("\\s", "");
            }

            String emptytagresponse_read = Command.emptytagresponse_read.replaceAll("\\s", "");
            String nack_read = Command.nack_read.replaceAll("\\s", "");
            String ack_read = Command.ack_read.replaceAll("\\s", "");


            if(response_1.equals(nack_read)){

                builder("Wrong command").show();
                return false;

            }else if(response_1.equals(ack_read)){

                if(response_2.equals(emptytagresponse_read)){

                    Log.d("response:-", "Tag not present");

                    builder("Tag not present").show();
                    return false;

                }else if(response_2.contains("AA18")){

                    String array_checksum = response_2.substring(4,52);

                    String cmd_checksum = response_2.substring(52,54).toLowerCase();

                    Log.d("array_checksum:-", array_checksum);

                    String calculated_checksum = twos_complement(hextoIntSum(array_checksum));

                    Log.d("checksum:-", calculated_checksum);

                    if(calculated_checksum.toLowerCase().equals(cmd_checksum)){

                        Log.d("Read Response 2:-", response_2);

                        tag_regisno = response_2.substring(6,26);
                        Log.d("Read Hex Registration Number:-", tag_regisno);

                        tag_fuellimit = response_2.substring(26,30);
                        Log.d("Read Fuel Limit:-", tag_fuellimit);

                        tag_uuid = response_2.substring(38,52);
                        Log.d("Read UUID:-", tag_uuid);

                        tag_regisno = hexToASCII(tag_regisno);
                        Log.d("Read ASCII Registration Number:-", tag_regisno);

                        tag_fuellimit = fuel_limit_value(tag_fuellimit);
                        tvAppend(textView,"\nRegisNo:- " + tag_regisno);
                        tvAppend(textView,"\nFuel Limit:- " + tag_fuellimit);
                        tvAppend(textView,"\nUUID:- " + tag_uuid);
                        if ((tag_regisno + tag_fuellimit).equals(empty_data.replaceAll("\\s", "")))
                        {
                            tvAppend(textView,"\nTag Data Empty.");
                            return false;
                        }
                    }else{

                        Log.d("response:-", "Wrong checksum");

                        builder("Incorrect UID, Please try again....").show();
                        return false;
                    }
                }

            }else{

                Log.d("response:-", "not ack, not nack");

                builder("Please reconnect cable!").show();
                return false;
            }*/

        }catch (Exception e){

            Log.d("Error while reading:-", e.toString());
        }

        return true;
    }

    public String fuel_limit_value(String fuel_limit_hex){

        StringBuilder sbfuellimit_lsb1 = new StringBuilder("");
        StringBuilder sbfuellimit_lsb2 = new StringBuilder("");

        for (int i = 2; i<4; i++)
        {
            char tagchar = fuel_limit_hex.charAt(i);
            sbfuellimit_lsb1.append(tagchar);
        }
        for (int i = 0; i<2; i++)
        {
            char tagchar = fuel_limit_hex.charAt(i);
            sbfuellimit_lsb1.append(tagchar);
        }
        String lsb1 = hexToint(sbfuellimit_lsb1.toString());
        if (lsb1.equals("0")){lsb1="";};
        return lsb1;
    }

    public String fuel_limit_LSB(String fuel_limit_value){

        int flv = Integer.parseInt(fuel_limit_value);
        String hex_flv = Integer.toHexString(flv);

        StringBuilder sbfuellimit = new StringBuilder("");
        for (int i = 0;i<(4-(hex_flv.length()));i++)
        {
            sbfuellimit.append("0");
        }
        sbfuellimit.append(hex_flv);
        hex_flv = sbfuellimit.toString();
        StringBuilder sb1 = new StringBuilder("");

        for (int i = 2; i<4; i++)
        {
            char tagchar = hex_flv.charAt(i);
            sb1.append(tagchar);
        }
        for (int i = 0; i<2; i++)
        {
            char tagchar = hex_flv.charAt(i);
            sb1.append(tagchar);
        }
        hex_flv = sb1.toString();
        return hex_flv;
    }

    public boolean chkrsponse_write(){
        if(response.equals(Command.emptytagresponse_write.replaceAll("\\s", "")))
        {
            builder("Tag not present").show();
        }
        if(response.equals(Command.nack_write.replaceAll("\\s", "")))
        {
            builder("Wrong Checksum").show();
        }
        if(response.equals(Command.success_write.replaceAll("\\s", "")))
        {
            return true;
        }
        return false;
    }

    public static String asciiToHex(String asciiValue){
        char[] chars = asciiValue.toCharArray();
        StringBuffer hex = new StringBuffer();
        for (int i = 0; i < chars.length; i++)
        {
            hex.append(Integer.toHexString((int) chars[i]));
        }
        return hex.toString();
    }

    public static String hexToASCII(String hexValue){
        StringBuilder output = new StringBuilder("");
        for (int i = 0; i < hexValue.length(); i += 2)
        {
            String str = hexValue.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }
        return output.toString();
    }

    public int hextoIntSum(String hexValue){
        StringBuilder output = new StringBuilder("");
        int hextointsum =0;
        for (int i = 0; i < hexValue.length(); i += 2)
        {
            String str = hexValue.substring(i, i + 2);
            output.append(Integer.parseInt(str, 16));
            hextointsum += Integer.parseInt(str, 16);
        }
        return hextointsum;
    }

    public static String hexToint(String hexValue)//for single hex value
    {
        int decimal=Integer.parseInt(hexValue,16);
        System.out.println(decimal);
        return Integer.toString(decimal);
    }

    public String twos_complement(int hextointsum){
        String twosComplement = Integer.toHexString((-1 * hextointsum));
        int n = twosComplement.length();
        StringBuilder output = new StringBuilder("");
        for (int i=(n-2); i<n; i++)
        {
            char tagchar = twosComplement.charAt(i);
            output.append(tagchar);
        }
        return output.toString();
    }

    public void inflate_dialogue(){

            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);

            View layout_dialog = LayoutInflater.from(MainActivity.this).inflate(R.layout.read_tagdata,null);
            builder.setView(layout_dialog);

            AppCompatButton btnOK = layout_dialog.findViewById(R.id.btnOK);
            TextView regisno = layout_dialog.findViewById(R.id.txt_regisno);
            TextView fuellimit = layout_dialog.findViewById(R.id.txt_fuellimit);
            TextView tuuid = layout_dialog.findViewById(R.id.txt_taguuid);
            final android.app.AlertDialog dialog = builder.create();
            dialog.show();
            dialog.setCancelable(false);
            dialog.getWindow().setGravity(Gravity.CENTER);

            btnOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            regisno.setText(tag_regisno.replaceAll("@",""));
            fuellimit.setText(tag_fuellimit);
            tuuid.setText(tag_uuid);
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void onStart() {
        super.onStart();
    }
    public void onResume() {
        super.onResume();
    }
    public void onPause() {
        super.onPause();
    }
    public void onStop() {
        super.onStop();
    }
    public void onDestroy() {
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }

}
