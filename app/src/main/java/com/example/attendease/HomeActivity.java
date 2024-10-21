package com.example.attendease;

import com.example.attendease.adapter.DayDateAdapter;
import com.example.attendease.adapter.SubjectWiseClassAdapter;
import com.example.attendease.database.DB;
import com.example.attendease.model.Class;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.attendease.adapter.ClassAdapter;
import com.example.attendease.model.ClassOverview;
import com.example.attendease.model.Methods;
import com.example.attendease.model.Variables;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    Variables VARIABLES = Variables.getInstance();
    Methods METHODS;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        METHODS = new Methods(this);



        ImageView profileIcon = findViewById(R.id.profileIcon);
        ImageView homeTab = findViewById(R.id.homeTab);
        ImageView overviewTab = findViewById(R.id.overviewTab);
        ImageView addClasses = findViewById(R.id.addClasses);

        homeTab.setImageResource(R.drawable.home_filled);
        overviewTab.setImageResource(R.drawable.chart);

        profileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profile = new Intent(HomeActivity.this,profile_activity.class);
                startActivity(profile);
            }
        });

        //HOME FUNCTIONS
        FrameLayout home = findViewById(R.id.home);
        TextView fullDate = findViewById(R.id.fullDate);
        TextView totalClassesToday = findViewById(R.id.totalClassesToday);
        RecyclerView dayCardRecyclerView = findViewById(R.id.dayCardRecyclerView);
        RecyclerView classes = findViewById(R.id.classes);





        fullDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialDatePicker<Long> materialDatePicker = MaterialDatePicker.Builder.datePicker()
                        .setTitleText("Select Date")
                        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                        .build();
                materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                    @Override
                    public void onPositiveButtonClick(Long selection) {
                        VARIABLES.selectedDate.setValue(new Date(selection));
                        VARIABLES.dayDateHashMap.setValue(METHODS.getDateMap(new Date(selection)));
                    }
                });
                materialDatePicker.show(getSupportFragmentManager(),materialDatePicker.getTag());
            }
        });

        dayCardRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        dayCardRecyclerView.setAdapter(new DayDateAdapter(this,this.VARIABLES.dayDateHashMap,this));

        totalClassesToday.setText(this.VARIABLES.totalClassOfDate.getValue().toString());
        this.VARIABLES.totalClassOfDate.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                totalClassesToday.setText(integer.toString());
                METHODS.updateOverview(new DB(HomeActivity.this));
            }
        });



        ClassAdapter classAdapter = new ClassAdapter(this,this.VARIABLES.classListDate,this);
        this.VARIABLES.totalClassOfDate.setValue(classAdapter.getItemCount());
        classes.setLayoutManager(new LinearLayoutManager(this));
        classes.setAdapter(classAdapter);

        this.VARIABLES.selectedDate.observe(this, new Observer<Date>() {
            @Override
            public void onChanged(Date date) {
                fullDate.setText(METHODS.getDate(VARIABLES.selectedDate.getValue()).toString());
                //Change dataset of existing classListDate
                ArrayList<ArrayList<String>> existingDataInDB = new DB(HomeActivity.this).getData(METHODS.getDate(VARIABLES.selectedDate.getValue()));
                ArrayList<Class> classItems = new ArrayList<>();
                for(int i=0;i<existingDataInDB.size();i++){
                    classItems.add(new Class(existingDataInDB.get(i).get(0),existingDataInDB.get(i).get(1),existingDataInDB.get(i).get(2)));
                }
                VARIABLES.classListDate.setValue(classItems);
                //notify the adapter
                classAdapter.notifyDataSetChanged();
                VARIABLES.totalClassOfDate.setValue(VARIABLES.classListDate.getValue().size());
                VARIABLES.dayDateHashMap.setValue(METHODS.getDateMap(VARIABLES.selectedDate.getValue()));
                VARIABLES.totalClasses.setValue(new DB(HomeActivity.this).getTotalClasses());
                VARIABLES.totalClassesAttended.setValue(new DB(HomeActivity.this).getClassesAttended());
            }
        });

        addClasses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VARIABLES.isLocked.setValue(true);
                ArrayList<Class> tempArr = VARIABLES.classListDate.getValue();
                tempArr.add(new Class(fullDate.getText().toString()));
                VARIABLES.classListDate.setValue(tempArr);
                classAdapter.notifyDataSetChanged();
                classes.scrollToPosition(classAdapter.getItemCount()-1);
            }
        });
        this.VARIABLES.isLocked.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean){
                    addClasses.setClickable(false);
                }else addClasses.setClickable(true);
            }
        });



        //OVERVIEW FUNCTIONS
        FrameLayout overview = findViewById(R.id.overview);
        TextView totalSubjectsCount = findViewById(R.id.totalSubjectsCount);
        TextView totalClassesCount = findViewById(R.id.totalClassesCount);
        TextView totalClassesAttended = findViewById(R.id.totalClassesAttended);
        TextView totalPercentage = findViewById(R.id.percentage);
        RecyclerView subjectWiseClasses = findViewById(R.id.subjectWiseClasses);
        this.VARIABLES.subjects.observe(this, new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(ArrayList<String> strings) {
                totalSubjectsCount.setText(VARIABLES.subjects.getValue().size()+"");

            }
        });
        this.VARIABLES.totalClasses.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                totalClassesCount.setText(VARIABLES.totalClasses.getValue().toString());
            }
        });
        this.VARIABLES.totalClassesAttended.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                totalClassesAttended.setText(VARIABLES.totalClassesAttended.getValue().toString());
                VARIABLES.totalPercentage.setValue((VARIABLES.totalClassesAttended.getValue()*1.0f)/VARIABLES.totalClasses.getValue()*100);
            }
        });
        this.VARIABLES.totalPercentage.observe(this, new Observer<Float>() {
            @Override
            public void onChanged(Float _) {
                if(VARIABLES.totalPercentage.getValue().toString() == "NaN"){
                    totalPercentage.setText("0%");
                }else {
                    int percent = VARIABLES.totalPercentage.getValue() == 0.0f ? 0 : Math.round(VARIABLES.totalPercentage.getValue());
                    totalPercentage.setText(percent + "%");
                }
            }
        });

        //Add subjectWiseClass in RecyclerView
        SubjectWiseClassAdapter subjectWiseClassAdapter = new SubjectWiseClassAdapter(this,this.VARIABLES.subjectWiseClassOverview,this);
        subjectWiseClasses.setLayoutManager(new LinearLayoutManager(this));
        subjectWiseClasses.setAdapter(subjectWiseClassAdapter);





        homeTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addClasses.setClickable(true);
                home.setVisibility(View.VISIBLE);
                overview.setVisibility(View.INVISIBLE);
                homeTab.setImageResource(R.drawable.home_filled);
                overviewTab.setImageResource(R.drawable.chart);
            }
        });

        overviewTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addClasses.setClickable(false);
                home.setVisibility(View.INVISIBLE);
                overview.setVisibility(View.VISIBLE);
                homeTab.setImageResource(R.drawable.home);
                overviewTab.setImageResource(R.drawable.chart_filled);
            }
        });
    }
}