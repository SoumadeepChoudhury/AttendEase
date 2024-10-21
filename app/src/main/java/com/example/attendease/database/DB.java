package com.example.attendease.database;

import static com.example.attendease.database.PARAMS.*;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.attendease.model.ClassOverview;
import com.example.attendease.model.Variables;

import java.util.ArrayList;
import java.util.Objects;


public class DB extends SQLiteOpenHelper {
    private String TABLE_NAME = "";
    public DB(@Nullable Context context) {
        super(context, DB_PATH + DB_NAME, null, DB_VERSION);
        updateTABLE();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        if(this.TABLE_NAME.isEmpty()) {
            updateTABLE();
        }
        sqLiteDatabase.execSQL(SQLQUERY.createTable(this.TABLE_NAME, new String[]{KEY_ID, KEY_date, KEY_subject, KEY_status}, new String[]{KEY_ID_TYPE, KEY_date_TYPE, KEY_subject_TYPE, KEY_status_TYPE}, KEY_ID, KEY_ID));
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public ClassOverview insertData(String date,String subject,String status){
        updateTABLE();
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues val = new ContentValues();
        val.put(KEY_date,date);
        val.put(KEY_subject,subject);
        val.put(KEY_status,status);
        db.insert(this.TABLE_NAME,null,val);
        ClassOverview item = getRecordOfSubject(subject);
        db.close();
        return item;
    }

    public ArrayList<ArrayList<String>> getData(String date){
        updateTABLE();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(SQLQUERY.get(this.TABLE_NAME,KEY_date,date),null);
        ArrayList<ArrayList<String>> data = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                ArrayList<String> arr = new ArrayList<>();
                String recordDate = cursor.getString(cursor.getColumnIndexOrThrow(KEY_date));
                String subject = cursor.getString(cursor.getColumnIndexOrThrow(KEY_subject));
                String status = cursor.getString(cursor.getColumnIndexOrThrow(KEY_status));

                arr.add(recordDate); //Date
                arr.add(status); //Status
                arr.add(subject); //Subject
                data.add(arr);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return data;
    }

    public int getTotalClasses(){
        updateTABLE();
        //Provides me all the classes record COUNT present in the table of the database
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(SQLQUERY.getCount(this.TABLE_NAME,null,null), null);

        cursor.moveToFirst();
        int count = cursor.getInt(0);

        cursor.close();
        db.close();

        return count;
    }

    public int getClassesAttended(){
        updateTABLE();
        //Provides me all the class record COUNT present in the table of the database BUT ONLY WHICH IS PRESENT.
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(SQLQUERY.getCount(this.TABLE_NAME,KEY_status,"Present"), null);

        cursor.moveToFirst();
        int count = cursor.getInt(0);

        cursor.close();
        db.close();

        return count;

    }

    public ClassOverview getRecordOfSubject(String subject){
        //Provides me all the records of the selected SUBJECT.
        SQLiteDatabase db= this.getReadableDatabase();
        Cursor cursor = db.rawQuery(SQLQUERY.get(this.TABLE_NAME,KEY_subject,subject),null);
        int totalClass = 0;
        int attendedClass = 0;
        ClassOverview classOverviewItem;
        if (cursor.moveToFirst()) {
            do {
                totalClass += 1;
                String status = cursor.getString(cursor.getColumnIndexOrThrow(KEY_status));
                attendedClass += (Objects.equals(status,"Present")?1:0);
            } while (cursor.moveToNext());
        }
        float percent = (attendedClass*1.0f)/totalClass*100;
        classOverviewItem = new ClassOverview(subject,totalClass,attendedClass,percent);
        cursor.close();
        db.close();

        return classOverviewItem;
    }


    public void updateTABLE(){
        //Updating table
        String course = Variables.getInstance().course.getValue() == null ? "" : Variables.getInstance().course.getValue();
        String term = Variables.getInstance().term.getValue() == null ? "" : Variables.getInstance().term.getValue();
        if(!course.isEmpty() && !term.isEmpty()) {
            course = course.toLowerCase();
            course = course.replace(" ", "_");
            term = term.toLowerCase();
            term = term.replace(" ", "_");
            this.TABLE_NAME = course + "_" + term;
        }
    }

    public void createTable(){
        updateTABLE();
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(SQLQUERY.createTable(this.TABLE_NAME, new String[]{KEY_ID, KEY_date, KEY_subject, KEY_status},new String[]{KEY_ID_TYPE,KEY_date_TYPE,KEY_subject_TYPE,KEY_status_TYPE},KEY_ID,KEY_ID));
        db.close();
    }
}
