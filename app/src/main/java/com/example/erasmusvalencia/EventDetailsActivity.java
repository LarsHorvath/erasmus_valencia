package com.example.erasmusvalencia;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class EventDetailsActivity extends AppCompatActivity {

    Event event;
    int event_id;
    ImageView logoView;
    TextView titleText, timeText, locationText, urlText, companyText;
    ImageButton favouriteButton;
    ImageButton notFavouriteButton;
    private static final String TAG = "EventDetailsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        logoView = findViewById(R.id.logoView);
        titleText = findViewById(R.id.titleText);
        timeText = findViewById(R.id.timeText);
        locationText = findViewById(R.id.locationText);
        urlText = findViewById(R.id.urlText);
        companyText = findViewById(R.id.companyText);
        favouriteButton = findViewById(R.id.favouriteButton);
        notFavouriteButton = findViewById(R.id.notFavouriteButton);

        favouriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                event.setFavourite(false);
                favouriteButton.setVisibility(View.INVISIBLE);
                notFavouriteButton.setVisibility(View.VISIBLE);
                Toast.makeText(v.getContext(),"removed from favourites", Toast.LENGTH_SHORT).show();
            }
        });
        notFavouriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                event.setFavourite(true);
                notFavouriteButton.setVisibility(View.INVISIBLE);
                favouriteButton.setVisibility(View.VISIBLE);
                Toast.makeText(v.getContext(),"event added to favourites", Toast.LENGTH_SHORT).show();
            }
        });

        getData();
        setData();

        favouriteButton.setVisibility(event.isFavourite() ? View.VISIBLE : View.INVISIBLE);
        notFavouriteButton.setVisibility(event.isFavourite() ? View.INVISIBLE : View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        String json = Event.toJson(Event.allEvents);
        FileHandler handler = new FileHandler(FileHandler.FILE_NAME);
        try {
            handler.clearFile();
            handler.writeFile(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getData() {
        if(getIntent().hasExtra("event_id")) {
            event_id = getIntent().getIntExtra("event_id", 0);
        }
        else {
            Toast.makeText(this,"no data", Toast.LENGTH_SHORT).show();
        }
    }

    private void setData() {
        event = Event.allEvents.get(event_id);
        titleText.setText(event.getTitle());
        timeText.setText(event.getStartDate().toString());
        locationText.setText(event.getLocation());
        urlText.setText(event.getUrl());
        companyText.setText(event.getCompany());
        logoView.setImageResource(Event.imagesrc[event.getCompanyID()]);
    }
}
