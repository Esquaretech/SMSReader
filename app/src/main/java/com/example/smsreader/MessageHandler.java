package com.example.smsreader;

import android.util.Log;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageHandler {

    public ArrayList<SMS> ParseMessage(List<Message> messages, String expectedDate) {

        ArrayList<SMS> parsedMessages = new ArrayList<SMS>();

        try {

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

            String timeFormat = "h:mm a"; // Define the desired time format
            SimpleDateFormat timeFormatter = new SimpleDateFormat(timeFormat);

            for (Message message : messages) {

                String formattedDate = dateFormat.format(new Date(Long.parseLong(message.getDate())));

                String formattedTime = timeFormatter.format(new Date(Long.parseLong(message.getDate())));

                Log.d("SMS", "formatted date is " + formattedDate);

                if (formattedDate.equals(expectedDate)) {

                    Log.d("SMS", message.toString());

                    if (message.getBody().contains("ATM")) {
                        Log.d("SMS", "ATM category");
                        //messages.add(new SMS(address, "", "", "", "", "ATM"));
                    } else if (message.getBody().contains("UPI")) {
                        Log.d("SMS", "UPI category");

                        // Define the regular expressions
                        String amountRegex = "\\b\\d+\\.\\d+\\b";
                        String nameRegex = "to\\s([A-Z\\s]+[a-z\\s]+)\\s";
                        // Compile the regular expressions
                        Pattern amountPattern = Pattern.compile(amountRegex);
                        Pattern namePattern = Pattern.compile(nameRegex);

                        // Match the patterns against the message
                        Matcher amountMatcher = amountPattern.matcher(message.getBody());
                        Matcher nameMatcher = namePattern.matcher(message.getBody());

                        String receiverName = "", transferredAmount = "";

                        // Find and print the results
                        if (amountMatcher.find()) {
                            transferredAmount = amountMatcher.group();
                            System.out.println("Amount: " + transferredAmount);
                        }

                        if (nameMatcher.find()) {
                            receiverName = nameMatcher.group(1);
                            System.out.println("Receiver Name: " + receiverName);
                        }

                        parsedMessages.add(new SMS(message.getHeader(), receiverName, transferredAmount, formattedDate, formattedTime, "UPI"));
                    } else {
                        Log.d("SMS", "General category");
                        //messages.add(new SMS(address, "", "", "", "", "General"));
                    }
                } else{
                    Log.d("SMS", message.getDate() + "is not match with " + formattedDate);
                }
            }
        } catch (Exception ex) {
            Log.d("Parser", ex.getMessage());
        }

        return parsedMessages;
    }
}