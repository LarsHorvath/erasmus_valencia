package com.example.erasmusvalencia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;

public class PlacesActivity extends BaseThemeChangerActivity {

    private ArrayList<Places> places = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);

        createPlaces();

        initRecyclerView();
    }

    private void initRecyclerView(){
        RecyclerView recyclerView = findViewById(R.id.recyclerView_places);
        MyAdapter2 adapter = new MyAdapter2(this,places);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    // create Places
    private void createPlaces(){
        Places esn1 =  new Places(1,"ESN @ UPV", "Primera planta, Edificio 4K, Camí de Vera, 46022 Valencia","https://goo.gl/maps/KMKxmK4ojN672npH9",R.mipmap.esn_upv);
        Places esn2 =  new Places(2,"ESN @ UV", "Aulari Sur, Aula S16, Av. dels Tarongers, 46022 Campus dels Tarongers, Valencia","https://goo.gl/maps/e35szZ5CMqS2Wioq9",R.mipmap.esn_uv);
        Places happyerasmus =  new Places(3,"Happy Erasmus", "Carrer de Ramon Llull, 21, 46021 València, Valencia","https://goo.gl/maps/pK5DFHfAS1Dbrtfq9",R.mipmap.happy_erasmus);
        Places erasmuslife =  new Places(4,"Erasmus Life", "Carrer de Ramon Llull, 21, 46021 València, Valencia","https://goo.gl/maps/bgytsN5HuZDwRES76",R.mipmap.erasmuslife_place);
        Places soyerasmus =  new Places(5,"Soy Erasmus", "Carrer del Clariano, 2, 46021 Valencia","https://goo.gl/maps/pQPHSXCZPrAR6fqD7",R.mipmap.soy_erasmus);

        places.add(esn1);
        places.add(esn2);
        places.add(happyerasmus);
        places.add(erasmuslife);
        places.add(soyerasmus);
    }
}
