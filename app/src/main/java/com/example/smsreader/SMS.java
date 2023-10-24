package com.example.smsreader;

public class SMS {

    String address;
    String name;

    String amount;

    String date;

    String time;
    String category;

    public SMS(String address, String name, String amount, String date, String time, String category)
    {
        this.address = address;
        this.name = name;
        this.amount = amount;
        this.date = date;
        this.time = time;
        this.category = category;
    }

    public String GetAddress()
    {
        return address;
    }

    public String GetName()
    {
        return name;
    }

    public String GetAmount()
    {
        return amount;
    }

    public String GetDate()
    {
        return date;
    }

    public String GetTime()
    {
        return time;
    }

    public String GetCategory()
    {
        return category;
    }
}
