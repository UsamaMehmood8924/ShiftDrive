package com.usamamehmood.shiftdrive;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//This class displays the bid request on vendor side UI

public class View_Bid_Requests extends AppCompatActivity {
    RecyclerView rv;
    List<BidRequest> bidRequests;
    MyRvAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view__bid__requests);

        bidRequests = new ArrayList<>();

        rv = findViewById(R.id.rv);
        RecyclerView.LayoutManager lm = new LinearLayoutManager(this);
        rv.setLayoutManager(lm);
        adapter = new MyRvAdapter(bidRequests,this);
        rv.setAdapter(adapter);

        getBidRequestsFromServer();

    }

    private void getBidRequestsFromServer()
    {
        ProgressDialog pd = new ProgressDialog(View_Bid_Requests.this);
        pd.setMessage("Loading...");
        pd.show();


        //Create a Volley Request
        String url = "http://codingwithsunny.com/Shiftdrive/showBidRequests.php";
        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject res=new JSONObject(response);
                            if(res.getString("status").equalsIgnoreCase("1"))
                            {
                                pd.dismiss();

                                List<String> reqDetails = new ArrayList<String>();
                                //bidRequests.clear();

                                JSONArray cast = res.getJSONArray("requests");
                                for (int i=0; i<cast.length(); i++) {
                                    JSONObject actor = cast.getJSONObject(i);

                                    bidRequests.add(new BidRequest(actor.getString("id"),actor.getString("user_id"),actor.getString("image"),actor.getString("username").trim(),actor.getString("makemodel"),actor.getString("makemodel"),actor.getString("damage"),actor.getString("noteforvendor"),actor.getString("date"),actor.getString("time")));
                                }
                                adapter.notifyDataSetChanged();
                                rv.setAdapter(adapter);

                            }
                            else
                            {
                                showErrorDialog("Loading failed...");
                                pd.dismiss();
                                Toast.makeText(View_Bid_Requests.this,res.getString("status"),Toast.LENGTH_LONG).show();
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
                        //This code is executed if there is an error.
                        showErrorDialog("Error loading...");
                        pd.dismiss();

                    }
                }) {
            protected Map<String, String> getParams() {
                Map<String, String> data = new HashMap<String, String>();
                Log.d("vendorlocation",Vendor_Details.getLocation());
                data.put("location", Vendor_Details.getLocation());
                return data;
            }
        };
        Volley.newRequestQueue(View_Bid_Requests.this).add(MyStringRequest);

    }

    //Show error on screen with an alert dialog
    private void showErrorDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Oops")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


}