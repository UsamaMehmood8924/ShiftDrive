package com.usamamehmood.shiftdrive;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
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

//This class display's the vendor details on user side

public class VendorDetailsScreen extends AppCompatActivity {

    TextView _shopname, _name, _location, _contact;
    LinearLayout detailsLayout;
    String vendor_id;
    Button tracklocation;

    String cloc, dloc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_details_screen);

        //Connection from UI
        _shopname = findViewById(R.id.details_shopname);
        _name = findViewById(R.id.details_name);
        _location = findViewById(R.id.details_location);
        _contact = findViewById(R.id.details_contact);
        detailsLayout = findViewById(R.id.details_layout);
        tracklocation = findViewById(R.id.tracklocation);

        detailsLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+_contact.getText().toString()));
                startActivity(intent);
                return false;
            }
        });

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle != null){
            vendor_id = bundle.getString("vendor_id");
        }

        getVendorDetails();


        tracklocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DisplayTrack();
            }
        });
    }

    private void DisplayTrack()
    {

        try{
            Uri uri = Uri.parse("https://maps.app.goo.gl/teHKmQBcUiqpyPpN9");
            Intent intent = new Intent(Intent.ACTION_VIEW,uri);

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(intent);

        } catch (Exception e) {
            e.printStackTrace();

        }
    }



    private void getVendorDetails()
    {
        ProgressDialog pd = new ProgressDialog(VendorDetailsScreen.this);
        pd.setMessage("Registering...");
        pd.show();
//Create a Volley Request
        String url = "http://codingwithsunny.com/Shiftdrive/getVendorDetails.php";
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
                                _shopname.setText(res.getString("shopname"));
                                _name.setText(res.getString("name"));
                                _location.setText(res.getString("location"));
                                _contact.setText(res.getString("contact"));
                            }
                            else
                            {
                                pd.dismiss();
                                Toast.makeText(VendorDetailsScreen.this,res.getString("status"),Toast.LENGTH_LONG).show();
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
                        pd.dismiss();
//This code is executed if there is an error.
                    }
                }) {
            protected Map<String, String> getParams() {
                Map<String, String> data = new HashMap<String, String>();
                data.put("vendor_id", vendor_id);
                return data;
            }
        };
        Volley.newRequestQueue(VendorDetailsScreen.this).add(MyStringRequest);

    }
}