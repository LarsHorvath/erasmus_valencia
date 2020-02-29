package com.example.erasmusvalencia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

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
    ArrayList<Event> events;
    Event.Date today, tmw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);


        // set up the calendar View
        setCalenderView();

        // Get event data and score them in Event.allEvents and this.events
        doFileMagic();

        // Getting the current date and initializing today & tomorrow
        getDate();

        // Updating the UI for the currently set date
        updateEvents(today, tmw);


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

    // initialize today and tomorrow
    private void getDate() {
        SharedPreferences preferences = getSharedPreferences("init2", MODE_PRIVATE);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String day = preferences.getString("day", dateFormat.format(date));
        today = new Event.Date(day);
        tmw = today.addDays(1);
    }
    // Either reads and parses the events from the raw_event_data resource or parses the in that case already existing json array of type Event
    private void doFileMagic() {
        // Setting the FileHandler's context
        FileHandler.context = this;

        // Reading file if this is the first time that app is open
        SharedPreferences preferences = getSharedPreferences("init2", MODE_PRIVATE);
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
    }//end doFileMagic

    // Updates the UI (the date and the events on that date)
    public void updateEvents(Event.Date start, Event.Date end) {
        SharedPreferences preferences = getSharedPreferences("init2", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("day", start.localeDateString());
        editor.apply();

        ArrayList<Event> filteredDate;
        filteredDate = Event.filterEvents(events, Event.FILTER_DATE, start, start);
        Collections.sort(filteredDate);
        displayEvents(filteredDate);
    }

    public void displayEvents(ArrayList<Event> fEvents) {
        int size = fEvents.size();
        int [] fids = new int[size];
        for (int i = 0; i < size; i++) {
            fids[i] = fEvents.get(i).getId();
        }

        recyclerView = findViewById(R.id.receyclerView2);

        MyAdapter myAdapter = new MyAdapter(this, fids);
        recyclerView.setAdapter(myAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

}
