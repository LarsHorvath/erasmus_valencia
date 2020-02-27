package com.example.erasmusvalencia;

import android.content.Context;
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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // Declarations
    RecyclerView recyclerView;
    String s1[], s2[], s3[];
    int images[];
    int imagesrc[] = {R.drawable.esn, R.drawable.happyerasmus, R.drawable.soyerasmus,
            R.drawable.erasmuslife, R.drawable.questionmark};
    private static final String TAG = "MainActivity";
    private static final String FILE_NAME = "events_list";
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
        events = new ArrayList<>();
        FileHandler fileHandler = new FileHandler(FILE_NAME);

        dateText = findViewById(R.id.dateView);
        ImageButton fButton = findViewById(R.id.fButton);
        ImageButton ffButton = findViewById(R.id.ffButton);
        ImageButton bButton = findViewById(R.id.bButton);
        ImageButton fbButton = findViewById(R.id.fbButton);
        filterSwitch = findViewById(R.id.filterSwitch);

        // Getting the current date and initializing today & tomorrow
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        SharedPreferences preferences = getSharedPreferences("init", MODE_PRIVATE);
        String day = preferences.getString("day", dateFormat.format(date));
        today = new Event.Date(day);
        tmw = today;
        restrict = preferences.getBoolean("restrictToDay", false);
        filterSwitch.setChecked(restrict);
        tmw = today.addDays(1);

        // Reading the file with the Events
        try {
            fileHandler.readFile();

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Parsing the read String to get the ArrayList of Event
        events = Event.parseFromJSONString(fileHandler.getContent());
        Collections.sort(events);

        // Updating the UI for the current date
        upDateEvents(today, tmw);

        // OnClickListeners for the buttons
        fButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                today = today.addDays(1);
                tmw = today.addDays(1);
                dateText.setText(today.dayToString());
                upDateEvents(today, tmw);
            }
        });
        ffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                today = today.addDays(7);
                tmw = today.addDays(1);
                dateText.setText(today.dayToString());
                upDateEvents(today, tmw);
            }
        });
        bButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                today = today.addDays(-1);
                tmw = today.addDays(1);
                dateText.setText(today.dayToString());
                upDateEvents(today, tmw);

            }
        });
        fbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                today = today.addDays(-7);
                tmw = today.addDays(1);
                dateText.setText(today.dayToString());
                upDateEvents(today, tmw);
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
                upDateEvents(today, tmw);
            }
        });

        upDateEvents(today, tmw);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences preferences = getSharedPreferences("init", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        // editor.clear();
        editor.apply();
    }

    // Updates the UI (the date and the events on that date)
    public void upDateEvents(Event.Date start, Event.Date end) {
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

    // Sets tiles invisible, fills them, and only the filled ones to visible
    public void displayEvents(ArrayList<Event> fEvents) {
        int size = fEvents.size();
        s1 = new String[size];
        s2 = new String[size];
        s3 = new String[size];
        images = new int[size];
        for (int i = 0; i < size; i++) {
            s1[i] = fEvents.get(i).getTitle();
            s2[i] = String.format(Locale.ENGLISH,  fEvents.get(i).getStartDate().toString());
            s3[i] = fEvents.get(i).getLocation();
            images[i] = imagesrc[fEvents.get(i).getCompanyID()];
        }

        recyclerView = findViewById(R.id.recyclerView);

        MyAdapter myAdapter = new MyAdapter(this, s1, s2, s3, imagesrc, fEvents);
        recyclerView.setAdapter(myAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
    
    // Class that helps reading and writing files, just close the whole class. (also not all functions used, some still from the game)
    public class FileHandler {

        private String fileName;
        private ArrayList<Player> players;
        private String content;

        public FileHandler(String fileName) {
            this.fileName = fileName;
            players = new ArrayList<>();
            content="no content yet";
            try {
                if (!fileExists()) createFile();
            } catch (IOException e) {
                Log.e(TAG, "FileHandler: exception at creation", e);
            }
        }

        public String getContent(){ return content; }

        private void writeScore(String player, int score) throws IOException {
            Player p = new Player(player, score);
            if (loadData() == null) {
                players = new ArrayList<>();
            }
            players.add(p);
            Collections.sort(players);
            saveData();
        }

        private void appendFile(String text) throws IOException {
            OutputStreamWriter fout = new OutputStreamWriter(openFileOutput(fileName, Context.MODE_APPEND));
            fout.write(text);
            fout.write(System.getProperty("line.separator"));
            fout.close();
        }

        private void writeFile(String text) throws IOException {
            OutputStreamWriter fout = new OutputStreamWriter(openFileOutput(fileName, Context.MODE_PRIVATE));
            fout.write(text);
            fout.close();
        }

        public void clearFile() throws IOException {
            this.writeFile("");
        }

        private void createFile() throws IOException {
            File file = new File(getFilesDir(), fileName);
            FileOutputStream fout = new FileOutputStream(file);
            fout.close();
        }

        public void readFile() throws IOException {
            if (!fileExists()) return;
            InputStreamReader fis = new InputStreamReader(openFileInput(fileName));
            BufferedReader fin = new BufferedReader(fis);

            String s, total = "";
            while ((s = fin.readLine())!=null) {
                total += " " + s;
            }
            fin.close();
            content = total;
            return;
        }

        private boolean fileExists() {
            File file = new File(getFilesDir(), fileName);
            return file.exists();
        }

        private void saveData() throws IOException {
            Gson gson = new Gson();
            String json = gson.toJson(players);
            writeFile(json);
        }

        public ArrayList<Player> loadData() throws IOException {
            Gson gson = new Gson();
            readFile();
            String json = content;
            Type type = new TypeToken<ArrayList<Player>>() {}.getType();
            players = gson.fromJson(json, type);
            return players;
        }

        public class Player implements Comparable<Player> {
            private String name;
            private Integer score;

            // Constructors
            public Player(String name, int score){
                this.name = name;
                this.score = score;
            }

            // Getters
            public String getName() {
                return name;
            }

            public Integer getScore() {
                return score;
            }

            @Override
            public int compareTo(Player p){
                return Integer.compare(this.score, p.score);
            }

        }

    }
}
