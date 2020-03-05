package com.example.erasmusvalencia;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class InfosActivity extends AppCompatActivity {

    TextView chapter_transport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infos);

        chapter_transport = findViewById(R.id.info_transport);

        //chapter_transport.setText();
        //chapter_transport.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
