package com.example.attendease.model;

public class ClassOverview {
    String subject;
    int totalClassesConducted;
    int totalClassesAttended;
    float percentage;

    public ClassOverview(String subject,int totalClassesConducted, int totalClassesAttended, float percentage) {
        this.percentage = percentage;
        this.subject = subject;
        this.totalClassesAttended = totalClassesAttended;
        this.totalClassesConducted = totalClassesConducted;
    }

    public float getPercentage() {
        return percentage;
    }

    public void setPercentage(float percentage) {
        this.percentage = percentage;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public int getTotalClassesAttended() {
        return totalClassesAttended;
    }

    public void setTotalClassesAttended(int totalClassesAttended) {
        this.totalClassesAttended = totalClassesAttended;
    }

    public int getTotalClassesConducted() {
        return totalClassesConducted;
    }

    public void setTotalClassesConducted(int totalClassesConducted) {
        this.totalClassesConducted = totalClassesConducted;
    }
}
