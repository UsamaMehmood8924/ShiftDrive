package com.usamamehmood.shiftdrive;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

//This class sign up the vendor

public class Vendor_Signup extends AppCompatActivity {

    EditText _regName,_regPhone,_regEmail,_regPassword,_regLocation,_regShopName;
    Button _RegisterBtn;
    TextView _BtnToSignin;

    private Boolean check = true;
    private Boolean check1 = true;

    String u_email,u_password, u_name,u_phone , u_location, u_shopname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor__signup);

        _regName = findViewById(R.id.regName);
        _regPhone = findViewById(R.id.regPhone);
        _regEmail = findViewById(R.id.regEmail);
        _regPassword = findViewById(R.id.regPassword);
        _RegisterBtn = findViewById(R.id.btnRegister);
        _BtnToSignin = findViewById(R.id.btntosignin);
        _regLocation = findViewById(R.id.regLocation);
        _regShopName = findViewById(R.id.regShopName);

        _RegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerVendor();
            }
        });

        _BtnToSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Vendor_Signup.this, Vendor_Login.class);
                startActivity(i);
            }
        });


    }

    private void registerVendor()
    {
        _regEmail.setError(null);
        _regPassword.setError(null);

        u_email = _regEmail.getText().toString();
        u_password = _regPassword.getText().toString();
        u_name = _regName.getText().toString();
        u_phone = _regPhone.getText().toString();
        u_location = _regLocation.getText().toString();
        u_shopname = _regShopName.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(u_password)) {
            _regPassword.setError("This field is required");
            focusView = _regPassword;
            cancel = true;
        }
        if (u_password.length() < 7) {
            _regPassword.setError("Password too short");
            focusView = _regPassword;
            cancel = true;
        }

        if (TextUtils.isEmpty(u_name)) {
            _regName.setError("This Field is required");
            focusView = _regName;
            cancel = true;
        }
        if (TextUtils.isEmpty(u_location)) {
            _regLocation.setError("This Field is required");
            focusView = _regLocation;
            cancel = true;
        }
        if (TextUtils.isEmpty(u_shopname)) {
            _regShopName.setError("This Field is required");
            focusView = _regShopName;
            cancel = true;
        }
        if (TextUtils.isEmpty(u_phone)) {
            _regPhone.setError("This Field is required");
            focusView = _regPhone;
            cancel = true;
        }
        if (u_phone.length() < 10 || u_phone.length() > 11) {
            _regPhone.setError("Please Enter 11 digit phone number");
            focusView = _regPhone;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(u_email)) {
            _regEmail.setError("This Field is required");
            focusView = _regEmail;
            cancel = true;
        } else if (!isEmailValid(u_email)) {
            _regEmail.setError("This email address is invalid");
            focusView = _regEmail;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {

            ProgressDialog pd = new ProgressDialog(Vendor_Signup.this);
            pd.setMessage("Registering...");
            pd.show();

            //Create a Volley Request
            String url = "http://codingwithsunny.com/Shiftdrive/registerVendor.php";
            StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject res=new JSONObject(response);
                                if(res.getString("status").equalsIgnoreCase("1"))
                                {
                                    pd.dismiss();

                                    //Setting User Credentials for further usage
                                    Vendor_Details.setId(res.getString("id"));
                                    Vendor_Details.setLocation(u_location);
                                    Vendor_Details.setShopname(u_shopname);
                                    Vendor_Details.setName(u_name);
                                    //Perform Intent
                                    Intent i = new Intent(Vendor_Signup.this,Vendor_Dashboard.class);
                                    finish();
                                    startActivity(i);
                                }
                                else
                                {
                                    showErrorDialog("Registration attempt failed");
                                    pd.dismiss();
                                    Toast.makeText(Vendor_Signup.this,res.getString("status"),Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            //Toast.makeText(MainActivity.this,response.toString(),Toast.LENGTH_LONG).show();
                        }
                    },
                    new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //This code is executed if there is an error.
                            showErrorDialog("Registration attempt failed");
                            pd.dismiss();

                        }
                    }) {
                protected Map<String, String> getParams() {
                    Map<String, String> data = new HashMap<String, String>();
                    data.put("email", u_email);
                    data.put("password", u_password);
                    data.put("name", u_name);
                    data.put("phone", u_phone);
                    data.put("location", u_location);
                    data.put("shopname", u_shopname);
                    return data;
                }
            };
            Volley.newRequestQueue(Vendor_Signup.this).add(MyStringRequest);
        }
    }

    private boolean isEmailValid(String email) {
        // You can add more checking logic here.
        return email.contains("@");
    }

    //Create an alert dialog to show in case registration failed
    private void showErrorDialog(String message){

        new AlertDialog.Builder(this)
                .setTitle("Oops")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


}