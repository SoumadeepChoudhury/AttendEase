package com.example.attendease.model;

import androidx.lifecycle.MutableLiveData;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Variables {
    private static Variables instance;
    public static synchronized Variables getInstance(){
        if(instance == null){
            instance = new Variables();
        }
        return instance;
    }
    public MutableLiveData<Boolean> isKeyboardVisible = new MutableLiveData<>(false);
    public MutableLiveData<String> name = new MutableLiveData<>();
    public MutableLiveData<String> email = new MutableLiveData<>();
    public MutableLiveData<String> course = new MutableLiveData<>();
    public MutableLiveData<String> term = new MutableLiveData<>();
    public MutableLiveData<ArrayList<String>> subjects = new MutableLiveData<>(new ArrayList<>());
    public JSONObject userData = new JSONObject();
    public String tempValForSelectedItem = "";
    public String prevCourseTemp = "";

    public MutableLiveData<Integer> totalClassOfDate = new MutableLiveData<>(0);
//    public MutableLiveData<ArrayList<ArrayList<String>>> existingData = new MutableLiveData<>(new ArrayList<>());
    public MutableLiveData<ArrayList<Class>> classListDate = new MutableLiveData<>(new ArrayList<>());
    public MutableLiveData<Boolean> isLocked = new MutableLiveData<>(false);
    public MutableLiveData<Integer> totalClasses = new MutableLiveData<>(0);
    public MutableLiveData<Integer> totalClassesAttended = new MutableLiveData<>(0);
    public MutableLiveData<Float> totalPercentage = new MutableLiveData<>(0f);
    public MutableLiveData<ArrayList<ClassOverview>> subjectWiseClassOverview = new MutableLiveData<>(new ArrayList<>());

    public MutableLiveData<HashMap<String,Methods.dateParser>> dayDateHashMap = new MutableLiveData<>(new HashMap<>());
    public MutableLiveData<Date> selectedDate = new MutableLiveData<>(new Date());
}
