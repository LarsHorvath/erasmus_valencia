package com.example.erasmusvalencia;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends BaseRecyclerActivity {

    private static final String TAG = "MainActivity";
    Button button_events, button_places, button_info;
    TextView nothingToShowView;

    @Override
    protected void initialize() {
        filterDialogSelection = new boolean[] {false};
        filterDialogItems = new String[]{"Enable search bar"};
        nothingToShowView = findViewById(R.id.noFavText);
        recyclerView = findViewById(R.id.recyclerView);
        button_events = findViewById(R.id.button_events);
        button_places = findViewById(R.id.button_places);
        button_info = findViewById(R.id.button_info);
        searchEdit = findViewById(R.id.searchEditMain);
    }

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_main;
    }

    @Override
    protected int getMenuResID() {
        return R.menu.main_menu;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.filter_icon:
                AlertDialog dialog = makeFilterAlert();
                dialog.show();
                return true;
            case R.id.settings_icon:
                Log.i(TAG, "onOptionsItemSelected: going from mainactivity to settingsactivity");
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected void updateUI() {
        super.updateUI();
        if (eventsFiltered.size() == 0) {
            nothingToShowView.setVisibility(View.VISIBLE);
        }
        else {
            nothingToShowView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /*SharedPreferences preferences = getSharedPreferences("init", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        // editor.clear();
        editor.apply();
        String json = Event.toJson(Event.allEvents);
        FileHandler handler = new FileHandler(FileHandler.FILE_NAME);
        try {
            handler.clearFile();
            handler.writeFile(json);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    // Updates the UI (the date and the events on that date)
    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void updateEvents() {
        if (allEvents == null) {
            eventsFiltered = new ArrayList<>();
            return;
        }
        eventsFiltered = Event.filterEvents(allEvents.values(), Event.FILTER_FAVOURITE, true);
        if (filterDialogSelection.length !=0 && filterDialogSelection[SHOW_SEARCH_BAR]) {
            eventsFiltered = Event.filterEvents(eventsFiltered, Event.FILTER_TEXT_SEARCH, filterText);
        }
        Collections.sort(eventsFiltered);
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        button_events.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // go to EventsActivity
                Intent intent = new Intent(MainActivity.this, EventsActivity.class);
                startActivity(intent);
            }
        });
        button_places.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // go to PlacesActivity
                Intent intent = new Intent(MainActivity.this, PlacesActivity.class);
                startActivity(intent);
            }
        });
        button_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // go to InfoActivity
                Intent intent = new Intent(MainActivity.this, InfoActivity.class);
                startActivity(intent);
            }
        });
    }
}
