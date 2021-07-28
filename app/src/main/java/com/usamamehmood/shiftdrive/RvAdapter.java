package com.usamamehmood.shiftdrive;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

//This is the recycler view that displays the vendor responses on user side UI

public class RvAdapter extends RecyclerView.Adapter<RvAdapter.MyViewHolder>{
    List<Bid_Response> ls;
    Context c;

    public RvAdapter(List<Bid_Response> ls, Context c) {
        this.ls = ls;
        this.c = c;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemRow = LayoutInflater.from(c).inflate(R.layout.row_bid_response,parent,false);
        return new RvAdapter.MyViewHolder(itemRow);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.shopname.setText("Bid Request from "+ls.get(position).getShopname());
        holder.time.setText(ls.get(position).getTime());
        holder.date.setText(ls.get(position).getDate());

        holder.rowww.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(c,DisplayRequestedBidResponse.class);
                i.putExtra("response_id",ls.get(position).getId());
                i.putExtra("vendor_id",ls.get(position).getVendor_id());
                i.putExtra("date",ls.get(position).getDate());
                i.putExtra("time",ls.get(position).getTime());
                i.putExtra("notefromvendor",ls.get(position).getNotefromvendor());
                i.putExtra("price",ls.get(position).getPrice());
                i.putExtra("shopname",ls.get(position).getShopname());
                i.putExtra("location",ls.get(position).getShop_location());
                i.putExtra("makemodel",ls.get(position).getMakemodel());
                i.putExtra("damage",ls.get(position).getDamage());
                i.putExtra("image",ls.get(position).getImage());
                i.putExtra("noteforvendor",ls.get(position).getNoteforvendor());
                i.putExtra("noteforvendor",ls.get(position).getNoteforvendor());
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
        TextView shopname,time,date;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            roww = itemView.findViewById(R.id.rowbidresponse_);
            rowww = itemView.findViewById(R.id.bidresponserow);
            shopname = itemView.findViewById(R.id.shopnameresponse);
            date = itemView.findViewById(R.id.dateresponse);
            time = itemView.findViewById(R.id.timeresponse);
        }
    }
}
