package com.usamamehmood.shiftdrive;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

//This class displays the bid response on User side UI

public class DisplayRequestedBidResponse extends AppCompatActivity {

    ImageView _car_image;
    TextView _model, _damage, _note_for, _note_from, _bid_amount;
    Button _btn_book_vendor, _btn_show_vendor;
    String vendor_id, enco_string;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_requested_bid_response);

        _car_image = findViewById(R.id.ImageCarBidRequestResponse);
        _model = findViewById(R.id.bidmodelresponse);
        _damage = findViewById(R.id.damagedArearesponse);
        _note_for = findViewById(R.id.noteforvendorresponse);
        _note_from = findViewById(R.id.notefromvendorresponse);
        _bid_amount = findViewById(R.id.bidamountresponse);

        _btn_book_vendor = findViewById(R.id.bookvendorresponse);
        _btn_show_vendor = findViewById(R.id.viewvendordetails);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        //checking whether the bundle is empty or not from previous UI
        if(bundle != null){
            _model.setText(bundle.getString("makemodel"));
            _damage.setText(bundle.getString("damage"));
            _note_for.setText(bundle.getString("noteforvendor"));
            _note_from.setText(bundle.getString("notefromvendor"));
            _bid_amount.setText(bundle.getString("price"));
            vendor_id = bundle.getString("vendor_id");

            enco_string = bundle.getString("image");
            String encodedImage = enco_string;
            byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            _car_image.setImageBitmap(decodedByte);

        }

        //This function displays the vendor details
        _btn_show_vendor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DisplayRequestedBidResponse.this,VendorDetailsScreen.class);
                i.putExtra("vendor_id",bundle.getString("vendor_id"));
                startActivity(i);
            }
        });

        //This function displays the user side booking UI
        _btn_book_vendor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(DisplayRequestedBidResponse.this,"Hello World",Toast.LENGTH_SHORT).show();
                Intent i = new Intent(DisplayRequestedBidResponse.this,UsersideVenderBookingActivity.class);
                i.putExtra("vendor_id",bundle.getString("vendor_id"));
                startActivity(i);
            }
        });



    }
}