package com.example.attendease.database;

import android.os.Environment;

import com.example.attendease.model.Variables;

import java.util.Objects;

public class PARAMS {

    public static final int DB_VERSION = 1;
    public static final String DB_PATH = Environment.getExternalStorageDirectory()+"/";
    public static final String DB_NAME = ".attendEase";

    public static final String KEY_ID = "id";
    public static final String KEY_date = "date";
    public static final String KEY_subject = "subject";
    public static final String KEY_status = "status";

    public static final String KEY_ID_TYPE = "INTEGER";
    public static final String KEY_date_TYPE = "TEXT";
    public static final String KEY_subject_TYPE = "TEXT";
    public static final String KEY_status_TYPE = "TEXT";


}
