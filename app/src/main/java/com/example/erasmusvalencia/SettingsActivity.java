package com.example.erasmusvalencia;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {
    Spinner themeSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = getSharedPreferences("settingsPref", MODE_PRIVATE);
        int theme = preferences.getInt("theme",0);
        if (theme == 0) {
            setTheme(R.style.DefaultTheme);
        }
        else if (theme == 2) {
            setTheme(R.style.Theme1);
        }
        setContentView(R.layout.activity_settings);

        themeSpinner = findViewById(R.id.themeSpinner);
        final String[] themes = {"Default", "Dark", "Theme 1", "Theme 2"};
        ArrayAdapter mAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, themes);
        themeSpinner.setAdapter(mAdapter);
        themeSpinner.setSelection(theme,false);
        themeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Context context = getApplicationContext();
                CharSequence text = String.format(Locale.ENGLISH, themes[position] + " selected");
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                getSharedPreferences("settingsPref", MODE_PRIVATE).edit().putInt("theme", position).apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        }
}
