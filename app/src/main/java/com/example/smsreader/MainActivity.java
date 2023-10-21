package com.example.smsreader;

import android.annotation.SuppressLint;
import android.app.Activity;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Telephony;
import java.util.Calendar;
import android.widget.AutoCompleteTextView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends Activity {
    private TextView smsTextView;
    private Button savebutton;
    public String jsonData;
    private String hdfcSms;
    public String DateOfToday;
    public String formattedDate;
    public String formattedTime;
    public String amount;
    public String receiver;
    public String description;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        smsTextView = findViewById(R.id.smsTextView);
        savebutton = findViewById(R.id.button);

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // Note: Months are zero-based, so add 1.
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DateOfToday = day + "-" + month + "-" + year;
        //DateOfToday = "13-10-2023"; //testing date

        hdfcSms = readHDFCSms(DateOfToday);
        smsTextView.setText(hdfcSms);

        savebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hdfcSms = readHDFCSms(DateOfToday);

                String FILE_NAME = DateOfToday ;
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("Date", formattedDate);
                    jsonObject.put("Time", formattedTime);
                    jsonObject.put("Amount", amount);
                    jsonObject.put("Receiver", receiver);
                    jsonObject.put("Description", description);

                   jsonData = jsonObject.toString();


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {

                    String filePath = "/storage/emulated/0/Download/"+FILE_NAME+".json";

                    FileOutputStream fos = new FileOutputStream(filePath);
                    fos.write(jsonData.getBytes());
                    fos.close();


                    Toast.makeText(getApplicationContext(), "Data saved to Downloads directory", Toast.LENGTH_LONG).show();


                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

    }
    private String readHDFCSms(String DateOfToday) {
        Uri uri = Uri.parse("content://sms/inbox");
        Cursor cursor = getContentResolver().query(uri, null, "body LIKE ?", new String[]{"%HDFC Bank%"}, null);

        if (cursor != null && cursor.moveToFirst()) {
            StringBuilder hdfcSms = new StringBuilder();
            do {
                @SuppressLint("Range") String body = cursor.getString(cursor.getColumnIndex(Telephony.Sms.BODY));
                @SuppressLint("Range") String date = cursor.getString(cursor.getColumnIndex(Telephony.Sms.DATE));
                String particularDate = date;
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                formattedDate = dateFormat.format(new Date(Long.parseLong(particularDate)));

                String timeFormat = "h:mm a"; // Define the desired time format
                SimpleDateFormat timeFormatter = new SimpleDateFormat(timeFormat);

                formattedTime = timeFormatter.format(new Date(Long.parseLong(particularDate)));

                if (formattedDate.equals(DateOfToday)) {
                    Pattern pattern = Pattern.compile("Money Transfer:Rs (\\d+\\.\\d+) from .* on (\\d{2}-\\d{2}-\\d{2}) to (.+) UPI: (\\d+) Not you?");

                    Matcher matcher = pattern.matcher(body);
                    if (matcher.find()) {
                        amount = matcher.group(1);
                        receiver = matcher.group(3);

                        AutoCompleteTextView autoCompleteTextView = findViewById(R.id.autoCompleteTextView);
                        description = autoCompleteTextView.getText().toString();

                        hdfcSms.append("Date: ").append(formattedDate).append("\nTime: ").append(formattedTime).append("\nAmount: ₹").append(amount).append("\nReceiver: ").append(receiver).append("\nDescription: ").append(description).append("\n\n");

                    } else {
                        System.out.println("No match found in the SMS.");
                    }
                }
            } while (cursor.moveToNext());

            cursor.close();

            if (hdfcSms.length() > 0) {
                return hdfcSms.toString();
            } else {
                return "No HDFC Bank SMS messages found for " + DateOfToday;
            }
        }
        return "No HDFC Bank SMS messages found for " + DateOfToday;
    }
}