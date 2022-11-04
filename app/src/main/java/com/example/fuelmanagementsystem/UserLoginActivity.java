package com.example.fuelmanagementsystem;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fuelmanagementsystem.networkmonitor.NetworkChangeListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class UserLoginActivity extends AppCompatActivity {

    EditText Email, Password;
    Button LogIn;
    String PasswordHolder, EmailHolder;
    String finalResult,rail = "Rail";
    Boolean CheckEditText;
    ProgressDialog progressDialog;
    HashMap<String, String> hashMap = new HashMap<>();
    public static final String MyPREFERENCES = "MyPrefs";
    HttpUrl httpUrl = new HttpUrl();
    HttpParse httpParse = new HttpParse();
    IntentFilter intentFilter;
    SharedPreferences sp_login;
    SharedPreferences.Editor editor;
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userlogin);

        Email = findViewById(R.id.email);
        Password = findViewById(R.id.password);
        LogIn = findViewById(R.id.Login);

        sp_login = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        if (sp_login.getBoolean("logged",false)) {
            goToMainActivity();
        }
        System.out.println(sp_login.getBoolean("logged",false));
        LogIn.setOnClickListener(view -> {

//            goToMainActivity();

            CheckEditTextIsEmptyOrNot();

            if (CheckEditText) {
                UserLoginFunction(EmailHolder, PasswordHolder);
            } else {
                Toast.makeText(UserLoginActivity.this, "Please fill all form fields.", Toast.LENGTH_LONG).show();
            }
        });

        Email.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                hideKeyboard(v);
            }
        });

        Password.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                hideKeyboard(v);
            }
        });
    }


    public void goToMainActivity() {
        Intent intent = new Intent(UserLoginActivity.this, MainActivity.class);
        startActivity(intent);
    }
//    public void goToConnectActivity() {
//        Intent intent = new Intent(UserLoginActivity.this, com.example.fuelmanagementsystem.ConnectActivity2.class);
//        startActivity(intent);
//    }

    public void CheckEditTextIsEmptyOrNot() {

        EmailHolder = Email.getText().toString();
        PasswordHolder = Password.getText().toString();

        CheckEditText = !TextUtils.isEmpty(EmailHolder) && !TextUtils.isEmpty(PasswordHolder);
    }


//    public void UserLoginFunction(final String email, final String password) {
//
//        if(email.equals(rail) && password.equals(rail)){
//
//            editor = sp_login.edit();
//            editor.putBoolean("logged", true);
//            editor.apply();
//            System.out.println(sp_login.getBoolean("logged",false));
//            goToMainActivity();
//            System.out.println(sp_login.getBoolean("logged",false));
//        }
//
//    }

    public void UserLoginFunction(final String email, final String password) {

        class UserLoginClass extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                progressDialog = ProgressDialog.show(UserLoginActivity.this, "Loading Data", null, true, true);
            }

            @Override
            protected void onPostExecute(String httpResponseMsg) {

                super.onPostExecute(httpResponseMsg);

                progressDialog.dismiss();

                try {
                    JSONObject jsonObject = new JSONObject(httpResponseMsg);
                    String error = jsonObject.getString("status");

                    if(error.equals("error")) {

                        progressDialog.dismiss();

                        new MaterialAlertDialogBuilder(UserLoginActivity.this).setTitle(jsonObject.getString("statusMessage")).setPositiveButton("Ok", (dialogInterface, i) -> {

                        }).show();

                    }else if(error.equals("success")) {

                        finish();

                        String user_data = jsonObject.getString("data");

                        JSONObject user_jsonObject = new JSONObject(user_data);

                        SharedPreferences.Editor editor = sp_login.edit();

                        editor.putString("mail",email);
                        editor.putBoolean("logged", true);
//                        editor.putString("uname", user_jsonObject.getString("email"));
//                        editor.putString("user_id", user_jsonObject.getString("ID"));
                        editor.apply();

                        goToMainActivity();
//                        goToConnectActivity();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(String... params) {

                hashMap.put("email", params[0]);
                hashMap.put("password", params[1]);
                finalResult = httpParse.postRequest(hashMap, httpUrl.HttpURL_login);
                return finalResult;
            }
        }

        UserLoginClass userLoginClass = new UserLoginClass();
        userLoginClass.execute(email, password);

    }

    @Override
    protected void onStart() {
        super.onStart();
        intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener, intentFilter);
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
