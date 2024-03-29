package com.usamamehmood.shiftdrive;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

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
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

//This class functionally detects the damage of car

public class DetectCarDamage extends AppCompatActivity {

    ImageView imgDisplayHere; // This view displays the selected image
    Button btnImgUpload, btnImgSelect;
    Uri uplaodImageUri;
    Button _btnGetVendorCost;
    String enco_string;

    TextView _damageResultReport;
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;

    Handler handler; //This handler connects the socket execution thread with main UI thread.

    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detect_car_damage);

        _damageResultReport = findViewById(R.id.damageresultreport);
        //btnImgUpload = findViewById(R.id.DetectDamageCarbtn);
        btnImgSelect = findViewById(R.id.selectDamageImageBtn);
        imgDisplayHere = findViewById(R.id.selectDamageCar);
        //resultCardView = findViewById(R.id.cardviewDamageResult); //update this
        _btnGetVendorCost = findViewById(R.id.getVenderBid);
        pd = new ProgressDialog(DetectCarDamage.this);
        handler=new Handler();

        if(CarImage.getImg()!=null)
        {
            uplaodImageUri = CarImage.getImg();
            CarDetails.setDamagedCarImg(uplaodImageUri);
            imgDisplayHere.setImageURI(CarImage.getImg());
            DetectDamageOnCar();
        }


        _btnGetVendorCost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(uplaodImageUri!=null) {

                    Intent i = new Intent(DetectCarDamage.this, BidRequestActivity.class);
                    startActivity(i);
                }
                else {
                    Toast.makeText(DetectCarDamage.this,"Please Select an Image First",Toast.LENGTH_LONG).show();
                }
            }
        });


        btnImgSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
                //resultlayout.setVisibility(View.INVISIBLE);
                //resultCardView.setVisibility(View.INVISIBLE);
            }
        });

    }


    private void DetectDamageOnCar()
    {
        if(uplaodImageUri!=null)
        {
            CarDetails.setDamagedCarImg(uplaodImageUri);
            pd.setMessage("Detecting...");
            pd.show();

            try {
                final InputStream imageStream = getContentResolver().openInputStream(uplaodImageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                //imgDisplayHere.setImageBitmap(selectedImage);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                byte[] array = byteArrayOutputStream.toByteArray();

                sendImage(array);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public class SendImageClient extends AsyncTask<byte[], Void, Void> {


        @Override
        protected Void doInBackground(byte[]... voids) {

            try {
                Socket socket= new Socket("192.168.10.2",5008);

                OutputStream out=socket.getOutputStream();
                DataOutputStream dataOutputStream=new DataOutputStream(out);
                //dataOutputStream.writeInt(voids[0].length);
                dataOutputStream.write(voids[0],0,voids[0].length);

                BufferedReader data=new BufferedReader(new InputStreamReader(socket.getInputStream()));
                final String st=data.readLine();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(st.trim().length()!=0)
                        {
                            try {
                                JSONObject recData=new JSONObject(st);
                                String lbl=recData.getString("label");

                                Toast.makeText(DetectCarDamage.this,lbl,Toast.LENGTH_LONG).show();
                                Log.d("Jres",lbl);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

                dataOutputStream.close();
                out.close();
                //socket.close();

            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }
    }

    private void sendImage(final byte[] array) {
        _damageResultReport.setVisibility(View.VISIBLE);
        _damageResultReport.setText("Detecting...");
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    Socket socket= new Socket("192.168.10.2",5008);
                    OutputStream out=socket.getOutputStream();
                    DataOutputStream dataOutputStream=new DataOutputStream(out);
                    dataOutputStream.write(array);
                    dataOutputStream.close();
                    out.close();
                    socket.close();
                    System.out.println("Sent");


                    //Inet4Address inet4Address = (Inet4Address) Inet4Address.getByName("192.168.0.60");

                    ServerSocket serverSocket = new ServerSocket(5009);
                    System.out.println("Binded with client");
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Request accepted");
                    DataInputStream dataInputStream  = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
                    final String response = dataInputStream.readLine();
                    System.out.println("Usama147 "+response);
                    clientSocket.close();
                    dataInputStream.close();
                    serverSocket.close();

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(response.length()!=0)
                            {
                                pd.dismiss();
                                enco_string = response;
                                String encodedImage = enco_string;
                                byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
                                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                imgDisplayHere.setImageBitmap(decodedByte);
                                _damageResultReport.setText("Damaged part: Bumper");
                                _damageResultReport.setVisibility(View.INVISIBLE);

                                //_Result_.setText("Result");
                                //_Result_Car_.setText(response);

                                //set c_make and c_model;

                                //Toast.makeText(RecognizeCarActivity.this, response,Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                    pd.dismiss();
                }
            }
        });
        thread.start();
    }

    private void selectImage()
    {
        final CharSequence[] option = {"Take Photo","Choose From Gallery","Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(DetectCarDamage.this);
        builder.setTitle("Select Photo!");
        builder.setItems(option, new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(option[i].equals("Take Photo"))
                {
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
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto , 2);
                }
                else if(option[i].equals("Cancel")){
                    dialogInterface.dismiss();
                }
            }
        });
        builder.show();
    }

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


    //ActivityResultForImages


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK)
        {
            if(requestCode == 2)
            {
                Uri selectedImage = data.getData();
                imgDisplayHere.setImageURI(selectedImage);
                uplaodImageUri = selectedImage;
                //resultCardView.setVisibility(View.INVISIBLE);
                _damageResultReport.setVisibility(View.INVISIBLE);
                DetectDamageOnCar();
                //_Result_.setText("");
                //_Result_Car_.setText("");
            }
            else if(requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK)
            {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                uplaodImageUri = getImageUri(DetectCarDamage.this,photo);
                imgDisplayHere.setImageBitmap(photo);
                //resultCardView.setVisibility(View.INVISIBLE);
                _damageResultReport.setVisibility(View.INVISIBLE);
                DetectDamageOnCar();
                //_Result_.setText("");
                //_Result_Car_.setText("");
            }
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    //Function to convert Image into ByteArray
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