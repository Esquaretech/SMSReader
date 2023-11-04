package com.example.smsreader;

public class SMS {

    String address;
    String name;
    String amount;
    String date;
    String time;
    String category;
    Boolean isDescriptionAdded;
    String id;

    public SMS(String address, String name, String amount, String date, String time, String category)
    {
        this.address = address;
        this.name = name;
        this.amount = "\u20B9 "+amount;
        this.date = date;
        this.time = time;
        this.category = category;
        this.isDescriptionAdded = false;
        this.id = address+date+time;

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

    public Boolean getIsDescriptionAdded(){return isDescriptionAdded;}

    public String getId(){return id;}

    @Override
    public String toString() {
        return "Address: " + address + ", Name: " + name + ", Amount: " + amount + ", Date: " + date + ", Time: " + time + ", Category: " + category;
    }
}
