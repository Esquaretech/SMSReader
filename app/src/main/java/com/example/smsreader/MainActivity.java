package com.example.smsreader;

import android.annotation.SuppressLint;
import android.app.Activity;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Telephony;
import java.util.Calendar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Date;
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView smsTextView = findViewById(R.id.smsTextView);

        // Get the current date
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // Note: Months are zero-based, so add 1.
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Define the desired date in the format "yyyy-MM-dd"
        String desiredDate = day + "-" + month + "-" + year;


        // Read and filter HDFC Bank SMS messages for the desired date
        String hdfcSms = readHDFCSms(desiredDate);

        // Update the TextView with the HDFC Bank SMS messages
        smsTextView.setText(hdfcSms);

    }

    private String readHDFCSms(String desiredDate) {
        Uri uri = Uri.parse("content://sms/inbox");
        Cursor cursor = getContentResolver().query(uri, null, "body LIKE ?", new String[]{"%HDFC Bank%"}, null);
        if (cursor != null && cursor.moveToFirst()) {
            StringBuilder hdfcSms = new StringBuilder();
            do {
                @SuppressLint("Range") String body = cursor.getString(cursor.getColumnIndex(Telephony.Sms.BODY));

                @SuppressLint("Range") String date = cursor.getString(cursor.getColumnIndex(Telephony.Sms.DATE));
                String particularDate = date;
                // Parse the date from milliseconds to "yyyy-MM-dd" format
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                String formattedDate = dateFormat.format(new Date(Long.parseLong(particularDate)));

                // Check if the parsed date matches the desired date
                if (formattedDate.equals(desiredDate)) {
                    Pattern pattern = Pattern.compile("Money Transfer:Rs (\\d+\\.\\d+) from .* on (\\d{2}-\\d{2}-\\d{2}) to (.+)");
                    Matcher matcher = pattern.matcher(body);
                    if (matcher.find()) {
                        // Extract the matched information
                        String amount = matcher.group(1);
//                        String date = matcher.group(2);
                        String receiver = matcher.group(3);

                        hdfcSms.append("Date: ").append(formattedDate).append("\nAmount: ₹").append(amount).append("\nReceiver: ").append(receiver).append("\n\n");

                    } else {
                        System.out.println("No match found in the SMS.");
                    }



                }
            } while (cursor.moveToNext());

            cursor.close();

            if (hdfcSms.length() > 0) {
                return hdfcSms.toString();
            } else {
                return "No HDFC Bank SMS messages found for " + desiredDate;
            }
        }

        return "No HDFC Bank SMS messages found for " + desiredDate;
    }
}