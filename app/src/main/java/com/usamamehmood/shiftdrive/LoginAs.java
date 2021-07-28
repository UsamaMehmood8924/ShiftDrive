package com.usamamehmood.shiftdrive;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

//This is the start up UI screen that asks the user whether he is car owner or vendor

public class LoginAs extends AppCompatActivity {

    Button _loginasowner, _loginasvendor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_as);

        _loginasowner = findViewById(R.id.loginasowner);
        _loginasvendor = findViewById(R.id.loginasvendor);


        _loginasowner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginAs.this, MainActivity.class);
                startActivity(i);
            }
        });

        _loginasvendor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginAs.this, Vendor_Login.class);
                startActivity(i);
            }
        });


    }
}