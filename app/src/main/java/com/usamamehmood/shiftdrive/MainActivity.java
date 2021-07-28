package com.usamamehmood.shiftdrive;

import androidx.annotation.NonNull;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

//This is the Login screen of user side UI

public class MainActivity extends AppCompatActivity {

    EditText _LoginEmail, _LoginPassword;
    Button _loginBtn;
    TextView _ForgetPassword, _NavToReg;

    private FirebaseAuth mAuth;
    FirebaseUser user;
    String uid;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _LoginEmail = findViewById(R.id.loginEmail);
        _LoginPassword = findViewById(R.id.loginPassword);
        _loginBtn = findViewById(R.id.loginButton);
        _ForgetPassword = findViewById(R.id.forgetPasswordbBtn);
        _NavToReg = findViewById(R.id.navToReg);

        mAuth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("UserProfiles");

        _NavToReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(i);
            }
        });

        _loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //attemptLogin();
                userLogin();
            }
        });
    }

    private void userLogin()
    {
        String email = _LoginEmail.getText().toString();
        String password = _LoginPassword.getText().toString();

        if (email.isEmpty())
            if (email.equals("") || password.equals("")) return;
        //Toast.makeText(this, "Login in progress...", Toast.LENGTH_SHORT).show();
        ProgressDialog pd = new ProgressDialog(MainActivity.this);
        pd.setMessage("Login in progress...");
        pd.show();


        //Create a Volley Request
        String url = "http://codingwithsunny.com/Shiftdrive/loginUser.php";
        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject res=new JSONObject(response);
                            //Toast.makeText(MainActivity.this,res.getString("password"),Toast.LENGTH_LONG).show();
                            //_pass.setText(res.getString("password"));
                            if(res.getString("status").equalsIgnoreCase("1"))
                            {
                                pd.dismiss();

                                //Setting User Credentials
                                UserCredentials.setId(res.getString("id"));
                                UserCredentials.setName(res.getString("name").toString().trim());
                                UserCredentials.setEmail(res.getString("email"));
                                UserCredentials.setPassword(res.getString("password"));
                                UserCredentials.setPhone(res.getString("phone"));

                                //Perform Intent
                                Intent i = new Intent(MainActivity.this,HomeActivity.class);
                                finish();
                                startActivity(i);
                            }
                            else
                            {
                                showErrorDialog("Login attempt failed");
                                pd.dismiss();
                                Toast.makeText(MainActivity.this,res.getString("status"),Toast.LENGTH_LONG).show();
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
                data.put("email", email);
                data.put("password", password);
                return data;
            }
        };
        Volley.newRequestQueue(MainActivity.this).add(MyStringRequest);
    }

    private void attemptLogin()
    {
        String email = _LoginEmail.getText().toString();
        String password = _LoginPassword.getText().toString();

        if (email.isEmpty())
            if (email.equals("") || password.equals("")) return;
        //Toast.makeText(this, "Login in progress...", Toast.LENGTH_SHORT).show();
        ProgressDialog pd = new ProgressDialog(MainActivity.this);
        pd.setMessage("Login in progress...");
        pd.show();

        //Use FirebaseAuth to sign in with email & password
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {


                if (!task.isSuccessful()) {
                    pd.dismiss();
                    showErrorDialog("There was a problem signing in");
                }
                else {

                    //Here User is authenticated. Now Check Whether this user is also a admin or not. If admin, then show LoginAs Screen, otherwise manually logged Home activity.
                    user = FirebaseAuth.getInstance().getCurrentUser();
                    uid = user.getUid();
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            for(DataSnapshot snapshot1 : snapshot.getChildren() )
                            {

                                User _user = snapshot1.getValue(User.class);

                                if(_user.getId().equals(uid))
                                {
                                    UserCredentials.setPhone(_user.getPhone());
                                    UserCredentials.setPassword(_user.getPassword());
                                    UserCredentials.setEmail(_user.getEmail());
                                    UserCredentials.setName(_user.getName());
                                    UserCredentials.setId(uid);

                                    pd.dismiss();

                                    Intent i = new Intent(MainActivity.this, HomeActivity.class);
                                    finish();
                                    startActivity(i);
                                    break;
                                }

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

            }
        });
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