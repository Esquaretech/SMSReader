package com.example.smsreader;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

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

                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("Id", id);
                    jsonObject.put("Address", address);
                    jsonObject.put("Receiver", name);
                    jsonObject.put("Amount", amount);
                    jsonObject.put("Date", date);
                    jsonObject.put("Time", time);
                    jsonObject.put("Description", Description);
                    jsonObject.put("Category", category);

                    jsonData = jsonObject.toString();

                    File downloadsDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

                    String fileName = "sms_reader_data.json";

                    // Create the file object
                    File file = new File(downloadsDirectory, fileName);

                    // Check if the file already exists
                    try {
                        Log.d("add description activity", "file not exist" );
                        // Create the file if it doesn't exist
                        if (!file.exists()) {
                            file.createNewFile();
                        }
                        else {
                            Log.d("add description activity", "file  exist" );
                            // Write JSON array to the file
                            FileWriter fileWriter = new FileWriter(file);
                            fileWriter.write(jsonData.toString());
                            fileWriter.close();
                            Log.d("JsonFileWriter", "JSON array written to file: " + file.getAbsolutePath());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    Toast.makeText(getApplicationContext(), "Data saved to Downloads directory", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(v.getContext(), MainActivity.class);
                    intent.putExtra("ID", id);
                    v.getContext().startActivity(intent);

                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage().toString());
                }

                Log.d("Button", "position:" + Description);
            }
        });
    }
}