package com.example.vinicius.buzufbaapp.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.vinicius.buzufbaapp.R;
import com.example.vinicius.buzufbaapp.config.ConfiguracaoFireBase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
}
