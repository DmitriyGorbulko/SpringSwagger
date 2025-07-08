package com.example.notificationservice.dto;

public class UserEvent {
    private String email;
    private String operation;

    public UserEvent() {}

    public UserEvent(String email, String operation) {
        this.email = email;
        this.operation = operation;
    }

    public String getEmail() {
        return email;
    }

    public String getOperation() {
        return operation;
    }
}
