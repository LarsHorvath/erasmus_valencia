package com.example.erasmusvalencia;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EventDetailsActivity extends AppCompatActivity {

    ImageView logoView;
    TextView titleText, timeText, locationText, urlText, companyText;
    ImageButton favouriteButton;
    ImageButton notFavouriteButton;
    boolean fav = false;


    String data1, data2, data3, url, company;
    int myImage;

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
                fav = false;
                favouriteButton.setVisibility(View.INVISIBLE);
                notFavouriteButton.setVisibility(View.VISIBLE);
                Toast.makeText(v.getContext(),"removed from favourites (actually not yet)", Toast.LENGTH_SHORT).show();
            }
        });
        notFavouriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fav = true;
                notFavouriteButton.setVisibility(View.INVISIBLE);
                favouriteButton.setVisibility(View.VISIBLE);
                Toast.makeText(v.getContext(),"event added to favourites (actually not yet)", Toast.LENGTH_SHORT).show();
            }
        });

        getData();
        setData();
    }

    private void getData() {
        if(getIntent().hasExtra("myImage") && getIntent().hasExtra("data1")
                && getIntent().hasExtra("data2") && getIntent().hasExtra("data3")) {
            data1 = getIntent().getStringExtra("data1");
            data2 = getIntent().getStringExtra("data2");
            data3 = getIntent().getStringExtra("data3");
            url = getIntent().getStringExtra("url");
            company = getIntent().getStringExtra("company");
            myImage = getIntent().getIntExtra("myImage", 1);
        }
        else {
            Toast.makeText(this,"no data", Toast.LENGTH_SHORT).show();
        }
    }

    private void setData() {
        titleText.setText(data1);
        timeText.setText(data2);
        locationText.setText(data3);
        urlText.setText(url);
        companyText.setText(company);
        logoView.setImageResource(myImage);
    }
}
