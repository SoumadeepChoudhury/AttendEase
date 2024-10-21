package com.example.attendease.model;

import android.os.Environment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

public class JSONParser {
    Variables VARIABLES;

    public JSONParser() {
        this.VARIABLES = Variables.getInstance();
    }

    private String createJSONString(String name,String email,String[] courses,String[][] term,String[][][] subjects){
        String jsonString = "{" +
                "\"name\":" + name + "," +
                "\"email\":"+email+","+
                "\"courses\":"+Arrays.toString(courses)+","+
                "\"term\":{";
        for(int i=0;i<courses.length;i++){
            jsonString += courses[i] +":"+Arrays.toString(term[i])+(i==courses.length-1?"":",");
        }
        jsonString+="},\"subjects\":{";
        for(int i=0;i<courses.length;i++){
            jsonString += courses[i]+":{";
            for(int j=0;j<term[i].length;j++){
                jsonString += term[i][j]+":"+Arrays.toString(subjects[i][j])+(j==term[i].length-1?"":",");
            }
            jsonString += "}"+((i==courses.length-1)?"":",");
        }
        jsonString += "}}";
        return jsonString;
    }

    public void createJSONFile() {
        String name = "\""+this.VARIABLES.name.getValue().trim()+"\"";
        String email = "\""+VARIABLES.email.getValue().trim()+"\"";
        String[] course = new String[]{"\""+this.VARIABLES.course.getValue().trim()+"\""};
        String[][] term = new String[][]{{"\""+this.VARIABLES.term.getValue().trim()+"\""}};
        ArrayList<String> subjectsData = this.VARIABLES.subjects.getValue();
        String[][][] subjects = new String[1][1][subjectsData.size()];
        for(int i=0;i<subjectsData.size();i++){
            subjects[0][0][i]="\""+subjectsData.get(i).trim()+"\"";
        }
        String jsonString = this.createJSONString(name,email,course,term,subjects);
        try {
            JSONObject obj=new JSONObject(jsonString);
            FileWriter fileWriter = new FileWriter(Environment.getExternalStorageDirectory()+"/.attendEase_userProfile.json");
            fileWriter.write(obj.toString());
            fileWriter.close();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private JSONObject loadJSON(){
        //JSON handling
        JSONObject jsonObject = null;
        try {
            File jsonFile = new File(Environment.getExternalStorageDirectory()+"/.attendEase_userProfile.json");
            FileInputStream fileInputStream = new FileInputStream(jsonFile);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line;

            // Read the file line by line
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }

            // Convert the StringBuilder content to a JSON object
            jsonObject = new JSONObject(stringBuilder.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public void setJSONData(){
        JSONObject entireDataObj = this.loadJSON();
        this.VARIABLES.userData = entireDataObj;
        try {
            this.VARIABLES.name.setValue(entireDataObj.getString("name"));
            this.VARIABLES.email.setValue(entireDataObj.getString("email"));
            JSONArray courses = entireDataObj.getJSONArray("courses");
            String course = courses.getString(courses.length()-1);
            this.VARIABLES.course.setValue(course);
            JSONObject termObj = entireDataObj.getJSONObject("term");
            JSONArray termArray = termObj.getJSONArray(course);
            String term = termArray.getString(termArray.length()-1);
            this.VARIABLES.term.setValue(term);
            JSONObject subjectObj = entireDataObj.getJSONObject("subjects");
            subjectObj = subjectObj.getJSONObject(course);
            JSONArray subjectArray = subjectObj.getJSONArray(term);
            ArrayList<String> subjects = new ArrayList<>();
            for(int i=0;i<subjectArray.length();i++){
                subjects.add(subjectArray.getString(i));
            }
            this.VARIABLES.subjects.setValue(subjects);


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void updateJSONFile(JSONObject obj){
        File jsonFile = new File(Environment.getExternalStorageDirectory()+"/.attendEase_userProfile.json");
        try {
            FileWriter writer = new FileWriter(jsonFile);
            writer.write(obj.toString());
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
