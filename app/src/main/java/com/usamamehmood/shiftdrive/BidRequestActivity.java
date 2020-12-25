package com.usamamehmood.shiftdrive;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class BidRequestActivity extends AppCompatActivity {

    EditText b_make, b_model, b_damage;
    Button sendBRequest;
    ImageView damageImageShowHere;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bid_request);

        damageImageShowHere = findViewById(R.id.ImageCarBid);
        sendBRequest = findViewById(R.id.sendBidRequest);
        b_make = findViewById(R.id.bidmake_);
        b_model = findViewById(R.id.bidmodel_);
        b_damage = findViewById(R.id.damagedArea_);




    }
}