package com.example.erasmusvalencia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.DatePicker;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.HorizontalCalendarView;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;

public class EventsActivity extends BaseRecyclerActivity {

    private static final String TAG = "EventsActivity";

    // Declarations
    DatePickerDialog picker;
    HorizontalCalendar horizontalCalendar;
    Calendar selectedDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set up the calendar View
        setCalenderView();
        horizontalCalendar.selectDate(selectedDay, true);
        ConstraintLayout constraintLayout = findViewById(R.id.constraintLayout2);
        if (theme == DEFAULT_THEME) {
            constraintLayout.setBackgroundColor(getResources().getColor(R.color.defaultColorPrimary));
        }
        else if (theme == THEME_1) {
            constraintLayout.setBackgroundColor(getResources().getColor(R.color.ColorPrimary1));
        }
    }

    private void setCalenderView() {
        /* Initialize the horizontal Calendar View */
        //starts before 1 month from now
        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.MONTH, -1);
        // ends after 3 month from now
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.MONTH, 3);
        // create the horizontal Calender with its builder
        horizontalCalendar = new HorizontalCalendar.Builder(this, R.id.calendarView)
                .range(startDate, endDate)
                .datesNumberOnScreen(5)
                .defaultSelectedDate((Calendar) selectedDay)
                .build();

        // Set listeners to calendar view
        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {
                Log.i(TAG, "onDateSelected: date: " + date.toString() + " position: " + position);
                Log.i(TAG, "onDateSelected: day: " + date.get(Calendar.DAY_OF_MONTH) + " month: " + (date.get(Calendar.MONTH)+1) + " year: "+date.get(Calendar.YEAR));
                selectedDay = date;
                updateEvents();
                updateUI();
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
        horizontalCalendar.selectDate(selectedDay, true);
    }

    @Override
    protected void initialize() {
        // Initialization
        searchEdit = findViewById(R.id.searchEdit);
        recyclerView = findViewById(R.id.recyclerView2);
        selectedDay = Calendar.getInstance();
        nothingToShowView = findViewById(R.id.eventsNotLoaded);
        filterDialogItems = new String[]{
                "Show search bar",
                "Restrict to today",
                "Only show favourites",
        };

        filterDialogSelection = new boolean[]{
                false,
                false,
                false,
        };

    }

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_events;
    }

    @Override
    protected int getMenuResID() {
        return R.menu.event_menu;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.filter_icon:
                AlertDialog dialog = makeFilterAlert();
                dialog.show();
                return true;
            case R.id.settings_icon:
                Log.i(TAG, "onOptionsItemSelected: going from eventsactivity to settingsactivity");
                Intent intent = new Intent(EventsActivity.this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.calendar_icon:
                showDatePickerDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
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
    @Override
    protected void updateEvents() {
        Calendar end = (Calendar) selectedDay.clone();
        end.add(Calendar.DAY_OF_MONTH, 1);
        if (Event.allEvents == null) {
            eventsFiltered = new ArrayList<>();
            return;
        }
        if (filterDialogSelection[1]) eventsFiltered = Event.filterEvents(Event.allEvents.values(), Event.FILTER_DATE, selectedDay, end);
        else eventsFiltered = Event.filterEvents(Event.allEvents.values(), Event.FILTER_DATE, selectedDay, selectedDay);
        if (filterDialogSelection[2]) eventsFiltered = Event.filterEvents(eventsFiltered, Event.FILTER_FAVOURITE, true);
        if (filterDialogSelection[0]) {
            eventsFiltered = Event.filterEvents(eventsFiltered, Event.FILTER_TEXT_SEARCH, filterText);
        }
        Collections.sort(eventsFiltered);
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
                selectedDay.set(i, i1, i2);
                updateEvents();
                updateUI();
                horizontalCalendar.selectDate(selectedDay, true);
            }
        }, year, month, day);
        picker.show();
    }

}
