package com.usamamehmood.shiftdrive;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.wifi.hotspot2.pps.Credential;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

// This is the Register Activity class that sign up a new user.

public class RegisterActivity extends AppCompatActivity {

    EditText _regName,_regPhone,_regEmail,_regPassword;
    Button _RegisterBtn;
    TextView _BtnToSignin;

    private Boolean check = true;
    private Boolean check1 = true;

    String u_email,u_password,u_cpassword, u_name,u_phone;

    // Firebase instance variables
    private FirebaseAuth mAuth;

    FirebaseUser user;
    String uid;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Taking the user details from UI elements
        _regName = findViewById(R.id.regName);
        _regPhone = findViewById(R.id.regPhone);
        _regEmail = findViewById(R.id.regEmail);
        _regPassword = findViewById(R.id.regPassword);
        _RegisterBtn = findViewById(R.id.btnRegister);
        _BtnToSignin = findViewById(R.id.btntosignin);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("UserProfiles");
        mAuth = FirebaseAuth.getInstance();

        _BtnToSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(RegisterActivity.this,MainActivity.class);
                startActivity(i);
            }
        });

        _RegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //attemptRegistration();
                registerUser();
            }
        });
    }


    private void registerUser()
    {
        _regEmail.setError(null);
        _regPassword.setError(null);

        u_email = _regEmail.getText().toString();
        u_password = _regPassword.getText().toString();
        u_name = _regName.getText().toString();
        u_phone = _regPhone.getText().toString();


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

            ProgressDialog pd = new ProgressDialog(RegisterActivity.this);
            pd.setMessage("Registering...");
            pd.show();

            //Create a Volley Request
            String url = "http://codingwithsunny.com/Shiftdrive/registerUser.php";
            StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject res=new JSONObject(response);
                                Log.d("Sohaib","I'm in5");
                                //Toast.makeText(MainActivity.this,res.getString("password"),Toast.LENGTH_LONG).show();
                                //_pass.setText(res.getString("password"));
                                if(res.getString("status").equalsIgnoreCase("1"))
                                {
                                    pd.dismiss();

                                    //Setting User Credentials for further usage
                                    UserCredentials.setId(res.getString("id"));
                                    UserCredentials.setName(u_name.trim());
                                    UserCredentials.setEmail(u_email);
                                    UserCredentials.setPassword(u_password);
                                    UserCredentials.setPhone(u_phone);
                                    //Perform Intent
                                    Intent i = new Intent(RegisterActivity.this,HomeActivity.class);
                                    finish();
                                    startActivity(i);
                                }
                                else
                                {
                                    showErrorDialog("Registration attempt failed");
                                    pd.dismiss();
                                    Toast.makeText(RegisterActivity.this,res.getString("status"),Toast.LENGTH_LONG).show();
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
                            Log.d("Sohaib",error.toString());
                            showErrorDialog("Registration attempt failed");
                            pd.dismiss();
//This code is executed if there is an error.
                        }
                    }) {
                protected Map<String, String> getParams() {
                    Map<String, String> data = new HashMap<String, String>();
                    data.put("email", u_email);
                    data.put("password", u_password);
                    data.put("name", u_name);
                    data.put("phone", u_phone);
                    Log.d("Sohaib","I'm in2");
                    return data;
                }
            };
            Volley.newRequestQueue(RegisterActivity.this).add(MyStringRequest);

        }
    }

    private void attemptRegistration() {
        _regEmail.setError(null);
        _regPassword.setError(null);

        u_email = _regEmail.getText().toString();
        u_password = _regPassword.getText().toString();
        u_name = _regName.getText().toString();
        u_phone = _regPhone.getText().toString();

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

            // Call create FirebaseUser()
            createFirebaseUser();
        }
    }

    private boolean isEmailValid(String email) {
        // You can add more checking logic here.
        return email.contains("@");
    }

    private void createFirebaseUser() {

        ProgressDialog pd = new ProgressDialog(RegisterActivity.this);
        pd.setMessage("Registering...");
        pd.show();

        //dialog.setMessage("Registering....");
        //dialog.show();

        String _email = _regEmail.getText().toString();
        String _password = _regPassword.getText().toString();

        Log.d("Usama",_email+" "+_password);

        mAuth.createUserWithEmailAndPassword(_email, _password).addOnCompleteListener(RegisterActivity.this,
                new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()){
                            //Log.d("1122", "user creation failed");
                            showErrorDialog("Registration attempt failed");
                            pd.dismiss();
                        } else {

                            //Now here, add user in database except image.
                            //invoke a method here to add user in database.
                            user = FirebaseAuth.getInstance().getCurrentUser();
                            uid = user.getUid();

                            UserCredentials.setId(uid);
                            UserCredentials.setName(u_name);
                            UserCredentials.setEmail(u_email);
                            UserCredentials.setPassword(u_password);
                            UserCredentials.setPhone(u_phone);

                            User _user = new User(u_name,u_phone,u_email,u_password,"",uid);

                            reference.child(uid).setValue(_user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        //Dismiss Dialog
                                        pd.dismiss();

                                        Intent i = new Intent(RegisterActivity.this,HomeActivity.class);
                                        finish();
                                        startActivity(i);

                                    }
                                    else
                                    {
                                        pd.dismiss();
                                        Toast.makeText(RegisterActivity.this,"Something went wrong...", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });


                        }
                    }
                });
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