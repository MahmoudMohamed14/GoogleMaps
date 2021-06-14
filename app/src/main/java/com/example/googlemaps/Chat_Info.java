package com.example.googlemaps;

public class Chat_Info {
    public String sender;
    public String receiver;
    public String message;

    public Chat_Info(String sender, String receiver, String message) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
    }

    public Chat_Info() {

    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getMessage() {
        return message;
    }
}
