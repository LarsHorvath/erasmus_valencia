package com.example.erasmusvalencia;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.EditText;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

// Activity that serves as a base class for MainActivity and EventsActivity which share most of their functionality.
public abstract class BaseRecyclerActivity extends BaseThemeChangerActivity {

    // Declarations
    private static final String TAG = "BaseRecyclerActivity";
    ArrayList<Event> eventsFiltered;
    RecyclerView recyclerView;
    EditText searchEdit;
    String filterText = "";
    boolean[] filterDialogSelection;
    String[] filterDialogItems;

    private ProgressDialog pd = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResID());

        // Get event data and score them in Event.allEvents and this.eventsFiltered
        doFileMagic();
        initialize();


        // Getting the current date and initializing today & tomorrow
        updatePreferences();
        setListeners();
        updateEvents();
        updateUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updatePreferences();
        updateEvents();
        updateUI();
    }

    protected abstract void initialize();
    protected void updatePreferences() {
        SharedPreferences preferences = getSharedPreferences("init", MODE_PRIVATE);
        for (int i = 0; i < filterDialogSelection.length; i++) {
            filterDialogSelection[i] = preferences.getBoolean(String.format(Locale.ENGLISH,"filterSelection%d", i), false);
        }
    }
    protected abstract int getLayoutResID();
    protected abstract int getMenuResID();
    // Filters the events according to new restriction, ATTENTION: implementation can or not also update the UI here.
    protected abstract void updateEvents();

    // Either reads and parses the events from the raw_event_data resource or parses the in that case already existing json array of type Event
    protected void doFileMagic() {
        ArrayList<Event> events = new ArrayList<>();
        // In case the Hashmap of all events is already populated
        FileHandler.context = this;
        if (Event.allEvents != null && Event.allEvents.size() != 0) {
            Log.i(TAG, "doFileMagic: we do not need to load any events, hashmap in Event still populated :)");
            events.addAll(Event.allEvents.values());
            Collections.sort(events);
        }
        else {
            // Show the ProgressDialog on this thread
            this.pd = ProgressDialog.show(this, "Working..", "Getting the events...", true, false);

            // Start a new thread that will download all the data
            new DownloadTask().execute("Any parameters my download task needs here");
        }
    }

    private class DownloadTask extends AsyncTask<String, Void, Object> {
        protected Object doInBackground(String... args) {
            Log.i("MyApp", "Background thread starting");

            ArrayList<Event> events = new ArrayList<>();
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
                Log.i(TAG, "onCreate: we are opening for first time or read from json failed");
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("first_time", false);
                editor.apply();
                events = Event.parseFromJSONString(FileHandler.readFromRaw());
                for (Event e : events) {
                    Event.allEvents.put(e.getId(), e);
                }
                Event.addEasterEggEvent();
            }
            return true;
        }

        protected void onPostExecute(Object result) {
            if (BaseRecyclerActivity.this.pd != null) {
                BaseRecyclerActivity.this.pd.dismiss();
            }
            updateEvents();
            updateUI();
        }
    }

    // Updates the UI
    protected void updateUI() {
        // at index zero is if search field activated
        if (filterDialogSelection.length != 0 && filterDialogSelection[0]) {
            searchEdit.setVisibility(View.VISIBLE);
        } else {
            searchEdit.setVisibility(View.GONE);
        }
        if (eventsFiltered == null) {
            updateEvents();
        }
        int[] fids = new int[eventsFiltered.size()];
        for (int i = 0; i < eventsFiltered.size(); i++) {
            fids[i] = eventsFiltered.get(i).getId();
        }

        EventTileAdapter myAdapter = new EventTileAdapter(this, fids);
        recyclerView.setAdapter(myAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(getMenuResID(), menu);
        return true;
    }

    protected AlertDialog makeFilterAlert() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(BaseRecyclerActivity.this);
        mBuilder.setTitle("Choose filters")
                .setMultiChoiceItems(filterDialogItems, filterDialogSelection, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                        SharedPreferences preferences = getSharedPreferences("init", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        try {
                            filterDialogSelection[i] = b;
                        } catch (IndexOutOfBoundsException be) {
                            be.printStackTrace();
                        }
                        editor.putBoolean(String.format(Locale.ENGLISH,"filterSelection%d",i),b);
                        editor.apply();
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updateEvents();
                        updateUI();
                    }
                });
        return mBuilder.create();
    }

    protected void setListeners() {
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
                updateUI();
            }
        });

    }
}
