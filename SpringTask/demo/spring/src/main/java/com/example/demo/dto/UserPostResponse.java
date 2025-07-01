package com.example.demo.dto;

import java.util.Date;

public class UserPostResponse {
    private Long id;
    private String name;
    private String email;
    private int age;
    private Date createdAt;
    private String message;

    public UserPostResponse(String s){
        this.message = s;
    }

    public UserPostResponse(){}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

}
