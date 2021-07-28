package com.usamamehmood.shiftdrive;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.Manifest;
import android.app.Activity;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

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
import java.util.HashMap;
import java.util.Map;

public class RecognizeCarActivity extends AppCompatActivity {

    ImageView imgDisplayHere; // This view displays the selected image
    Button btnToDamageDetect, btnImgSelect; //Buttons for 'Select Image' and 'Recognize this Car' respectively.
    Uri uplaodImageUri; //Uniform Resource identifier for identifying the selected image resource location.
    TextView _Result_; //This text field shows the car result.
    TextView _notStatisfied_text;
    Spinner dropdown_results;
    Bitmap imgBitmap;
    String encodeImageString;


    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;

    Handler handler; //This handler connects the socket execution thread with main UI thread.
    RelativeLayout resultCardView; //resultCardView holds the TextView 'c_make' and 'c_model'.
    TextView c_make, c_model; //These text field shows the make and model of selected car.
    ProgressDialog pd; //Progress dialog executes when user is waiting for some response from the system.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognize_car);

        //* Here we are linking the variables with UI field references (ID's)
        _Result_ = findViewById(R.id.recognizeResult);
        c_make = findViewById(R.id.carMake);
//        c_model = findViewById(R.id.carModel);
        btnToDamageDetect = findViewById(R.id.nextBtn);
        btnImgSelect = findViewById(R.id.selectRecognizeImageBtn);
        imgDisplayHere = findViewById(R.id.selectRecognizeCar);
        resultCardView = findViewById(R.id.makenmodelresult);
        _notStatisfied_text = findViewById(R.id.notSatisfied);
        dropdown_results = findViewById(R.id.spinner1);

        ArrayAdapter<String> adpt = new ArrayAdapter<>(
                RecognizeCarActivity.this,
                R.layout.custom_spinner,
                getResources().getStringArray(R.array.car_list)
        );
        adpt.setDropDownViewResource(R.layout.custom_spinner_dropdown);
        dropdown_results.setAdapter(adpt);


        dropdown_results.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = dropdown_results.getItemAtPosition(position).toString();

                if(position!=0)
                {
                    c_make.setText(selectedItem);
                    CarDetails.setMake_model(c_make.getText().toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Initializing the Progress Dialog instance.
        pd = new ProgressDialog(RecognizeCarActivity.this);

        //Initializing the handler instance.
        handler=new Handler();

        //This function Executes when user press 'Select Image' Button.
        btnImgSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage(); //This function gets the image from android device.
            }
        });

        btnToDamageDetect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(c_make.getText()!="Fetching Results..." && c_make.getText()!="")
                {
                    CarDetails.setMake_model(c_make.getText().toString());
                }
                Intent i = new Intent(RecognizeCarActivity.this, DetectCarDamage.class);
                startActivity(i);
            }
        });


    }


    private void performCarRecognition()
    {
        Log.d("Asad: ","PerformCarRecognition");
        if(uplaodImageUri!=null) //Checks whether user has selected an image or not. Returns True if image is selected.
        {
            c_make.setText("Fetching Results..."); //Setting c_make text field with this text till the response from server
            //c_model.setText("Fetching Results..."); //Setting c_model text field with this text till the response from server
            pd.setMessage("Recognizing..."); //Progress Dialog shows this text while the system is processing and waits for the response from server.
            pd.show(); //Displays the progress Dialog on GUI.
            uploadImageToServer();


                    try {
                        final InputStream imageStream = getContentResolver().openInputStream(uplaodImageUri); //Getting the Image Uri in InputStream
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream); //Converting InputStream (Image Uri) into Bitmap.
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(); //Creating ByteArrayOutputStream to convert the Bitmap into byte array.
                        selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream); //Compressing the Bitmap to byte array.
                        byte[] array = byteArrayOutputStream.toByteArray(); //array variable stores the byte array of image bitmap.
                        Log.d("Wahaj1","I'm in");
                        sendImage(array); //This function sends the image to server.

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
        }
    }

    /*
    * sendImage Function receives the array of bytes.
    * Image is sent to server in the form of bytes via sockets.
    *
    * "Phase 1"
    * When user is sending an image to ShiftDrive machine learning trained model, The user device is acting as a client and model is acting as a server.
    * ShiftDrive Car recognition model is currently hosted on laptop.
    * In sendImage Function, a socket is created to sending the image to server.
    * "Laptop - wifi Router" ip and receiving port number of the server is assigned to the created client socket.
    *
    * "Phase 2"
    * When user is expecting some response from the ShiftDrive model, then user device is acting as a server and hosted machine(Laptop) is acting as a client.
    * So, a server socket is created that has assigned a port number for receiving data from the "device - wifi Router" ip.
    * */

    private void sendImage(final byte[] array) {
        resultCardView.setVisibility(View.VISIBLE); //Card View's visibility is set to 'On' to display the results.
        Thread thread=new Thread(new Runnable() { //A new thread is created for background Process( sending and retrieving data)
            @Override
            public void run() {
                Log.d("Wahaj2","I'm in");
                try {
                    Log.d("Wahaj4","I'm in");
                    //Phase 1 - Sending Image from user device to trained model. "Client - Server" Communication.
                    //Socket socket= new Socket("192.168.1.103",5013); //Server Ip address and server listening port number
                    Socket socket= new Socket("192.168.10.2",5003); //Server Ip address and server listening port number
                    Log.d("Wahaj","I'm in");
                    OutputStream out=socket.getOutputStream(); //OutputStream is created to send data from client socket.
                    DataOutputStream dataOutputStream=new DataOutputStream(out); //Initializing the DataOutputStream
                    dataOutputStream.write(array); //Sending image byte array from client socket.
                    dataOutputStream.close(); //Closing the DataOutputStream
                    out.close(); //Closing the OutputStream
                    socket.close(); //Closing the client's socket.

                    //Phase 2 - Receiving the response from trained model. "Server - Client" Communication.
                    ServerSocket serverSocket = new ServerSocket(5006); //Creating a port for listening from client.
                    Socket clientSocket = serverSocket.accept(); //client socket is created to receive data from client.
                    DataInputStream dataInputStream  = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream())); //DataInputStream to receive input stream from listening port
                    final String response = dataInputStream.readLine(); //Receiving the data from input stream and storing it in a string variable to display on UI.
                    clientSocket.close(); //closing the client Socket.
                    dataInputStream.close(); //closing the DataInputStream
                    serverSocket.close(); //closing the server socket.

                    /*
                    Now, the system has successfully sent an Image to server and get a response in string variable named 'response'.
                     */

                    //handler.post() connects this running thread with main UI thread.
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(response.trim().length()!=0) //If response variable contains some string.
                            {
                                pd.dismiss(); //Progress dialog is dismissed to display the results.

                                //These checks verifies which car model is present in the response.
                                //c_make.setText() displays the make of car.
                                //c_model.setText() display the model of car.
                                if(response.equalsIgnoreCase("Civic"))
                                {
                                    c_make.setText("Honda Civic");
                                    //c_model.setText("Civic");
                                }
                                else if(response.equalsIgnoreCase("Corolla"))
                                {
                                    c_make.setText("Toyota Corolla");
                                    //c_model.setText("Corolla");
                                }
                                else if(response.equalsIgnoreCase("Wagon R"))
                                {
                                    c_make.setText("Suzuki Wagon R");
                                    //c_model.setText("Wagon R");
                                }
                                else if(response.equalsIgnoreCase("Mehran"))
                                {
                                    c_make.setText("Suzuki Mehran");
                                    //c_model.setText("Mehran");
                                }
                                else
                                {
                                    c_make.setText("Error in recognition...");
                                    //c_model.setText("Error in recognition...");
                                }

                            }
                        }
                    });

                } catch (IOException e) { //If there arises some error while connecting with the server.
                    e.printStackTrace();
                    pd.dismiss();
                }
            }
        });
        thread.start();//Thread is executed.
        _notStatisfied_text.setVisibility(View.VISIBLE);
        dropdown_results.setVisibility(View.VISIBLE);
    }


    /*
    * SelectImage() function show a dialog box that asks the user to select image from gallery or take a photo from camera.
    * If user clicks on 'Take Photo' then a prompt will open and asks for the permission to open camera.
    * If user clicks on 'Choose From Gallery', the gallery will be open.
    * */
    private void selectImage()
    {
        final CharSequence[] option = {"Take Photo","Choose From Gallery","Cancel"}; //setting an array of some strings "options"
        AlertDialog.Builder builder = new AlertDialog.Builder(RecognizeCarActivity.this); //Creating a dialog "Alert Builder Dialog"
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
                CarImage.setImg(selectedImage);
                imgDisplayHere.setImageURI(selectedImage); //Displays the image on UI.
                uplaodImageUri = selectedImage; //Assigning Select Image's URI to uploadImageImageUri variable.
                resultCardView.setVisibility(View.INVISIBLE); //Since, user has selected a new image, so system sets the visibility of result card view to  'invisible'
                performCarRecognition();
            }
            else if(requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK)//if user has selected 'Take Photo'
            {
                Bitmap photo = (Bitmap) data.getExtras().get("data"); //Getting the image bitmap from camera.
                uplaodImageUri = getImageUri(RecognizeCarActivity.this,photo); //Assigning Clicked Image's URI to uploadImageImageUri variable.
                imgDisplayHere.setImageBitmap(photo); //Displays the image on UI.
                resultCardView.setVisibility(View.INVISIBLE);//Since, user has selected a new image, so system sets the visibility of result card view to  'invisible'
                performCarRecognition();
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

    //Upload Image to server
    private void uploadImageToServer()
    {
        try
        {
            InputStream inputStream=getContentResolver().openInputStream(uplaodImageUri);
            imgBitmap= BitmapFactory.decodeStream(inputStream);
            encodeBitmapImage(imgBitmap);
        }catch (Exception ex)
        {

        }
        String url = "http://codingwithsunny.com/Shiftdrive/uploadImg.php";
        StringRequest request=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_LONG).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String,String> map=new HashMap<String, String>();
                map.put("upload",encodeImageString);
                map.put("user_id",UserCredentials.getId().toString());
                return map;
            }
        };

        RequestQueue queue= Volley.newRequestQueue(getApplicationContext());
        queue.add(request);
    }

    private void encodeBitmapImage(Bitmap bitmap)
    {
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        byte[] bytesofimage = byteArrayOutputStream.toByteArray();
        encodeImageString = android.util.Base64.encodeToString(bytesofimage, Base64.DEFAULT);
    }




}