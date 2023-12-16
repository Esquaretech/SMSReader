package com.example.smsreader;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.Toast;

import android.content.Context;
import android.widget.Toast;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageHandler {

    private static boolean isIdInList(List<SMS> smsList, String targetId) {

        if(smsList.size() > 0) {
            for (SMS sms : smsList) {
                System.out.println("TargetId " + targetId);
                if (sms.getId().contains(targetId)) {

                    System.out.println("Entry found " + targetId);

                    return true;
                }
            }
            return false;
        }
        else
        {
            System.out.println("No entry in stored message");
            return false;
        }
    }

    public ArrayList<SMS> ParseMessage(List<Message> messages, String expectedDate, List<SMS> storedMessage) {

        ArrayList<SMS> parsedMessages = new ArrayList<SMS>();
        try {

            System.out.println("StoredMessage count " + storedMessage.size());
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

            String timeFormat = "h:mm a"; // Define the desired time format
            SimpleDateFormat timeFormatter = new SimpleDateFormat(timeFormat);

            for (Message message : messages) {

                String formattedDate = dateFormat.format(new Date(Long.parseLong(message.getDate())));

                String formattedTime = timeFormatter.format(new Date(Long.parseLong(message.getDate())));

                Log.d("SMS", "formatted date is " + formattedDate);

//                if (formattedDate.equals(expectedDate)) {

                    Log.d("SMS", message.toString());

                    if (message.getBody().contains("withdrawn")) {
                        Log.d("SMS", "ATM category");
                        //messages.add(new SMS(address, "", "", "", "", "ATM"));

                        //Define the regular expressions
                        String amountRegex = "\\b\\d+";
                        String nameRegex =  "\\bAt\\b\\s*(.*?)\\s*\\bOn\\b";

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

                        String id = message.getHeader() + formattedDate + formattedTime;
                        if(!isIdInList(storedMessage, id))
                            parsedMessages.add(new SMS(message.getHeader(), receiverName, transferredAmount, formattedDate, formattedTime, "ATM"));
                    }
                    else if (message.getBody().contains("UPI")
                            && !message.getBody().contains("timed out")
                            && !message.getBody().contains("requested")
                            && !message.getBody().contains("T&C")
                            && !message.getBody().contains("credited")
                            || message.getBody().contains("debited")) {
                        Log.d("SMS", "UPI category");

                        // Compile the regular expression for amount
                        String amountRegex = "\\d+\\.\\d+";
                        Pattern amountPattern = Pattern.compile(amountRegex);

                        // Match the amount pattern against the message
                        Matcher amountMatcher = amountPattern.matcher(message.getBody());

                        String transferredAmount = "";
                        // Find and print the amount result
                        if (amountMatcher.find()) {
                            transferredAmount = amountMatcher.group();
                            System.out.println("Amount: " + transferredAmount);
                        }
                        List<String> nameRegexs = new ArrayList<String>() {
                            {
                                add("to\\s([A-Z\\s]+[a-z\\s]+(.*?))\\s?UPI");
                                add("to\\s([A-Z\\s]+[a-z\\s]+(.*?))(?=UPI)");
                                add("to\\s([A-Z\\s]+[a-z\\s]+(.*?))\\s?Refno");
                                add("to\\s(.*?)(?=\\sUPI)");
                                add("to\\s*a/c\\s*\\*\\*(\\d+)");
                            }
                        };
                        // Iterate over the list of name regex patterns
                        String receiverName = "";
                        for (String nameRegex : nameRegexs) {
                            // Compile the current name regex pattern
                            Pattern namePattern = Pattern.compile(nameRegex);

                            // Match the name pattern against the message
                            Matcher nameMatcher = namePattern.matcher(message.getBody());

                            // If a match is found, print the result and break the loop
                            if (nameMatcher.find()) {
                                receiverName = nameMatcher.group(1);
                                System.out.println("Receiver Name: " + receiverName);
                                System.out.println("Receiver Name-" + nameMatcher.group(1));
                                break;
                            }

                            else {
                                Log.d("Name not matched", message.getBody());
                            }
                        }

                        String id = message.getHeader() + formattedDate + formattedTime;

                        if(!isIdInList(storedMessage, id))
                            parsedMessages.add(new SMS(message.getHeader(), receiverName, transferredAmount, formattedDate, formattedTime, "UPI"));
                    }
                    else {
                        Log.d("SMS", "General category");
                        //messages.add(new SMS(address, "", "", "", "", "General"));
                    }
//                } else{
//
//                    Log.d("SMS", message.getDate() + " is not match with " + formattedDate);
//                }
            }
        } catch (Exception ex) {
            Log.d("Parser", ex.getMessage());
        }
        return parsedMessages;
    }
}