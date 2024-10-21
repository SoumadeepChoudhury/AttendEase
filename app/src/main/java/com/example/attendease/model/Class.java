package com.example.attendease.model;

import java.util.Date;

public class Class {
    private String date;
    private String subject;
    private String status;

    public Class(String date, String status, String subject) {
        this.date = date;
        this.status = status;
        this.subject = subject;
    }

    public Class(String date) {
        this.date = date;
        this.subject = "";
        this.status = "";
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
