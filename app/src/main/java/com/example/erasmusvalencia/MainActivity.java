package com.example.erasmusvalencia;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    String[] filterDialogItems = new String[]{
            "Enable search bar",
    };

    boolean[] filterDialogSelection = new boolean[]{
            false,
    };

    // Declarations
    RecyclerView recyclerView;
    DatePickerDialog picker;
    EditText searchEdit;

    private static final String TAG = "MainActivity";
    ArrayList<Event> events;
    Button button_events, button_places, button_info;
    String filterText = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialization
        button_events = findViewById(R.id.button_events);
        button_places = findViewById(R.id.button_places);
        button_info = findViewById(R.id.button_info);
        searchEdit = findViewById(R.id.searchEditMain);

        // Get event data and score them in Event.allEvents and this.events
        doFileMagic();

        // OnClickListeners for the buttons and the switch
        setListeners();

        //set preferences
        setDateAndPreferences();

        // Updating the UI for the currently set date
        updateEvents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setDateAndPreferences();
        updateEvents();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
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
            case R.id.calendar_icon:
                //showDatePickerDialog();
                return true;
            default:return super.onOptionsItemSelected(item);

        }
    }

    private void setDateAndPreferences() {
        SharedPreferences preferences = getSharedPreferences("init", MODE_PRIVATE);
        filterDialogSelection[0] = preferences.getBoolean("restrictToText", false);
    }

    private AlertDialog makeFilterAlert() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        mBuilder.setTitle("Choose filters")
                .setMultiChoiceItems(filterDialogItems, filterDialogSelection, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                        SharedPreferences preferences = getSharedPreferences("init", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        switch (i) {
                            case 0:
                                filterDialogSelection[0] = b;
                                editor.putBoolean("restrictToText", b);
                                editor.apply();
                        }
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updateEvents();
                    }
                });
        return mBuilder.create();
    }


    // Either reads and parses the events from the raw_event_data resource or parses the in that case already existing json array of type Event
    private void doFileMagic() {
        events = new ArrayList<>();
        // In case the Hashmap of all events is already populated
        if (Event.allEvents != null && Event.allEvents.size() != 0) {
            events.addAll(Event.allEvents.values());
            Collections.sort(events);
            return;
        }
        // Setting the FileHandler's context
        FileHandler.context = this;

        // Reading file if this is the first time that app is open
        SharedPreferences preferences = getSharedPreferences("init", MODE_PRIVATE);
        boolean firstOpen = preferences.getBoolean("first_time", true);
        boolean success = true;
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
    public void updateEvents() {
        ArrayList<Event> filtered;
        filtered = Event.filterEvents(events, Event.FILTER_FAVOURITE, true);
        if (filterDialogSelection[0]) {
            filtered = Event.filterEvents(filtered, Event.FILTER_TEXT_SEARCH, filterText);
        }
        Collections.sort(filtered);
        displayEvents(filtered);
    }

    public void displayEvents(ArrayList<Event> fEvents) {
        if (filterDialogSelection[0]) {
            searchEdit.setVisibility(View.VISIBLE);
        }
        else {
            searchEdit.setVisibility(View.GONE);
        }
        int size = fEvents.size();
        TextView noFavText=findViewById(R.id.noFavText);
        if (size == 0) {
            noFavText.setVisibility(View.VISIBLE);
        }
        else {
            noFavText.setVisibility(View.GONE);
        }
        int [] fids = new int[size];
        for (int i = 0; i < size; i++) {
            fids[i] = fEvents.get(i).getId();
        }

        recyclerView = findViewById(R.id.recyclerView);

        MyAdapter myAdapter = new MyAdapter(this, fids);
        recyclerView.setAdapter(myAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setListeners() {
        searchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filterText = searchEdit.getText().toString();
                updateEvents();
            }
        });
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
            }
        });
    }
}
