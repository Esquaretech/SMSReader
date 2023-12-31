package com.example.smsreader;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import android.Manifest;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Environment;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Telephony;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.navigation.NavigationView;
import androidx.navigation.ui.AppBarConfiguration;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity  {
    private static final int PERMISSION_REQUEST_READ_SMS = 1001;
    private ArrayList<SMS> parsedMessages;
    private ArrayList<SMS> storedMessages;
    public String dateOfToday;
    private RecyclerView recyclerView;
    private SMSListAdapter adapter;
    private DrawerLayout drawer;

    private NavigationView navigationView;

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_READ_SMS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Toolbar toolbar = findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);

                //Demo date for testing purpose
                dateOfToday = "01-11-2023";

                //Requesting Permission for Read/Write at device storage
                requestWritePermission();

                storedMessages = readStoredMessages();
                List<Message> messages = readMessages(dateOfToday);
                parsedMessages = new MessageHandler().ParseMessage(messages, dateOfToday, storedMessages);

                if(parsedMessages.size()==0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("No Messages found for this date " + dateOfToday)
                            .setTitle("Error")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // You can perform additional actions if needed
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }

                adapter = new SMSListAdapter(parsedMessages);
                recyclerView.setAdapter(adapter);
            } else {
                // Permission denied, handle accordingly (e.g., display a message, disable functionality)
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        } else {
            // For other menu items
            return super.onOptionsItemSelected(item);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        // Set up the navigation drawer
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();

        // Initialize the NavController
        // NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        // Set up ActionBar with NavController
        // NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        // Set up navigation menu item clicks
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Handle navigation item clicks here
                int id = item.getItemId();

                if (id == R.id.nav_home) {
                    // Handle home action
                } else if (id == R.id.nav_gallery) {
                    // Handle gallery action
                } else if (id == R.id.nav_slideshow) {
                    // Handle slideshow action
                }

                // Close the navigation drawer
                drawer.closeDrawers();
                return true;
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, PERMISSION_REQUEST_READ_SMS);
        } else {
            //Demo date for testing purpose
            dateOfToday = "01-11-2023";

            //Requesting Permission for Read/Write at device storage
            requestWritePermission();

            storedMessages = readStoredMessages();
            List<Message> messages = readMessages(dateOfToday);
            parsedMessages = new MessageHandler().ParseMessage(messages, dateOfToday, storedMessages);

            if(parsedMessages.size()==0){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("No Messages found for this date "+ dateOfToday)
                        .setTitle("Error")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // You can perform additional actions if needed
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
            adapter = new SMSListAdapter(parsedMessages);
            recyclerView.setAdapter(adapter);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        try {
            storedMessages = readStoredMessages();

            Intent intent = getIntent();
            String id = intent.getStringExtra("ID");

            if(id != null) {

                Log.d("ITEM old", "" + id);

                Log.d("ITEM Before", "" + parsedMessages.size());

                SMS itemToRemove = null;

                for (SMS sms : parsedMessages) {

                    Log.d("ITEM", "" + sms.getId());

                    if (sms.getId().equals(id)) {

                        Log.d("ITEM", "Matched");
                        itemToRemove = sms;
                        Log.d("to remove", itemToRemove.id);
                    }
                }
                parsedMessages.remove(itemToRemove);

                adapter.updateData(parsedMessages);

                Log.d("Resume", id);
            }
        }
        catch (Exception e){

            Log.e("Resume", "Error while resume " + e.getMessage().toString());
        }
    }
    private ArrayList<SMS> readStoredMessages() {
        try {
            ArrayList<SMS> storedMessages = new ArrayList<>();

            File downloadsDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            String fileName = "sms_reader_data.json";

            File file = new File(downloadsDirectory, fileName);

            // Check if the file exists
            if (!file.exists()) {
                Log.d("Main activity", "file not exist" );
                // If the file doesn't exist, create an empty file
                file.createNewFile();
            } else {
                // If the file exists, read its content
                Log.d("Main activity", "file  exists" );
                try {
                    // Read content from the file
                    BufferedReader reader = new BufferedReader(new FileReader(file));
                    StringBuilder jsonData = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        jsonData.append(line);
                    }
                    reader.close();

                    // If the file was empty or did not contain valid JSON, return an empty list
                    if (jsonData.length() == 0) {
                        System.out.print("json data empty in main activity");
                        return storedMessages;
                    }
                    try {
                        System.out.print("Length of json data in stored device "+jsonData.length());

                        String jsonArrayString = "[" + jsonData.toString() + "]";
                        JSONArray jsonArray = new JSONArray(jsonArrayString);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            Log.d("StoredMessages", "Stored JSON: " + jsonArray.getJSONObject(i));

                            String id = jsonObject.getString("Id");
                            String address = jsonObject.getString("Address");
                            String name = jsonObject.getString("Receiver");
                            String amount = jsonObject.getString("Amount");
                            String date = jsonObject.getString("Date");
                            String time = jsonObject.getString("Time");
                            String description = jsonObject.getString("Description");
                            String category = jsonObject.getString("Category");

                            SMS storedSMS = new SMS(address, name, amount, date, time, category);
                            storedSMS.id = id;
                            storedSMS.description = description;
                            storedMessages.add(storedSMS);

                        }


                    } catch (JSONException e) {
                        Log.e("readStoredMessages", "Error while parsing JSON array: " + e.getMessage());
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("StoredMessages count in main activity " + storedMessages.size());
            return storedMessages;
        } catch (Exception ex) {
            Log.e("ReadStoredMessages", "Error while parsing stored messages. " + ex.getMessage());
            return null;
        }
    }
    private void requestWritePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    private List<Message> readMessages(String DateOfToday) {
        try {
            List<Message> messages = new ArrayList<Message>();

            Uri uri = Uri.parse("content://sms/inbox");

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            Date thresholdDate;

            try {
                System.out.println("Date parse");
                thresholdDate = dateFormat.parse(DateOfToday);
                System.out.println("Date parse after" + thresholdDate.toString());
            } catch (ParseException e) {
                e.printStackTrace();
                return messages;
            }
            long thresholdTimeInMillis = thresholdDate.getTime();

            String[] projection = null;
            String sortOrder = "date DESC";
            List<String> selectionArgs = new ArrayList<String>(){{
                add("%BANK%");
                add("%BNK%");
                add("%ATM%");
                add("%SBI%");
                add("%KVB%");
                add("%HDFC%");
                add("%AXIS%");
                add("%INDBNK%");
                add("%INDIANBANK%");
                add("%IND%");
            }};
            String selection = "(address LIKE ?";

            for (int i = 1; i < selectionArgs.size(); i++) {
                selection += " OR address LIKE ?";
            }
            selection += ") AND date > ?";

            selectionArgs.add(String.valueOf(thresholdTimeInMillis));

            Cursor cursor = getContentResolver().query(uri, projection, selection, selectionArgs.toArray(new String[0]), sortOrder);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    @SuppressLint("Range") String address = cursor.getString(cursor.getColumnIndex(Telephony.Sms.ADDRESS));
                    @SuppressLint("Range") String body = cursor.getString(cursor.getColumnIndex(Telephony.Sms.BODY));
                    @SuppressLint("Range") String date = cursor.getString(cursor.getColumnIndex(Telephony.Sms.DATE));

                    messages.add(new Message(address, body, date));

                } while (cursor.moveToNext());
                cursor.close();
            }
            else {
                Log.d("SMS", "Cursor is null");
            }

            System.out.println(messages.size());
            return messages;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}