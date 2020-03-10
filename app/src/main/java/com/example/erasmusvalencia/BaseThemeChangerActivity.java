package com.example.erasmusvalencia;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;

public abstract class BaseThemeChangerActivity extends AppCompatActivity {
    final static int DEFAULT_THEME = 0;
    final static int THEME_1 = 1;
    public int [] themes = {R.style.DefaultTheme, R.style.Theme1};
    public static int theme;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateTheme();
    }

    protected void updateTheme() {
        SharedPreferences preferences = getSharedPreferences("settingsPref", MODE_PRIVATE);
        theme = preferences.getInt("theme", DEFAULT_THEME);
        if (theme == DEFAULT_THEME) {
            setTheme(R.style.DefaultTheme);
        }
        else if (theme == THEME_1) {
            setTheme(R.style.Theme1);
        }
    }

}
