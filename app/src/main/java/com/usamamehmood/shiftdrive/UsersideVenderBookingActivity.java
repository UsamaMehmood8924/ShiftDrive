package com.usamamehmood.shiftdrive;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

//This class displays the Booking UI on user side

public class UsersideVenderBookingActivity extends AppCompatActivity {

    private DatePicker datePicker;
    private Calendar calendar;
    private TextView dateView;
    private int year, month, day;
    Button selectDateButton, finalBookingButton;
    TextView selectedBookingTime;
    TextView _shopname, _name, _location, _contact;
    LinearLayout detailsLayout, showBookingTimeDetails;
    String vendor_id;
    Button tracklocation;
    DatePickerDialog datePickerDialog;
    Boolean isDateSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userside_vender_booking);

        //Getting the details from UI
        showBookingTimeDetails = findViewById(R.id.showbookingtime);
        _shopname = findViewById(R.id.details_shopname_1);
        _name = findViewById(R.id.details_name_1);
        _location = findViewById(R.id.details_location_1);
        _contact = findViewById(R.id.details_contact_1);
        detailsLayout = findViewById(R.id.details_layout_1);
        tracklocation = findViewById(R.id.tracklocation_1);
        finalBookingButton = findViewById(R.id.finalBookingButton);

        finalBookingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isDateSelected)
                {
                    Toast.makeText(UsersideVenderBookingActivity.this,"Booking Notification Sent to Vendor",Toast.LENGTH_LONG).show();
                    Intent i = new Intent(UsersideVenderBookingActivity.this,HomeActivity.class);
                    startActivity(i);
                }
                else
                {
                    Toast.makeText(UsersideVenderBookingActivity.this,"Please select a date",Toast.LENGTH_SHORT).show();
                }
            }
        });

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

        selectDateButton = findViewById(R.id.selectBookingDate_1);
        dateView = findViewById(R.id.selectedBookingTime);

        calendar = Calendar.getInstance();

        //This function displays the calender
        selectDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = new DatePickerDialog(UsersideVenderBookingActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text
                                showBookingTimeDetails.setVisibility(View.VISIBLE);
                                dateView.setText(dayOfMonth + "/"
                                        + (monthOfYear + 1) + "/" + year);
                                isDateSelected = true;

                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });
        //showDate(year, month+1, day);
    }

    //This function calls the Maps to show the distance of user from vendor's workshop
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
        ProgressDialog pd = new ProgressDialog(UsersideVenderBookingActivity.this);
        pd.setMessage("Fetching Details...");
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
                                Toast.makeText(UsersideVenderBookingActivity.this,res.getString("status"),Toast.LENGTH_LONG).show();
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
        Volley.newRequestQueue(UsersideVenderBookingActivity.this).add(MyStringRequest);

    }

    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);
        Toast.makeText(getApplicationContext(), "ca",
                Toast.LENGTH_SHORT)
                .show();
    }
    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this,
                    myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    showDate(arg1, arg2+1, arg3);
                }
            };

    private void showDate(int year, int month, int day) {
        dateView.setText(new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year));
    }

}