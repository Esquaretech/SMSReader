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

    public String getAddress()
    {
        return address;
    }

    public String getName()
    {
        return name;
    }

    public String getAmount()
    {
        return amount;
    }

    public String getDate()
    {
        return date;
    }

    public String getTime()
    {
        return time;
    }

    public String getCategory()
    {
        return category;
    }

    @Override
    public String toString() {
        return "Address: " + address + ", Name: " + name + ", Amount: " + amount + ", Date: " + date + ", Time: " + time + ", Category: " + category;
    }
}
