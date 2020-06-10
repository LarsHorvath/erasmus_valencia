package com.example.erasmusvalencia;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class EventDetailsActivity extends BaseThemeChangerActivity {

    Event event;
    int event_id;
    ImageView logoView;
    TextView titleText, timeText, locationText, urlText, companyText, descriptionText;
    ConstraintLayout cl;
    private static final String TAG = "EventDetailsActivity";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        cl = findViewById(R.id.constraintLayout);
        logoView = findViewById(R.id.logoView);
        titleText = findViewById(R.id.titleText_places);
        timeText = findViewById(R.id.timeText);
        locationText = findViewById(R.id.locationText);
        urlText = findViewById(R.id.urlText);
        companyText = findViewById(R.id.companyText);
        descriptionText = findViewById(R.id.descriptionText);

        getData();
        setData();

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem settingsItem = menu.findItem(R.id.favouriteIcon);
        // set your desired icon here based on a flag if you like
        if (event != null) {
            if (event.isFavourite()) settingsItem.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_favorite_black_24dp));
            else settingsItem.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_favorite_border_black_24dp));
        }
        else {
            Log.i(TAG, "onPrepareOptionsMenu: event isn't defined yet wjasdfjkasdf jkö asdfjlöksdaf");
        }
        

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onStop() {
        super.onStop();
        /*String json = Event.toJson(Event.allEvents);
        FileHandler handler = new FileHandler(FileHandler.FILE_NAME);
        try {
            handler.clearFile();
            handler.writeFile(json);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.favouriteIcon:
                if (event.isFavourite()) {
                    event.setFavourite(false);
                    item.setIcon(R.drawable.ic_favorite_border_black_24dp);
                    Toast.makeText(this,"removed from favourites", Toast.LENGTH_SHORT).show();
                } else {
                    event.setFavourite(true);
                    item.setIcon(R.drawable.ic_favorite_black_24dp);
                    Toast.makeText(this,"Event added to favourites", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.shareIcon:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                String toSend;
                if (event.getUrl() != null) toSend = String.format(Locale.ENGLISH,"Hi, I would like to share the following event with you:\n\n*%s*\n%s _%s_\n%s %s\n%s\n\n Are you in?",event.getTitle(), getString(R.string.emoji_clock), event.getStartDate().format(DateTimeFormatter.ofPattern("E dd.MM.yyyy hh:mm")), getString(R.string.emoji_location), event.getLocation(),event.getUrl());
                else toSend = String.format(Locale.ENGLISH,"Hi, I would like to share the following event with you:\n\n*%s*\n%s _%s_\n%s %s\n\n Are you in?",event.getTitle(), getString(R.string.emoji_clock), event.getStartDate().format(DateTimeFormatter.ofPattern("E dd.MM.yyyy hh:mm")), getString(R.string.emoji_location), event.getLocation());
                sendIntent.putExtra(Intent.EXTRA_TEXT, toSend);
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, null));
                return true;
            default:return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.event_details_menu, menu);
        MenuItem fav_item = findViewById(R.id.favouriteIcon);
        if (fav_item == null) return true;
        if (event.isFavourite()) {
            fav_item.setIcon(R.drawable.ic_favorite_black_24dp);
        } else {
            fav_item.setIcon(R.drawable.ic_favorite_border_black_24dp);
        }
        return true;
    }



    private void getData() {
        if(getIntent().hasExtra("event_id")) {
            event_id = getIntent().getIntExtra("event_id", 0);
        }
        else {
            Toast.makeText(this,"no data", Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setData() {
        event = Event.allEvents.get(event_id);
        titleText.setText(event.getTitle());
        timeText.setText(event.getStartDate().format(DateTimeFormatter.ofPattern("E dd.MM.yyyy hh:mm")));
        locationText.setText(event.getLocation());
        urlText.setText(event.getUrl());
        companyText.setText(event.getCompany());
        logoView.setImageResource(Event.imagesrc[event.getCompanyID()]);
        descriptionText.setText(event.getDescription());

        //descriptionText.setMovementMethod(new ScrollingMovementMethod());
    }
}
