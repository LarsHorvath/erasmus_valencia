package com.example.erasmusvalencia;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    // Declarations
    RecyclerView recyclerView;

    private static final String TAG = "MainActivity";
    ArrayList<Event> events;
    TextView dateText;
    Event.Date today, tmw;
    Switch filterSwitch;
    boolean restrict;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialization
        dateText = findViewById(R.id.dateView);
        ImageButton fButton = findViewById(R.id.fButton);
        ImageButton ffButton = findViewById(R.id.ffButton);
        ImageButton bButton = findViewById(R.id.bButton);
        ImageButton fbButton = findViewById(R.id.fbButton);
        filterSwitch = findViewById(R.id.filterSwitch);

        // Get event data and score them in Event.allEvents and this.events
        doFileMagic();

        // Getting the current date and initializing today & tomorrow
        getDate();

        // OnClickListeners for the buttons
        setListeners(fButton, ffButton, bButton, fbButton);

        // Updating the UI for the currently set date
        updateEvents(today, tmw);
    }

    private void getDate() {
        SharedPreferences preferences = getSharedPreferences("init", MODE_PRIVATE);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String day = preferences.getString("day", dateFormat.format(date));
        today = new Event.Date(day);
        tmw = today;
        restrict = preferences.getBoolean("restrictToDay", false);
        filterSwitch.setChecked(restrict);
        tmw = today.addDays(1);
    }

    // Either reads and parses the events from the raw_event_data resource or parses the in that case already existing json array of type Event
    private void doFileMagic() {
        // Setting the FileHandler's context
        FileHandler.context = this;

        // Reading file if this is the first time that app is open
        SharedPreferences preferences = getSharedPreferences("init", MODE_PRIVATE);
        boolean firstOpen = preferences.getBoolean("first_time", true);
        boolean success = true;
        events = new ArrayList<>();
        if (!firstOpen) {
            Log.i(TAG, "onCreate: already opened it before");
            FileHandler fileHandler = new FileHandler(FileHandler.FILE_NAME);
            try {
                fileHandler.readFile();
                String json = fileHandler.getContent();
                Event.allEvents = Event.parseFromJSON(json);
                events.addAll(Event.allEvents.values());
                Log.i(TAG, "onCreate: successfully read from json");
            } catch (IOException e) {
                e.printStackTrace();
                success = false;
            } catch (Exception e) {
                success = false;
            }
        }
        if (firstOpen || !success) {
            Log.i(TAG, "onCreate: we are opening for first time");
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("first_time", false);
            editor.apply();
            events = Event.parseFromJSONString(FileHandler.readFromRaw());
            for (Event e : events) {
                Event.allEvents.put(e.getId(), e);
            }
        }
        Collections.sort(events);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences preferences = getSharedPreferences("init", MODE_PRIVATE);
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
        }
    }

    // Updates the UI (the date and the events on that date)
    public void updateEvents(Event.Date start, Event.Date end) {
        SharedPreferences preferences = getSharedPreferences("init", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("day", start.localeDateString());
        editor.apply();

        dateText.setText(today.dayToString());
        ArrayList<Event> filteredDate;
        if (restrict) filteredDate = Event.filterEvents(events, Event.FILTER_DATE, start, end);
        else filteredDate = Event.filterEvents(events, Event.FILTER_DATE, start, start);
        Collections.sort(filteredDate);
        displayEvents(filteredDate);
    }

    public void displayEvents(ArrayList<Event> fEvents) {
        int size = fEvents.size();
        int [] fids = new int[size];
        for (int i = 0; i < size; i++) {
            fids[i] = fEvents.get(i).getId();
        }

        recyclerView = findViewById(R.id.recyclerView);

        MyAdapter myAdapter = new MyAdapter(this, fids);
        recyclerView.setAdapter(myAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setListeners(ImageButton fButton, ImageButton ffButton, ImageButton bButton, ImageButton fbButton) {
        fButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                today = today.addDays(1);
                tmw = today.addDays(1);
                dateText.setText(today.dayToString());
                updateEvents(today, tmw);
            }
        });
        ffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                today = today.addDays(7);
                tmw = today.addDays(1);
                dateText.setText(today.dayToString());
                updateEvents(today, tmw);
            }
        });
        bButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                today = today.addDays(-1);
                tmw = today.addDays(1);
                dateText.setText(today.dayToString());
                updateEvents(today, tmw);

            }
        });
        fbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                today = today.addDays(-7);
                tmw = today.addDays(1);
                dateText.setText(today.dayToString());
                updateEvents(today, tmw);
            }
        });
        filterSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                restrict = isChecked;
                SharedPreferences preferences = getSharedPreferences("init", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("restrictToDay", restrict);
                editor.apply();
                updateEvents(today, tmw);
            }
        });
    }
}
