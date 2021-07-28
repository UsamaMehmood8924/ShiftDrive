package com.usamamehmood.shiftdrive;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

//This class displays the vendor dashboard
public class Vendor_Dashboard extends AppCompatActivity {

    LinearLayout btnViewBidRequest, btnViewNewBookings, btnlogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor__dashboard);

        //connecting UI elements
        btnViewBidRequest = findViewById(R.id.viewbidrequestBtn);
        btnViewNewBookings = findViewById(R.id.viewnewbookingsBtn);
        btnlogout = findViewById(R.id.vendorlogout);
        btnViewBidRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Vendor_Dashboard.this, View_Bid_Requests.class);
                startActivity(i);
            }
        });

        btnlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Vendor_Dashboard.this,LoginAs.class);
                startActivity(i);
                finish();
            }
        });


    }
}