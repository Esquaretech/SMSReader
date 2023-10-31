package com.example.smsreader;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class add_description_activity extends AppCompatActivity {
public String jsonData;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_description);

        // Get the selected user's details from Intent
        Intent intent = getIntent();
        String address = intent.getStringExtra("ADDRESS");
        String name = intent.getStringExtra("NAME");
        String amount = intent.getStringExtra("AMOUNT");
        String date = intent.getStringExtra("DATE");
        String time = intent.getStringExtra("TIME");
        String category = intent.getStringExtra("CATEGORY");
        String id = intent.getStringExtra("ID");

        // Display the details in your activity
        TextView textViewAddress = findViewById(R.id.text_Address);
        TextView textViewName = findViewById(R.id.text_Name);
        TextView textViewAmount = findViewById(R.id.text_Amount);
        TextView textViewDate = findViewById(R.id.text_Date);
        TextView textViewTime = findViewById(R.id.text_Time);
        TextView textViewCategory = findViewById(R.id.text_Category);

        textViewAddress.setText(address);
        textViewName.setText(name);
        textViewAmount.setText(amount);
        textViewDate.setText(date);
        textViewTime.setText(time);
        textViewCategory.setText(category);

        EditText editTextDescription = findViewById(R.id.editText_Description);
        Button buttonAction = findViewById(R.id.button_Save);
        buttonAction.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String Description = editTextDescription.getText().toString();

                String FILE_NAME = date;
                try {
                    JSONObject jsonObject = new JSONObject();

                    jsonObject.put("Date", address);
                    jsonObject.put("Receiver", name);
                    jsonObject.put("Amount", amount);
                    jsonObject.put("Date", date);
                    jsonObject.put("Time", time);
                    jsonObject.put("Description", Description);

                    jsonData = jsonObject.toString();

                    String filePath = "/storage/emulated/0/Download/" + FILE_NAME + ".json";

                    FileOutputStream fos = new FileOutputStream(filePath, true);
                    fos.write(jsonData.getBytes());
                    fos.close();


                    Toast.makeText(getApplicationContext(), "Data saved to Downloads directory", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(v.getContext(), MainActivity.class);
                    intent.putExtra("ID", id);
                    v.getContext().startActivity(intent);


            }
                catch (Exception e) {
                    throw new RuntimeException(e.getMessage().toString());
                }

                Log.d("Button", "position:"+ Description);
            }
        });
    }
}