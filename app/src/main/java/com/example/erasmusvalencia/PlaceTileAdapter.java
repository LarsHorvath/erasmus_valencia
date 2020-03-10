package com.example.erasmusvalencia;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PlaceTileAdapter extends RecyclerView.Adapter<PlaceTileAdapter.PlaceViewHolder> {

    private static final String TAG = "RecyclerViewAdapter Places";
    private ArrayList<Places> places = new ArrayList<>();
    private Context mContext;

    public PlaceTileAdapter(Context ct, ArrayList<Places> places ) {
        mContext = ct;
        this.places = places;
    }

    @NonNull
    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.places_tile, parent, false);
        return new PlaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceViewHolder holder, final int position) {

        holder.mTitleText.setText(places.get(position).getName());
        holder.mLocationText.setText(places.get(position).getLocation());
        holder.mImage.setImageResource(places.get(position).getImagesrc());

        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Link to Google maps
                Log.d(TAG, "onClick: here go to google link");
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(places.get(position).getUrl()));
                mContext.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    public class PlaceViewHolder extends RecyclerView.ViewHolder{

        TextView mTitleText, mLocationText;
        ImageView mImage;
        ConstraintLayout mainLayout;

        public PlaceViewHolder(@NonNull View itemView) {
            super(itemView);
            mTitleText = itemView.findViewById(R.id.titleText_places);
            mLocationText = itemView.findViewById(R.id.locationText_places);
            mImage = itemView.findViewById(R.id.imageView_places);
            mainLayout = itemView.findViewById(R.id.mainLayout_places);
        }
    }
}
