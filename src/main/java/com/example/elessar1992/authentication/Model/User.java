package com.example.elessar1992.authentication.Model;

/**
 * Created by elessar1992 on 4/30/19.
 */

public class User
{
    private String name, address, phoneNumber;

    public User(){
    }

    public User(String name, String address, String phoneNumber)
    {
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }
}

