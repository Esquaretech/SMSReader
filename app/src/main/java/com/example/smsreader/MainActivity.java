package com.example.smsreader;

import android.annotation.SuppressLint;
import android.app.Activity;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import android.Manifest;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Telephony;

import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends Activity {
    private static final int PERMISSION_REQUEST_READ_SMS = 1001;
    private ArrayList<SMS> parsedMessages;
    public String dateOfToday;
    private RecyclerView recyclerView;
    private SMSListAdapter adapter;

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_READ_SMS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed to read messages
                //dateOfToday = "01-11-2023";
                dateOfToday = "24-10-2023";

                List<Message> messages = readMessages(dateOfToday);
                parsedMessages = new MessageHandler().ParseMessage(messages, dateOfToday);

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
           dateOfToday = "26-10-2023";
            //dateOfToday = "24-10-2023";

            List<Message> messages = readMessages(dateOfToday);
            parsedMessages = new MessageHandler().ParseMessage(messages, dateOfToday);

            adapter = new SMSListAdapter(parsedMessages);
            recyclerView.setAdapter(adapter);
        }


    }
    @Override
    protected void onResume() {
        super.onResume();

        try {

            Intent intent = getIntent();
            String id = intent.getStringExtra("ID");

            Log.d("ITEM old", ""+id);

            Log.d("ITEM Before", ""+parsedMessages.size());

            SMS itemToRemove = null;

            for (SMS sms : parsedMessages) {

                Log.d("ITEM", ""+sms.getId());

                if (sms.getId().equals(id)) {

                    Log.d("ITEM", "Matched");
                    itemToRemove =  sms;
                }
            }

            parsedMessages.remove(itemToRemove);

            adapter.updateData(parsedMessages);

            Log.d("Resume", id);
        }
        catch (Exception e){
            Log.e("Resume", "Error while resume" + e.getMessage().toString());
        }
    }

    private List<Message> readMessages(String DateOfToday) {
        try {
            List<Message> messages = new ArrayList<Message>();

            Uri uri = Uri.parse("content://sms/inbox");

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            Date thresholdDate;

            try {
                thresholdDate = dateFormat.parse(DateOfToday);
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
            } else {
                Log.d("SMS", "Cursor is null");
            }

            System.out.println(messages.size());
            return messages;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}