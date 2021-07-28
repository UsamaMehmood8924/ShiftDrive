package com.usamamehmood.shiftdrive;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class BidRequestActivity extends AppCompatActivity {

    EditText b_model, b_damage, b_note;
    Button sendBRequest; // A button that calls rest API for sending bid request
    Uri uplaodImageUri; //Uniform Resource identifier for identifying the selected image resource location.
    ImageView damageImageShowHere; //Displays the damaged Imaged of car
    Bitmap imgBitmap;
    String encodeImageString;
    ProgressDialog pd; //Progress dialog executes when user is waiting for some response from the system.
    String bid_model, bid_damage, bid_note; //car model, damaged area, note for vendor

    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bid_request);

        //connection form UI elements
        damageImageShowHere = findViewById(R.id.ImageCarBid);
        sendBRequest = findViewById(R.id.sendBidRequest);
        b_model = findViewById(R.id.bidmodel_);
        b_damage = findViewById(R.id.damagedArea_);
        b_note = findViewById(R.id.noteforvendor_);

        //Checking whether the image or car selected or not
        if(CarDetails.getDamagedCarImg()!=null)
        {
            damageImageShowHere.setImageURI(CarDetails.getDamagedCarImg());
            uplaodImageUri = CarDetails.getDamagedCarImg();
        }

        //Checking whether the make of model of car fetched or not.
        if(CarDetails.getMake_model()!="" && CarDetails.getMake_model()!="Fetching Results...")
        {
            b_model.setText(CarDetails.getMake_model());
        }

        //Calling a function to get image from gallery or from camera
        damageImageShowHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(); //This function gets the image from android device.
            }
        });

        //Calling a function to send POST API bid request
        sendBRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBidRequest();
            }
        });
    }

    //This function will bidding details to the database
    private void sendBidRequest()
    {
        //Getting details from UI elements
        bid_damage = b_damage.getText().toString();
        bid_model = b_model.getText().toString();
        bid_note = b_note.getText().toString();

        boolean cancel = false;
        View focusView = null;

        //Checking the status of UI fields
        if (TextUtils.isEmpty(bid_damage)) {
            b_damage.setError("This Field is required");
            focusView = b_damage;
            cancel = true;
        }
        if (TextUtils.isEmpty(bid_model)) {
            b_model.setError("This Field is required");
            focusView = b_model;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {

            ProgressDialog pd = new ProgressDialog(BidRequestActivity.this);
            pd.setMessage("Requesting...");
            pd.show();

            try
            {
                InputStream inputStream=getContentResolver().openInputStream(uplaodImageUri);
                imgBitmap = BitmapFactory.decodeStream(inputStream);
                encodeBitmapImage(imgBitmap);
            }catch (Exception ex)
            {

            }

            //Create a Volley Request
            String url = "http://codingwithsunny.com/Shiftdrive/bidRequest.php";
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

                                    Toast.makeText(BidRequestActivity.this,"Request Sent",Toast.LENGTH_LONG).show();

                                    Intent i = new Intent(BidRequestActivity.this,HomeActivity.class);
                                    finish();

                                    callPushNotification();

                                    startActivity(i);
                                }
                                else
                                {
                                    showErrorDialog("Bid request failed");
                                    pd.dismiss();
                                    Toast.makeText(BidRequestActivity.this,res.getString("status"),Toast.LENGTH_LONG).show();
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
                            showErrorDialog("Bid request failed");
                            pd.dismiss();

                        }
                    }) {
                //setting the details in Hashmap
                protected Map<String, String> getParams() {
                    Map<String, String> data = new HashMap<String, String>();
                    data.put("user_id", UserCredentials.getId());
                    data.put("makemodel", bid_model);
                    data.put("damage", bid_damage);
                    data.put("noteforvendor", bid_note);
                    data.put("location", "Okara");
                    data.put("upload",encodeImageString);
                    return data;
                }
            };
            Volley.newRequestQueue(BidRequestActivity.this).add(MyStringRequest);

        }
    }

    //when bid request sent successfully, this function is called
    private void callPushNotification()
    {
        //Create a Volley Request
        String url = "http://codingwithsunny.com/Shiftdrive/notifications.php";
        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                },
                new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //This code is executed if there is an error.


                    }
                }) {
            protected Map<String, String> getParams() {
                Map<String, String> data = new HashMap<String, String>();
                return data;
            }
        };
        Volley.newRequestQueue(BidRequestActivity.this).add(MyStringRequest);
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

    //this function converts image to base64 byte string
    private void encodeBitmapImage(Bitmap bitmap)
    {
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        byte[] bytesofimage = byteArrayOutputStream.toByteArray();
        encodeImageString = android.util.Base64.encodeToString(bytesofimage, Base64.DEFAULT);
    }



    /*
     * SelectImage() function show a dialog box that asks the user to select image from gallery or take a photo from camera.
     * If user clicks on 'Take Photo' then a prompt will open and asks for the permission to open camera.
     * If user clicks on 'Choose From Gallery', the gallery will be open.
     * */
    private void selectImage()
    {
        final CharSequence[] option = {"Take Photo","Choose From Gallery","Cancel"}; //setting an array of some strings "options"
        AlertDialog.Builder builder = new AlertDialog.Builder(BidRequestActivity.this); //Creating a dialog "Alert Builder Dialog"
        builder.setTitle("Select Photo!"); //Setting the dialog title
        builder.setItems(option, new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(option[i].equals("Take Photo"))
                {
                    //Asking for permission to open camera. If permission was already granted then else block is executed otherwise if block will execute.
                    if (checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                    {
                        requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                    }
                    else
                    {
                        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, CAMERA_REQUEST);
                    }
                }
                else if(option[i].equals("Choose From Gallery"))
                {
                    //An intent is created to open the gallery.
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto , 2);
                }
                else if(option[i].equals("Cancel")){
                    //closes the dialog box.
                    dialogInterface.dismiss();
                }
            }
        });
        builder.show(); //Executes to display dialog box.
    }


    //This override function sets the granted permission.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
            else
            {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }


    //This override function selects the image from gallery or clicked photo from camera.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK)
        {

            if(requestCode == 2)//if user has selected 'Choose from Gallery' option.
            {
                Uri selectedImage = data.getData(); //Getting the image URI from gallery.
                damageImageShowHere.setImageURI(selectedImage); //Displays the image on UI.
                uplaodImageUri = selectedImage; //Assigning Select Image's URI to uploadImageImageUri variable.
                //resultCardView.setVisibility(View.INVISIBLE); //Since, user has selected a new image, so system sets the visibility of result card view to  'invisible'
            }
            else if(requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK)//if user has selected 'Take Photo'
            {
                Bitmap photo = (Bitmap) data.getExtras().get("data"); //Getting the image bitmap from camera.
                uplaodImageUri = getImageUri(BidRequestActivity.this,photo); //Assigning Clicked Image's URI to uploadImageImageUri variable.
                damageImageShowHere.setImageBitmap(photo); //Displays the image on UI.
                //resultCardView.setVisibility(View.INVISIBLE);//Since, user has selected a new image, so system sets the visibility of result card view to  'invisible'
            }
        }
    }

    //This function converts the Bitmap into Uri.
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    //Function to convert Image Uri into ByteArray
    public byte[] getByteArray(Uri image)
    {
        Bitmap pic = null;
        try{
            pic = MediaStore.Images.Media.getBitmap(getContentResolver(),image);
        }
        catch (IOException e){
            e.printStackTrace();
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        pic.compress(Bitmap.CompressFormat.PNG,90,byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }


}