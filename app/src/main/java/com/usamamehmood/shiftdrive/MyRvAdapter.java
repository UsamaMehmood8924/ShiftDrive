package com.usamamehmood.shiftdrive;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

//This is the recycler view class that displays the bid requests from user to vendor side UI

public class MyRvAdapter extends RecyclerView.Adapter<MyRvAdapter.MyViewHolder> {

    List<BidRequest> ls;
    Context c;

    public MyRvAdapter(List<BidRequest> ls, Context c) {
        this.ls = ls;
        this.c = c;
    }

    @NonNull
    @Override
    public MyRvAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemRow = LayoutInflater.from(c).inflate(R.layout.row_bid_requests,parent,false);
        return new MyViewHolder(itemRow);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.clientName.setText("Bid Request from "+ls.get(position).getClientName());
        holder.time.setText(ls.get(position).getTime());
        holder.date.setText(ls.get(position).getDate());

        holder.rowww.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(c,DisplayRequestedBid.class);
                //Creating a bundle to post this data on next UI
                i.putExtra("request_id",ls.get(position).getId());
                i.putExtra("makemodel",ls.get(position).getCarMake());
                i.putExtra("damage",ls.get(position).getCarDamage());
                i.putExtra("noteforvendor",ls.get(position).getNoteForVendor());
                i.putExtra("carimage",ls.get(position).getCarImage());
                i.putExtra("clientid",ls.get(position).getUid());
                i.putExtra("bidid",ls.get(position).getId());
                //Toast.makeText(c,ls.get(position).getCarImage(),Toast.LENGTH_SHORT).show();

                c.startActivity(i);
                //Toast.makeText(c,"Show Bid Details",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return ls.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout roww;
        LinearLayout rowww;
        TextView clientName,time,date;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            roww = itemView.findViewById(R.id.rowbidrequests);
            rowww = itemView.findViewById(R.id.bidrequestrow);
            clientName = itemView.findViewById(R.id.clientName);
            date = itemView.findViewById(R.id.date);
            time = itemView.findViewById(R.id.time);
        }
    }
}
