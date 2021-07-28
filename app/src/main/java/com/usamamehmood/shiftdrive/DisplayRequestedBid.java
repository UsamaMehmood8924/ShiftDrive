package com.usamamehmood.shiftdrive;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

//This class displays the bidding details to vendor side UI

public class DisplayRequestedBid extends AppCompatActivity {

    EditText _makemodel, _damage, _noteforvendor, _notefromvendor, _price;
    String enco_string;
    ImageView showImage;
    String clientid = "", bidid = "";
    Button sendBidResponse;
    String bidPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_requested_bid);

        //Connection UI
        _makemodel = findViewById(R.id.bidmodelrequest);
        _damage = findViewById(R.id.damagedArearequest);
        _noteforvendor = findViewById(R.id.noteforvendorrequest);
        _notefromvendor = findViewById(R.id.notefromvendorrequest);
        _price = findViewById(R.id.bidamount);
        showImage = findViewById(R.id.ImageCarBidRequest);
        sendBidResponse = findViewById(R.id.sendBidresponse);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        //Checking if bundle is empty from previous UI or not.
        if(bundle != null){
            _makemodel.setText(bundle.getString("makemodel"));
            _damage.setText(bundle.getString("damage"));
            _noteforvendor.setText(bundle.getString("noteforvendor"));
            clientid = bundle.getString("clientid");
            bidid = bundle.getString("bidid");

            //Converting image to bitmap
            enco_string = bundle.getString("carimage");
            String encodedImage = enco_string;
            byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            showImage.setImageBitmap(decodedByte);
        }

        //Calling a function to send bidding response to the user
        sendBidResponse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendResponse();
            }
        });

    }

    //This function takes all the details from UI needed to send the bid response and then calls POST API Request
    private void sendResponse()
    {
        bidPrice = _price.getText().toString();
        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(bidPrice)) {
            _price.setError("This Field is required");
            focusView = _price;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {

            ProgressDialog pd = new ProgressDialog(DisplayRequestedBid.this);
            pd.setMessage("Sending Response...");
            pd.show();

            //Create a Volley Request
            String url = "http://codingwithsunny.com/Shiftdrive/bidResponse.php";
            StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject res=new JSONObject(response);
                                if(res.getString("status").equalsIgnoreCase("1"))
                                {
                                    pd.dismiss();

                                    //Perform Intent

                                    Toast.makeText(DisplayRequestedBid.this,"Response Sent",Toast.LENGTH_LONG).show();
                                    Intent i = new Intent(DisplayRequestedBid.this,Vendor_Dashboard.class);
                                    finish();
                                    startActivity(i);
                                }
                                else
                                {
                                    showErrorDialog("Attempt failed");
                                    pd.dismiss();
                                    Toast.makeText(DisplayRequestedBid.this,res.getString("status"),Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //This code is executed if there is an error.
                            showErrorDialog("Attempt failed");
                            pd.dismiss();

                        }
                    }) {
                //adding details in hashmap
                protected Map<String, String> getParams() {
                    Map<String, String> data = new HashMap<String, String>();
                    data.put("vendor_id", Vendor_Details.getId());
                    data.put("client_id", clientid);
                    data.put("req_id", bidid);
                    data.put("notefromvendor", _notefromvendor.getText().toString());
                    data.put("price", bidPrice);

                    return data;
                }
            };
            Volley.newRequestQueue(DisplayRequestedBid.this).add(MyStringRequest);

        }

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