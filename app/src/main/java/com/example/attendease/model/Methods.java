package com.example.attendease.model;


import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.attendease.database.DB;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class Methods {

    public Methods(Context ctx) {
        if(ctx != null) {
            DB db = new DB(ctx);
            Variables VAR = Variables.getInstance();
            VAR.totalClassesAttended.setValue(db.getClassesAttended());
            VAR.totalClasses.setValue(db.getTotalClasses());
            ArrayList<ArrayList<String>> existingDataInDB = db.getData(getDate(new Date()));
            ArrayList<Class> classItems = new ArrayList<>();
            for(int i=0;i<existingDataInDB.size();i++){
                classItems.add(new Class(existingDataInDB.get(i).get(0),existingDataInDB.get(i).get(1),existingDataInDB.get(i).get(2)));
            }
            VAR.classListDate.setValue(classItems);
            updateOverview(db);
        }
    }

    public String getDate(Date date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
        return dateFormat.format(date);
    }

    public void updateOverview(DB db){
        Variables VAR = Variables.getInstance();
        ArrayList<ClassOverview> arr = new ArrayList<>();
        for(int i=0;i<VAR.subjects.getValue().size();i++){
            ClassOverview c= db.getRecordOfSubject(VAR.subjects.getValue().get(i).toString());
            arr.add(db.getRecordOfSubject(VAR.subjects.getValue().get(i).toString()));
        }
        VAR.subjectWiseClassOverview.setValue(arr);
    }

    public HashMap<String,dateParser> getDateMap(Date date){

        HashMap<String, dateParser> weekDates = new HashMap<>();

        String[] daysOfWeek = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        int daysToMonday = (currentDayOfWeek == Calendar.SUNDAY) ? -6 : Calendar.MONDAY - currentDayOfWeek;
        calendar.add(Calendar.DAY_OF_YEAR, daysToMonday); // Move to the Monday of the current week

        for (int i = 0; i < 7; i++) {
            weekDates.put(daysOfWeek[i], new dateParser(calendar.getTime()));

            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        return weekDates;
    }

    public static class dateParser {
        private int extractDate;
        private String fetch_Date;
        public Date date;
        public String[] access_ = new String[6];
        public String hashValue;

        public dateParser(Date dateObject){
            this.date = dateObject;
            String objDate = this.date.toString()+" ";
            String[] arrWords = new String[6];
            String dateWord = "";
            int ct = -1;
            for (int i = 0; i < objDate.length(); i++) {
                if (Objects.equals(objDate.charAt(i)+""," ")) {
                    ct++;
                    arrWords[ct] = dateWord;
                    dateWord = "";
                } else {
                    dateWord = dateWord + objDate.charAt(i);
                }
            }

            this.extractDate = Integer.parseInt(arrWords[2]);
            this.fetch_Date = arrWords[1] + " " + arrWords[2] + "," + " " + arrWords[5];
            this.access_ = arrWords;
        }

        public int getDate() {
            // It will return the Integer value of the input date.
            return this.extractDate;
        }

        @NonNull
        public String toString() {
            // It will fetch the date as : Oct 20, 2024 .
            return this.fetch_Date;
        }

        public String value(){
            return this.hashValue;
        }
    }

}


