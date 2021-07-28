package com.usamamehmood.shiftdrive;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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

//This class login's the vendor in SD application

public class Vendor_Login extends AppCompatActivity {

    EditText _LoginEmail, _LoginPassword;
    Button _loginBtn;
    TextView _ForgetPassword, _NavToReg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor__login);

        //Connecting the UI elements
        _LoginEmail = findViewById(R.id.loginEmail);
        _LoginPassword = findViewById(R.id.loginPassword);
        _loginBtn = findViewById(R.id.loginButton);
        _ForgetPassword = findViewById(R.id.forgetPasswordbBtn);
        _NavToReg = findViewById(R.id.navToReg);


        _NavToReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Vendor_Login.this, Vendor_Signup.class);
                startActivity(i);
            }
        });

        _loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vendorLogin();
            }
        });
    }

    private void vendorLogin()
    {
        String email = _LoginEmail.getText().toString();
        String password = _LoginPassword.getText().toString();

        if (email.isEmpty())
            if (email.equals("") || password.equals("")) return;
        //Toast.makeText(this, "Login in progress...", Toast.LENGTH_SHORT).show();
        ProgressDialog pd = new ProgressDialog(Vendor_Login.this);
        pd.setMessage("Login in progress...");
        pd.show();


        //Create a Volley Request
        String url = "http://codingwithsunny.com/Shiftdrive/loginVendor.php";
        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject res=new JSONObject(response);
                            if(res.getString("status").equalsIgnoreCase("1"))
                            {
                                pd.dismiss();

                                //Setting User Credentials
                                Vendor_Details.setId(res.getString("id"));
                                Log.d("vendorid",Vendor_Details.getId());
                                Vendor_Details.setName(res.getString("name"));
                                Vendor_Details.setShopname(res.getString("shopname"));
                                Vendor_Details.setLocation(res.getString("location"));


                                //Perform Intent
                                Intent i = new Intent(Vendor_Login.this,Vendor_Dashboard.class);
                                finish();
                                startActivity(i);
                            }
                            else
                            {
                                showErrorDialog("Login attempt failed");
                                pd.dismiss();
                                Toast.makeText(Vendor_Login.this,res.getString("status"),Toast.LENGTH_LONG).show();
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
                data.put("email", email);
                data.put("password", password);
                return data;
            }
        };
        Volley.newRequestQueue(Vendor_Login.this).add(MyStringRequest);
    }

    //Show error on screen with an alert dialog
    private void showErrorDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Oops")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}