package com.example.attendease;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.example.attendease.database.DB;
import com.example.attendease.fragments.BottomSheetFragment;
import com.example.attendease.model.JSONParser;
import com.example.attendease.model.Variables;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class profile_activity extends AppCompatActivity {

    private float x1,x2;
    static final int MIN_DISTANCE = 150;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        Variables VARIABLES = Variables.getInstance();
        VARIABLES.isKeyboardVisible.setValue(false);


        //HANDLING KEYBOARD HIDING THE VIEW.
        // Get the root view of the layout
        View rootView = findViewById(android.R.id.content);
        // Add a Global Layout Listener to detect changes in the layout for keyboard
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                // Create a rectangle to hold the dimensions of the visible window
                Rect rect = new Rect();
                rootView.getWindowVisibleDisplayFrame(rect);

                // Get the height of the screen
                int screenHeight = rootView.getRootView().getHeight();

                // Calculate the height difference when the keyboard is visible
                int keypadHeight = screenHeight - rect.bottom;

                // Check if the keyboard is visible (height difference is more than 100 pixels)
                boolean isKeyboardNowVisible = keypadHeight > screenHeight * 0.15; // Keyboard is likely 15% or more of screen height

                if (isKeyboardNowVisible && !VARIABLES.isKeyboardVisible.getValue()) {
                    // Keyboard just became visible
                    VARIABLES.isKeyboardVisible.setValue(true);
                } else if (!isKeyboardNowVisible && VARIABLES.isKeyboardVisible.getValue()) {
                    // Keyboard just became hidden
                    VARIABLES.isKeyboardVisible.setValue(false);
                }
            }
        });

        TextView name = findViewById(R.id.name);
        TextView email = findViewById(R.id.email);
        EditText course = findViewById(R.id.course);
        EditText term = findViewById(R.id.term);
        ChipGroup subjectChipGroup = findViewById(R.id.subjectChipGroup);
        ImageButton addSubject = findViewById(R.id.addButton);
        ImageView back = findViewById(R.id.back);




        //OBSERVE CHANGES
        VARIABLES.course.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                course.setText(VARIABLES.course.getValue());
                //change the term and subject too
                try {
                    JSONObject term = VARIABLES.userData.getJSONObject("term");
                    JSONArray termArray = term.getJSONArray(VARIABLES.course.getValue());
                    if(termArray.length()>0) {
                        VARIABLES.term.setValue(termArray.getString(termArray.length() - 1));
                    }else VARIABLES.term.setValue("");
                    //onchanging value of term --> it will trigger the term observe
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        VARIABLES.term.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                term.setText(VARIABLES.term.getValue());
                if(VARIABLES.term.getValue().isEmpty()) {
                    addSubject.setClickable(false);
                }else addSubject.setClickable(true);
                //change the subject too
                try {
                    JSONObject subjects_obj = VARIABLES.userData.getJSONObject("subjects");
                    subjects_obj = subjects_obj.getJSONObject(VARIABLES.course.getValue());
                    if(subjects_obj.length() == 0 ){
                        VARIABLES.subjects.setValue(new ArrayList<>());
                    }else{
                        JSONArray termSubjects = subjects_obj.getJSONArray(VARIABLES.term.getValue());
                        ArrayList<String> tempArray = new ArrayList<>();
                        for (int i=0;i<termSubjects.length();i++) {
                            tempArray.add(termSubjects.get(i).toString());
                        }
                        VARIABLES.subjects.setValue(tempArray);
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }
        });
        VARIABLES.subjects.observe(this, new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(ArrayList<String> strings) {
                if(!strings.isEmpty()) {
                    new DB(profile_activity.this).createTable();
                }
                //Removing existing subjects
                while(subjectChipGroup.getChildCount()>0){
                    FrameLayout chip = (FrameLayout) subjectChipGroup.getChildAt(0);
                    subjectChipGroup.removeView(chip);
                }
                //Adding new subjects
                ArrayList<String> subjects = VARIABLES.subjects.getValue();
                for(int i=0;i<subjects.size();i++){
                    FrameLayout subjectView = (FrameLayout) LayoutInflater.from(profile_activity.this).inflate(R.layout.chip_layout,null);
                    LinearLayout subjectEditLayout = subjectView.findViewById(R.id.subjectEditLayout);
                    Chip subject = subjectView.findViewById(R.id.subject);
                    EditText subjectEdit = subjectView.findViewById(R.id.subjectEdit);
                    ImageView deleteSubject = subjectView.findViewById(R.id.deleteSubject);

                    ArrayList<String> existingSubjects = VARIABLES.subjects.getValue();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);


                    VARIABLES.isKeyboardVisible.observe(profile_activity.this, new Observer<Boolean>() {
                        @Override
                        public void onChanged(Boolean aBoolean) {
                            if(!aBoolean){
                                subjectEdit.clearFocus();
                            }
                        }
                    });
                    subject.setVisibility(View.VISIBLE);
                    subject.setText(subjects.get(i));
                    subjectEdit.setText(subjects.get(i));
                    subject.setClickable(true);
                    subjectEditLayout.setVisibility(View.GONE);

                    deleteSubject.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            existingSubjects.remove(subject.getText());
                            VARIABLES.subjects.setValue(existingSubjects);
                            subjectChipGroup.removeView(subjectView);
                        }
                    });
                    subjectEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View view, boolean hasFocus) {
                            if(!hasFocus) {
                                //Get the subject and continue the code.
                                String sub = subjectEdit.getText().toString();
                                if(!sub.isEmpty() && !existingSubjects.contains(sub)) {
                                    existingSubjects.add(sub);
                                    VARIABLES.subjects.setValue(existingSubjects);
                                    subjectEditLayout.setVisibility(View.GONE);
                                    subject.setText(sub);
                                    subject.setVisibility(View.VISIBLE);
                                    subject.setClickable(true);
                                }
                                subject.setChecked(false);
                            }
                        }
                    });
                    subjectEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                            if (i == EditorInfo.IME_ACTION_DONE) {

                                if (imm != null) {
                                    imm.hideSoftInputFromWindow(subjectEdit.getWindowToken(), 0);
                                }
                                return true;
                            }
                            return false;
                        }
                    });
                    subject.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            subject.setVisibility(View.GONE);
                            subjectEditLayout.setVisibility(View.VISIBLE);
                            subjectEdit.requestFocus();
                            existingSubjects.remove(subjectEdit.getText().toString());
                            if (imm != null) {
                                imm.showSoftInput(subjectEdit, InputMethodManager.SHOW_IMPLICIT);
                            }
                        }
                    });
                    subject.setOnCloseIconClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            existingSubjects.remove(subject.getText().toString());
                            VARIABLES.subjects.setValue(existingSubjects);
                            subjectChipGroup.removeView(subjectView);
                        }
                    });
                    subjectChipGroup.addView(subjectView);
                }

                try {
                    if(!VARIABLES.course.getValue().isEmpty() && !VARIABLES.term.getValue().isEmpty()) {
                        JSONObject subject_obj = VARIABLES.userData.getJSONObject("subjects");
                        JSONObject subjectObj_course = subject_obj.getJSONObject(VARIABLES.course.getValue());
                        subjectObj_course.put(VARIABLES.term.getValue(), new JSONArray(VARIABLES.subjects.getValue()));
                        subject_obj.put(VARIABLES.course.getValue(), subjectObj_course);
                        VARIABLES.userData.put("subjects", subject_obj);
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        name.setText(VARIABLES.name.getValue().toString());
        email.setText(VARIABLES.email.getValue().toString());
        course.setText(VARIABLES.course.getValue().toString());
        term.setText(VARIABLES.term.getValue().toString());
        //Adding the subjects to chipgroup
        ArrayList<String> subjects = VARIABLES.subjects.getValue();
        for(int i=0;i<subjects.size();i++){
            FrameLayout subjectView = (FrameLayout) LayoutInflater.from(this).inflate(R.layout.chip_layout,null);
            Chip subject = subjectView.findViewById(R.id.subject);
            LinearLayout subjectEditLayout = subjectView.findViewById(R.id.subjectEditLayout);
            EditText subjectEdit = subjectView.findViewById(R.id.subjectEdit);
            ImageView deleteSubject = subjectView.findViewById(R.id.deleteSubject);

            subject.setClickable(true);
            subject.setText(subjects.get(i));
            subjectEditLayout.setVisibility(View.GONE);
            subject.setVisibility(View.VISIBLE);

            subjectChipGroup.addView(subjectView);
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONParser jsonParser = new JSONParser();
                jsonParser.updateJSONFile(VARIABLES.userData);

                //Update existing UI if changed
                VARIABLES.selectedDate.setValue(new Date());

                //Change the course and term if either is empty
                if(VARIABLES.term.getValue().isEmpty() || VARIABLES.term.getValue() == null){
                    VARIABLES.course.setValue(VARIABLES.prevCourseTemp);
                    try {
                        JSONArray courseArray = VARIABLES.userData.getJSONArray("courses");
                        for (int i =0;i<courseArray.length();i++){
                            if(Objects.equals(courseArray.get(i),VARIABLES.prevCourseTemp)){
                                courseArray.remove(i);
                                break;
                            }
                        }
                        courseArray.put(VARIABLES.prevCourseTemp);
                        VARIABLES.userData.put("courses",courseArray);
                        new JSONParser().updateJSONFile(VARIABLES.userData);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    VARIABLES.prevCourseTemp = "";
                }

                finish();
            }
        });

        course.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (course.getRight() - course.getCompoundDrawables()[2].getBounds().width())) {
                    BottomSheetFragment bsf = new BottomSheetFragment("Courses");
                    bsf.show(getSupportFragmentManager(),bsf.getTag());
                    return true;
                }
            }
            return false;
        });

        term.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (term.getRight() - term.getCompoundDrawables()[2].getBounds().width())) {
                    BottomSheetFragment bsf = new BottomSheetFragment("Terms");
                    bsf.show(getSupportFragmentManager(),bsf.getTag());
                    return true;
                }
            }
            return false;
        });

        addSubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FrameLayout subjectView = (FrameLayout) LayoutInflater.from(profile_activity.this).inflate(R.layout.chip_layout,null);
                LinearLayout subjectEditLayout = subjectView.findViewById(R.id.subjectEditLayout);
                Chip subject = subjectView.findViewById(R.id.subject);
                EditText subjectEdit = subjectView.findViewById(R.id.subjectEdit);
                ImageView deleteSubject = subjectView.findViewById(R.id.deleteSubject);

                ArrayList<String> existingSubjects = VARIABLES.subjects.getValue();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);


                VARIABLES.isKeyboardVisible.observe(profile_activity.this, new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean aBoolean) {
                        if(!aBoolean){
                            subjectEdit.clearFocus();
                        }
                    }
                });
                subjectEdit.requestFocus();
                subject.setClickable(true);

                deleteSubject.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        existingSubjects.remove(subject.getText().toString());
                        VARIABLES.subjects.setValue(existingSubjects);
                        subjectChipGroup.removeView(subjectView);
                    }
                });
                subjectEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean hasFocus) {
                        if(!hasFocus) {
                            //Get the subject and continue the code.
                            String sub = subjectEdit.getText().toString();
                            if(!sub.isEmpty() && !existingSubjects.contains(sub)) {
                                existingSubjects.add(sub);
                                VARIABLES.subjects.setValue(existingSubjects);
                                subjectEditLayout.setVisibility(View.GONE);
                                subject.setText(sub);
                                subject.setVisibility(View.VISIBLE);
                                subject.setClickable(true);
                            }
                            subject.setChecked(false);
                        }
                    }
                });
                subjectEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                        if (i == EditorInfo.IME_ACTION_DONE) {

                            if (imm != null) {
                                imm.hideSoftInputFromWindow(subjectEdit.getWindowToken(), 0);
                            }
                            return true;
                        }
                        return false;
                    }
                });
                subject.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        subject.setVisibility(View.GONE);
                        subjectEditLayout.setVisibility(View.VISIBLE);
                        subjectEdit.requestFocus();
                        existingSubjects.remove(subjectEdit.getText().toString());
                        if (imm != null) {
                            imm.showSoftInput(subjectEdit, InputMethodManager.SHOW_IMPLICIT);
                        }
                    }
                });
                subject.setOnCloseIconClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        existingSubjects.remove(subject.getText().toString());
                        VARIABLES.subjects.setValue(existingSubjects);
                        subjectChipGroup.removeView(subjectView);
                    }
                });
                subjectChipGroup.addView(subjectView);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Variables VARIABLES = Variables.getInstance();
        JSONParser jsonParser = new JSONParser();
        jsonParser.updateJSONFile(VARIABLES.userData);

        //Update existing UI if changed
        VARIABLES.selectedDate.setValue(new Date());

        //Change the course and term if either is empty
        if(VARIABLES.term.getValue().isEmpty() || VARIABLES.term.getValue() == null){
            VARIABLES.course.setValue(VARIABLES.prevCourseTemp);
            try {
                JSONArray courseArray = VARIABLES.userData.getJSONArray("courses");
                for (int i =0;i<courseArray.length();i++){
                    if(Objects.equals(courseArray.get(i),VARIABLES.prevCourseTemp)){
                        courseArray.remove(i);
                        break;
                    }
                }
                courseArray.put(VARIABLES.prevCourseTemp);
                VARIABLES.userData.put("courses",courseArray);
                new JSONParser().updateJSONFile(VARIABLES.userData);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            VARIABLES.prevCourseTemp = "";
        }

    }
}