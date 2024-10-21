package com.example.attendease.database;



import java.util.Objects;

public class SQLQUERY {
    public static String createTable(String tableName,String[] columns,String[] columnType, String primaryKey, String autoIncrement){
        String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (";
        for(int i=0;i<columns.length;i++) {
            sql += columns[i] + " " + columnType[i];
            if(Objects.equals(columns[i], primaryKey)){
                sql += " PRIMARY KEY";
            }
            if(Objects.equals(autoIncrement, columns[i])){
                sql += " AUTOINCREMENT";
            }
            if(i<columns.length-1)
                sql += ",";
        }
        sql += ")";
        return sql;
    }

    public static String get(String TABLE_NAME,String KEY,String arg){
        String sql = "";
        if(KEY == null || arg == null){
            sql = "SELECT * FROM " + TABLE_NAME;
        }else{
            sql = "SELECT * FROM " + TABLE_NAME + " WHERE "+KEY + " = '" + arg+"';";
        }
        return sql;
    }

    public static String getCount(String TABLE_NAME,String KEY,String arg){
        String sql = "";
        if(KEY == null || arg == null){
            sql = "SELECT COUNT(*) FROM "+ TABLE_NAME;
        }else{
            sql = "SELECT COUNT(*) FROM "+ TABLE_NAME + " WHERE "+KEY + " = '"+arg+"'";
        }
        return sql;
    }
}
