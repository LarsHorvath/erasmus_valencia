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

public class EventTileAdapter extends RecyclerView.Adapter<EventTileAdapter.EventViewHolder> {

    private int ids[];
    private Context context;

    public EventTileAdapter(Context ct, int ids[]) {
        context = ct;
        this.ids = ids;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.event_tile, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, final int position) {
        final Event e = Event.allEvents.get(ids[position]);
        holder.titleText.setText(e.getTitle());
        holder.timeText.setText(Event.dayToString(e.getStartDate(), Event.NAME_AND_DAY_AND_TIME));
        holder.locationText.setText(e.getLocation());
        holder.logoView.setImageResource(Event.imagesrc[e.getCompanyID()]);
        if (e.isFavourite()) {
            holder.favView.setVisibility(View.VISIBLE);
        }
        else {
            holder.favView.setVisibility(View.GONE);
        }
        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
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

    public class EventViewHolder extends RecyclerView.ViewHolder{

        TextView titleText, timeText, locationText;
        ImageView logoView, favView;
        ConstraintLayout constraintLayout;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.titleText_places);
            timeText = itemView.findViewById(R.id.timeText);
            locationText = itemView.findViewById(R.id.locationText_places);
            logoView = itemView.findViewById(R.id.logoImage);
            favView = itemView.findViewById(R.id.favView);
            constraintLayout = itemView.findViewById(R.id.mainLayout);
        }
    }
}
