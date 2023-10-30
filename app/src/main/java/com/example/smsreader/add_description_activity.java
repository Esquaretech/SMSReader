package com.example.smsreader;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class add_description_activity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_description);

        // Get the selected user's details from Intent
        Intent intent = getIntent();
        String name = intent.getStringExtra("NAME");
        String email = intent.getStringExtra("EMAIL");

        // Display the details in your activity
        TextView textViewName = findViewById(R.id.textName);
        TextView textViewEmail = findViewById(R.id.textAddress);

        textViewName.setText(name);
        textViewEmail.setText(email);
    }
}