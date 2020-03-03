package com.example.erasmusvalencia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.HorizontalCalendarView;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;

public class EventsActivity extends AppCompatActivity {

    private static final String TAG = "EventsActivity";
    RecyclerView recyclerView;

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
    DatePickerDialog picker;

    ArrayList<Event> events;
    Event.Date selectedDay;
    boolean filterDay;
    boolean filterFavourites;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        // Initialization

        // Get event data and score them in Event.allEvents and this.events
        doFileMagic();

        // Getting the current date and initializing today & tomorrow
        setDateAndPreferences();

        updateEvents(selectedDay);

        // OnClickListeners for the buttons and the switch
        setListeners();

        // set up the calendar View
        setCalenderView();

        // Updating the UI for the currently set date
        updateEvents(selectedDay);

    }

    private void setCalenderView(){
        /* Initialize the horizontal Calendar View */
        //starts before 1 month from now
        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.MONTH, -1);
        // ends after 3 month from now
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.MONTH, 3);

        // create the horizontal Calender with its builder
        HorizontalCalendar horizontalCalendar = new HorizontalCalendar.Builder(this, R.id.calendarView)
                .range(startDate, endDate)
                .datesNumberOnScreen(5)
                .build();

        // Set listeners to calendar view
        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {
                Log.i(TAG, "onDateSelected: date: " + date.toString() + " position: " + position);
                Log.i(TAG, "onDateSelected: day: " + date.get(Calendar.DAY_OF_MONTH) + " month: " + (date.get(Calendar.MONTH)+1) + " year: "+date.get(Calendar.YEAR));
                selectedDay = new Event.Date(date.get(Calendar.DAY_OF_MONTH), date.get(Calendar.MONTH)+1, date.get(Calendar.YEAR));
                updateEvents(selectedDay);
            }

            @Override
            public void onCalendarScroll(HorizontalCalendarView calendarView,
                                         int dx, int dy) {

            }

            @Override
            public boolean onDateLongClicked(Calendar date, int position) {
                return true;
            }
        });
    }//end setCalendarView

    @Override
    protected void onResume() {
        super.onResume();
        updateEvents(selectedDay);
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
                Toast.makeText(this, "Settings selected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.calendar_icon:
                showDatePickerDialog();
                return true;
            default:return super.onOptionsItemSelected(item);

        }
    }

    private AlertDialog makeFilterAlert() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(EventsActivity.this);
        mBuilder.setTitle("Choose filters")
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
    }

    private void setDateAndPreferences() {
        SharedPreferences preferences = getSharedPreferences("init", MODE_PRIVATE);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String day = preferences.getString("day", dateFormat.format(date));
        selectedDay = new Event.Date(day);
        filterDay = preferences.getBoolean("restrictToDay", false);
        filterFavourites = preferences.getBoolean("restrictToFavourites", false);
        filterDialogSelection[0] = filterDay;
        filterDialogSelection[1] = filterFavourites;
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
    public void updateEvents(Event.Date start) {
        SharedPreferences preferences = getSharedPreferences("init", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("day", start.localeDateString());
        editor.apply();

        ArrayList<Event> filtered;
        if (filterDay) filtered = Event.filterEvents(events, Event.FILTER_DATE, start, start.addDays(1));
        else filtered = Event.filterEvents(events, Event.FILTER_DATE, start, start);
        if (filterFavourites) filtered = Event.filterEvents(filtered, Event.FILTER_FAVOURITE, true);
        Collections.sort(filtered);
        displayEvents(filtered);
    }

    public void displayEvents(ArrayList<Event> fEvents) {
        int size = fEvents.size();
        int [] fids = new int[size];
        for (int i = 0; i < size; i++) {
            fids[i] = fEvents.get(i).getId();
        }

        recyclerView = findViewById(R.id.recyclerView2);

        MyAdapter myAdapter = new MyAdapter(this, fids);
        recyclerView.setAdapter(myAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void showDatePickerDialog() {
        final Calendar cldr = Calendar.getInstance();
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        int month = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);
        // date picker dialog
        picker = new DatePickerDialog(EventsActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                selectedDay = new Event.Date(i2, i1 + 1, i);
                updateEvents(selectedDay);
            }
        }, year, month, day);

        picker.show();
    }

    private void setListeners() {

    }

}
