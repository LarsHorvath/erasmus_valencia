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

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private int ids[];
    private Context context;

    public MyAdapter(Context ct, int ids[]) {
        context = ct;
        this.ids = ids;
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
        final Event e = Event.allEvents.get(ids[position]);
        holder.myText1.setText(e.getTitle());
        holder.myText2.setText(e.getStartDate().toString());
        holder.myText3.setText(e.getLocation());
        holder.myImage.setImageResource(Event.imagesrc[e.getCompanyID()]);
        if (e.isFavourite()) {
            holder.favView.setVisibility(View.VISIBLE);
        }
        else {
            holder.favView.setVisibility(View.GONE);
        }
        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EventDetailsActivity.class);
                intent.putExtra("event_id", e.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return ids.length;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView myText1, myText2, myText3;
        ImageView myImage, favView;
        ConstraintLayout mainLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            myText1 = itemView.findViewById(R.id.titleText_places);
            myText2 = itemView.findViewById(R.id.timeText);
            myText3 = itemView.findViewById(R.id.locationText_places);
            myImage = itemView.findViewById(R.id.logoImage);
            favView = itemView.findViewById(R.id.favView);
            mainLayout = itemView.findViewById(R.id.mainLayout);
        }
    }
}
