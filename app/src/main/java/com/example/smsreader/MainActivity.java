package com.example.smsreader;

import android.annotation.SuppressLint;
import android.app.Activity;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Telephony;
import java.util.Calendar;

import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
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
    private List<SMS> parsedMessages;
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

        //smsTextView = findViewById(R.id.smsTextView);
        //savebutton = findViewById(R.id.button);

        DateOfToday = "25-10-2023"; //testing date

        parsedMessages = readAndParseMessagesForGivenDate(DateOfToday);

        //smsTextView.setText(parsedMessages);

        ArrayAdapter<SMS> adapter = new ArrayAdapter<>(this, R.layout.sms_item, R.id.text_view_address, parsedMessages);

        // Set the adapter for the ListView
        ListView listView = findViewById(R.id.list_view_sms);
        listView.setAdapter(adapter);

        /*savebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parsedMessages = readAndParseMessagesForGivenDate(DateOfToday);

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
        });*/

    }
    private List<SMS> readAndParseMessagesForGivenDate(String DateOfToday) {
        try {
            List<SMS> messages = new ArrayList<SMS>();

            // Construct the query for SMS messages after the threshold date
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
            String selection = "(address LIKE ?";
            String sortOrder = "date DESC";

            List<String> selectionArgs = new ArrayList<String>(){{
                add("%BANK%");
                add("%BNK%");
                add("%ATM%");
                add("%SBI%");
                add("%KVB%");
                add("%HDFC%");
                add("%AXIS%");
                add("%UPI%");
            }};

            for (int i = 1; i < selectionArgs.size(); i++) {
                selection += " OR address LIKE ?";
            }

            selection += ") AND date > ?";

            selectionArgs.add(String.valueOf(thresholdTimeInMillis));
            Cursor cursor = getContentResolver().query(uri, projection, selection, selectionArgs.toArray(new String[0]), sortOrder);

            //List<SMS> messages = new ArrayList<SMS>();

            if (cursor != null && cursor.moveToFirst()) {

                do {
                    @SuppressLint("Range") String address = cursor.getString(cursor.getColumnIndex(Telephony.Sms.ADDRESS));
                    @SuppressLint("Range") String body = cursor.getString(cursor.getColumnIndex(Telephony.Sms.BODY));
                    @SuppressLint("Range") String date = cursor.getString(cursor.getColumnIndex(Telephony.Sms.DATE));

                    String particularDate = date;
                    formattedDate = dateFormat.format(new Date(Long.parseLong(particularDate)));

                    String timeFormat = "h:mm a"; // Define the desired time format
                    SimpleDateFormat timeFormatter = new SimpleDateFormat(timeFormat);

                    formattedTime = timeFormatter.format(new Date(Long.parseLong(particularDate)));

                    Log.d("SMS", "formatted date is " + formattedDate);

                    if (formattedDate.equals(DateOfToday)) {

                        Log.d("SMS", "Address: " + address);

                        if (body.contains("ATM")) {
                            Log.d("SMS", "ATM category");
                            //messages.add(new SMS(address, "", "", "", "", "ATM"));
                        } else if (body.contains("UPI")) {
                            Log.d("SMS", "UPI category");

                            //String message = "Dear UPI user A/C X5525 debited by 10.0 on date 25Oct23 trf to NAVIIN AGENCY Refno 366453056960. If not u? call 1800111109. -SBI";

                            // Define the regular expressions
                            String amountRegex = "\\b\\d+\\.\\d+\\b";
                            String nameRegex = "to\\s([A-Z\\s]+)\\s";
                            String dateRegex = "\\b\\d{1,2}[A-Z][a-z]{2}\\d{0,2}\\b";

                            // Compile the regular expressions
                            Pattern amountPattern = Pattern.compile(amountRegex);
                            Pattern namePattern = Pattern.compile(nameRegex);
                            Pattern datePattern = Pattern.compile(dateRegex);

                            // Match the patterns against the message
                            Matcher amountMatcher = amountPattern.matcher(body);
                            Matcher nameMatcher = namePattern.matcher(body);
                            Matcher dateMatcher = datePattern.matcher(body);

                            String receiverName = "", transferredAmount = "", transferredDate = "";

                            // Find and print the results
                            if (amountMatcher.find()) {
                                transferredAmount = amountMatcher.group();
                                System.out.println("Amount: " + transferredAmount);
                            }

                            if (nameMatcher.find()) {
                                receiverName = nameMatcher.group(1);
                                System.out.println("Receiver Name: " + receiverName);
                            }

                            if (dateMatcher.find()) {
                                transferredDate = dateMatcher.group();
                                System.out.println("Date: " + transferredDate);
                            }

                            messages.add(new SMS(address, receiverName, transferredAmount, transferredDate, "", "UPI"));
                        } else {
                            Log.d("SMS", "General category");
                            //messages.add(new SMS(address, "", "", "", "", "General"));
                        }

                        // Define a regular expression pattern to match the specific format.
                        /*Pattern pattern = Pattern.compile("Pattern pattern = Pattern.compile(\"Your A/c XX\\\\d+ KuloDinesh" + "INR (\\\\d+\\\\.\\\\d{2}) on (\\\\d{2}-[A-Za-z]{3}-\\\\d{2} \\\\d{2}:\\\\d{2}:\\\\d{2})\\\\* (.*?)\\\\*Avl BAl is INR(\\\\d+\\\\.\\\\d{2})\");");

                        Matcher matcher = pattern.matcher(body);
                        if (matcher.find()) {
                            amount = matcher.group(1);
                            receiver = matcher.group(4);

                            AutoCompleteTextView autoCompleteTextView = findViewById(R.id.autoCompleteTextView);
                            description = autoCompleteTextView.getText().toString();

                            hdfcSms.append("Date: ").append(formattedDate).append("\nTime: ").append(formattedTime).append("\nAmount: ₹").append(amount).append("\nReceiver: ").append(receiver).append("\nDescription: ").append(description).append("\n\n");
                        } else {
                            System.out.println("No match found in the SMS.");
                        }*/
                    }

                } while (cursor.moveToNext());

                cursor.close();
            } else {
                Log.d("SMS", "Cusor is null");
            }

            System.out.println(messages.size());
            return messages;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}