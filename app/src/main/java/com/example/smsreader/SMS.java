package com.example.smsreader;

public class SMS {

    String address;
    String receiverName;
    String amount;
    String date;
    String time;
    String category;
    String description;
    Boolean isDescriptionAdded;
    String id;

    public SMS(String address, String receiverName, String amount, String date, String time, String category)
    {
        this.address = address;
        this.receiverName = receiverName;
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
        return receiverName;
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

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String value)
    {
        description = value;
    }

    public Boolean getIsDescriptionAdded(){return isDescriptionAdded;}

    public String getId(){return id;}

    @Override
    public String toString() {
        return "SMS{" +
                "id='" + id +
                ", address='" + address +
                ", receiver='" + receiverName +
                ", amount='" + amount +
                ", date='" + date +
                ", time='" + time +
                ", description='" + description +
                ", category='" + category +
                '}';
    }
}
