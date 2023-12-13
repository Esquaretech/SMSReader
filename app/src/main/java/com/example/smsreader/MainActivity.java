package com.example.smsreader;

import android.annotation.SuppressLint;
import android.app.Activity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import android.Manifest;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Toast;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Telephony;

import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends Activity {
    private static final int PERMISSION_REQUEST_READ_SMS = 1001;
    private ArrayList<SMS> parsedMessages;
    private List<SMS> storedMessage;
    public String dateOfToday;
    private RecyclerView recyclerView;
    private SMSListAdapter adapter;
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_READ_SMS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed to read messages
                dateOfToday = "01-11-2023";

                storedMessage = readStoredMessages();
                List<Message> messages = readMessages(dateOfToday);
                parsedMessages = new MessageHandler().ParseMessage(messages, dateOfToday, storedMessage);

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, PERMISSION_REQUEST_READ_SMS);
        } else {
            // Permission already granted, proceed to read messages
           //dateOfToday = "26-10-2023";

            dateOfToday = "01-11-2023";
            storedMessage = readStoredMessages();
            List<Message> messages = readMessages(dateOfToday);
            parsedMessages = new MessageHandler().ParseMessage(messages, dateOfToday, storedMessage);

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

            storedMessage = readStoredMessages();

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

    private List<SMS> readStoredMessages()
    {
        try {

            ArrayList<SMS> storedMessages = new ArrayList<SMS>();
            // Replace this path with the actual path where you store your JSON files

            String FILE_NAME = "sms_reader_data.json";
            String filePath = "/storage/emulated/0/Download/" + FILE_NAME;

            // Iterate through files in the directory
            File file = new File(filePath);

            if (file.isFile()) {
                // Read JSON data from the file
                StringBuilder jsonData = new StringBuilder();
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonData.append(line);
                }
                reader.close();

                // Parse JSON data and create SMS object
                JSONObject jsonObject = new JSONObject(jsonData.toString());
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

                // Print each stored SMS in log
                Log.d("StoredMessages", "Stored SMS: " + storedSMS.toString());

            }
            else {
                System.out.println("No file found");
            }
            return storedMessages;
        }
        catch (Exception ex)
        {
            System.out.println("Error while parsing stored messages. " + ex.getMessage());

            return null;
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