package com.example.erasmusvalencia;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    String[] filterDialogItems = new String[]{
            "Restrict to today",
            "Only show favourites",
            "Free events",
            "Single day events"
    };

    boolean[] filterDialogSelection = new boolean[]{
            false,
            false,
            false,
            false,
    };

    // Declarations
    RecyclerView recyclerView;
    DatePickerDialog picker;

    private static final String TAG = "MainActivity";
    ArrayList<Event> events;
    TextView favouritesText;
    Button button_events, button_places, button_info;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialization
        favouritesText = findViewById(R.id.dateView);
        favouritesText.setText("Favourites");
        button_events = findViewById(R.id.button_events);
        button_places = findViewById(R.id.button_places);
        button_info = findViewById(R.id.button_info);

        // Get event data and score them in Event.allEvents and this.events
        doFileMagic();

        // Getting the current date and initializing today & tomorrow
        //setDateAndPreferences();

        // OnClickListeners for the buttons and the switch
        setListeners();

        // Updating the UI for the currently set date
        updateEvents();
    }

    @Override
    protected void onResume() {
        super.onResume();
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
                //AlertDialog dialog = makeFilterAlert();
                //dialog.show();
                return true;
            case R.id.settings_icon:
                Toast.makeText(this, "Settings selected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.calendar_icon:
                //showDatePickerDialog();
                return true;
            default:return super.onOptionsItemSelected(item);

        }
    }

    /*private AlertDialog makeFilterAlert() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        mBuilder.setTitle("Text size")
                .setMultiChoiceItems(filterDialogItems, filterDialogSelection, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                        SharedPreferences preferences = getSharedPreferences("init", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        switch (i) {
                            case 0:
                                filterDay = b;
                                filterDialogSelection[0] = b;
                                editor.putBoolean("restrictToDay", filterDay);
                                editor.apply();
                                break;
                            case 1:
                                filterFavourites = b;
                                filterDialogSelection[1] = b;
                                editor.putBoolean("restrictToFavourites", filterFavourites);
                                editor.apply();
                        }
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updateEvents(selectedDay);
                    }
                });
        return mBuilder.create();
    }*/

    /*private void setDateAndPreferences() {
        SharedPreferences preferences = getSharedPreferences("init", MODE_PRIVATE);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String day = preferences.getString("day", dateFormat.format(date));
        selectedDay = new Event.Date(day);
        filterDay = preferences.getBoolean("restrictToDay", false);
        filterFavourites = preferences.getBoolean("restrictToFavourites", false);
        filterDialogSelection[0] = filterDay;
        filterDialogSelection[1] = filterFavourites;
    }*/

    /*private void setToday() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        selectedDay = new Event.Date(dateFormat.format(date));
        updateEvents(selectedDay);
    }*/

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
        Collections.sort(filtered);
        displayEvents(filtered);
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

    /*private void showDatePickerDialog() {
        final Calendar cldr = Calendar.getInstance();
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        int month = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);
        // date picker dialog
        picker = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                selectedDay = new Event.Date(i2, i1 + 1, i);
                updateEvents(selectedDay);
            }
        }, year, month, day);

        picker.show();
    }*/

    private void setListeners() {
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
