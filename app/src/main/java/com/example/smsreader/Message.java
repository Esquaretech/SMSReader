package com.example.smsreader;

public class Message {

    String header = "", body = "", date = "";

    public Message(String header, String body, String date)
    {
        this.header = header;
        this.body = body;
        this.date = date;
    }

    public String getHeader()
    {
        return header;
    }

    public String getBody()
    {
        return body;
    }

    public String getDate()
    {
        return date;
    }

    @Override
    public String toString() {
        return "Header: " + header + ", Body: " + body + ", Date: " + date;
    }
}
