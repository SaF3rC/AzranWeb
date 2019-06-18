package me.invakid.azran.entity;

public class Client {

    public String id, username, boughtOn, expire;

    public Client(String id, String username, String boughtOn, String expire) {
        this.id = id;
        this.username = username;
        this.boughtOn = boughtOn;
        this.expire = expire;
    }
}
