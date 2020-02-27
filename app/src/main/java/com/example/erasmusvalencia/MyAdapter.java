package com.example.erasmusvalencia;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    ArrayList<Event> eventsToDisplay;

    String titles[], dates[], locations[];
    int logosources[];
    Context context;

    public MyAdapter(Context ct, String s1[], String s2[], String s3[], int img[], ArrayList<Event> eventsToDisplay) {
        context = ct;
        titles = s1;
        dates = s2;
        locations = s3;
        logosources = img;
        this.eventsToDisplay = eventsToDisplay;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.event_tile, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        /*holder.myText1.setText(titles[position]);
        holder.myText2.setText(dates[position]);
        holder.myText3.setText(locations[position]);
        holder.myImage.setImageResource(logos[position]);*/
        final Event e = eventsToDisplay.get(position);
        holder.myText1.setText(e.getTitle());
        holder.myText2.setText(e.getStartDate().toString());
        holder.myText3.setText(e.getLocation());
        holder.myImage.setImageResource(logosources[e.getCompanyID()]);
        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EventDetailsActivity.class);
                intent.putExtra("data1", e.getTitle());
                intent.putExtra("data2", e.getStartDate().toString());
                intent.putExtra("data3", e.getLocation());
                intent.putExtra("url", e.getUrl());
                intent.putExtra("company", e.getCompany());
                intent.putExtra("myImage", logosources[e.getCompanyID()]);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return titles.length;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView myText1, myText2, myText3;
        ImageView myImage;
        ConstraintLayout mainLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            myText1 = itemView.findViewById(R.id.titleText);
            myText2 = itemView.findViewById(R.id.timeText);
            myText3 = itemView.findViewById(R.id.location_Text);
            myImage = itemView.findViewById(R.id.logoImage);
            mainLayout = itemView.findViewById(R.id.mainLayout);
        }
    }
}
