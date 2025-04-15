package com.example.likealion_mini_project.model;

// vo class (value object)
// entity class  // dbms
// dto class  // server 입장
public class Student {
    private int id;
    private String name;
    private String email;
    private String phone;
    private String memo;
    private String photo;

    public Student() {
    }
    public Student(int id, String name, String email, String phone, String memo, String photo) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.memo = memo;
        this.photo = photo;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getMemo() {
        return memo;
    }

    public String getPhoto() {
        return photo;
    }
}
