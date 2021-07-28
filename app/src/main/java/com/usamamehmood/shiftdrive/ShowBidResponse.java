package com.usamamehmood.shiftdrive;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
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

//This class shows the bid response from vendor on User side UI

public class ShowBidResponse extends AppCompatActivity {

    //Recycler view elements
    RecyclerView rv;
    List<Bid_Response> _bidResponse;
    RvAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_bid_response);

        _bidResponse = new ArrayList<>();

        rv = findViewById(R.id.rvresponse);
        RecyclerView.LayoutManager lm = new LinearLayoutManager(this);
        rv.setLayoutManager(lm);
        adapter = new RvAdapter(_bidResponse,this);
        rv.setAdapter(adapter);

        getBidResponseFromServer();


    }

    //Calling the API request to get bid responses from database
    private void getBidResponseFromServer()
    {
        ProgressDialog pd = new ProgressDialog(ShowBidResponse.this);
        pd.setMessage("Loading...");
        pd.show();

        //Create a Volley Request
        String url = "http://codingwithsunny.com/Shiftdrive/showBidResponse.php";
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
                                //Log.d("Noor Length: ", String.valueOf(cast.length()));
                                for (int i=0; i<cast.length(); i++) {
                                    JSONObject actor = cast.getJSONObject(i);

                                    _bidResponse.add(new Bid_Response(actor.getString("response_id"),actor.getString("vendor_id"),actor.getString("shopname"),actor.getString("location"),actor.getString("makemodel"),actor.getString("date"),actor.getString("time"),actor.getString("damage"),actor.getString("image"),actor.getString("noteforvendor"),actor.getString("notefromvendor"),actor.getString("price")));
                                    //bidRequests.add(new BidRequest(actor.getString("id"),actor.getString("user_id"),actor.getString("image"),actor.getString("username").trim(),actor.getString("makemodel"),actor.getString("makemodel"),actor.getString("damage"),actor.getString("noteforvendor"),actor.getString("date"),actor.getString("time")));
                                }
                                adapter.notifyDataSetChanged();
                                rv.setAdapter(adapter);

                            }
                            else
                            {
                                showErrorDialog("Login attempt failed");
                                pd.dismiss();
                                Toast.makeText(ShowBidResponse.this,res.getString("status"),Toast.LENGTH_LONG).show();
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
                        showErrorDialog("Attempt failed");
                        pd.dismiss();

                    }
                }) {
            protected Map<String, String> getParams() {
                Map<String, String> data = new HashMap<String, String>();
                data.put("user_id",UserCredentials.getId());
                return data;
            }
        };
        Volley.newRequestQueue(ShowBidResponse.this).add(MyStringRequest);

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