package com.example.attendease.fragments;


import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.attendease.R;
import com.example.attendease.database.DB;
import com.example.attendease.model.Variables;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BottomSheetFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BottomSheetFragment extends BottomSheetDialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private String title;
    private Variables VARIABLES;
    private ArrayList<String> data = new ArrayList<>();

    public BottomSheetFragment(String title) {
        // Required empty public constructor
        this.title = title;
        this.VARIABLES = Variables.getInstance();
    }

    public BottomSheetFragment() {
        this.VARIABLES = Variables.getInstance();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BottomSheetFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BottomSheetFragment newInstance(String param1, String param2) {
        BottomSheetFragment fragment = new BottomSheetFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View bsf = inflater.inflate(R.layout.fragment_bottom_sheet, container, false);
        TextView title = bsf.findViewById(R.id.title);
        ChipGroup items = bsf.findViewById(R.id.chipGroup);
        ImageView add = bsf.findViewById(R.id.addButton);
        title.setText(this.title);

        //Add existing data
        if(Objects.equals(this.title, "Courses")){
            try {
                JSONArray courses = this.VARIABLES.userData.getJSONArray("courses");
                for(int i=0;i<courses.length();i++){
                    data.add(courses.getString(i));
                    items.addView(addChip(this.title,courses.getString(i),this.VARIABLES.course.getValue(),items,data));
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        }else if(Objects.equals(this.title,"Terms")){
            try {
                JSONObject term = this.VARIABLES.userData.getJSONObject("term");
                JSONArray termArray = term.getJSONArray(this.VARIABLES.course.getValue());
                for(int i=0;i<termArray.length();i++){
                    data.add(termArray.getString(i));
                    items.addView(addChip(this.title,termArray.getString(i),this.VARIABLES.term.getValue(),items,data));
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                items.addView(addChip(title.getText().toString(),null,null,items,data));
            }
        });

        return bsf;
    }

    private FrameLayout addChip(String title,String text,String selectedData,ChipGroup items,ArrayList<String> data){
        Variables VARIABLES = Variables.getInstance();
        FrameLayout item = (FrameLayout) LayoutInflater.from(getContext()).inflate(R.layout.chip_layout,null);
        LinearLayout editTextLayout = item.findViewById(R.id.subjectEditLayout);
        Chip chip = item.findViewById(R.id.subject);
        EditText editText = item.findViewById(R.id.subjectEdit);
        ImageView delete = item.findViewById(R.id.deleteSubject);

        if(text != null) {
            chip.setVisibility(View.VISIBLE);
            editTextLayout.setVisibility(View.GONE);
            chip.setText(text);
            chip.setClickable(true);
            chip.setCheckable(true);
            if(Objects.equals(text,selectedData)){
                chip.setChecked(true);
                VARIABLES.tempValForSelectedItem = selectedData.trim();
            }
        }else{
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //delete the item
                    items.removeView(item);
                }
            });
            editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    if(!hasFocus) {
                        //Get the subject and continue the code.
                        String sub = editText.getText().toString();
                        if(data.isEmpty()){
                            VARIABLES.tempValForSelectedItem = sub.trim();
                            chip.setChecked(true);
                        }
                        data.add(sub.trim());
                        chip.setVisibility(View.VISIBLE);
                        editTextLayout.setVisibility(View.GONE);
                        chip.setText(sub);
                    }
                }
            });
            editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    if (i == EditorInfo.IME_ACTION_DONE) {
                        editText.clearFocus();
                        return true;
                    }
                    return false;
                }
            });
        }
        chip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Add Code on chip select
                for (int i = 0; i < items.getChildCount(); i++) {
                    Chip chip = items.getChildAt(i).findViewById(R.id.subject);
                    chip.setChecked(false);  // Uncheck the chip
                }
                chip.setChecked(true);
                if(Objects.equals(title, "Courses")) {
                    VARIABLES.tempValForSelectedItem = chip.getText().toString().trim();
                }else if(Objects.equals(title, "Terms")){
                    VARIABLES.tempValForSelectedItem = chip.getText().toString().trim();
                }
            }
        });
        chip.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                data.remove(chip.getText());
                delete(chip.getText().toString(),title);
                VARIABLES.tempValForSelectedItem = data.get(data.size()-1).trim();
                items.removeView(item);
            }
        });
        return item;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if(Objects.equals(this.title, "Courses") && !this.VARIABLES.tempValForSelectedItem.isEmpty()) {
            this.data.remove(this.VARIABLES.tempValForSelectedItem.trim());
            this.data.add(this.VARIABLES.tempValForSelectedItem.trim());
            try {
                this.VARIABLES.userData.put("courses",new JSONArray(data));
                //Addition of blank set in term
                JSONObject term = this.VARIABLES.userData.getJSONObject("term");
                //Addition
                JSONObject subject = this.VARIABLES.userData.getJSONObject("subjects");
                for (String course : data) {
                    if (!term.has(course)) {
                        term.put(course,new JSONArray());
                        subject.put(course,new JSONObject());
                    }
                }
                this.VARIABLES.userData.put("term",term);
                this.VARIABLES.userData.put("subjects",subject);

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            this.VARIABLES.prevCourseTemp = this.VARIABLES.course.getValue();
            this.VARIABLES.course.setValue(this.VARIABLES.tempValForSelectedItem.trim());
            this.VARIABLES.tempValForSelectedItem = "";
        }else if(Objects.equals(this.title,"Terms") && !this.VARIABLES.tempValForSelectedItem.isEmpty()){
            this.data.remove(this.VARIABLES.tempValForSelectedItem.trim());
            this.data.add(this.VARIABLES.tempValForSelectedItem.trim());
            try {
                JSONObject term = VARIABLES.userData.getJSONObject("term");
                term.put(VARIABLES.course.getValue(),new JSONArray(data));

                //Updating subject part
                JSONObject subjectsMain = VARIABLES.userData.getJSONObject("subjects");
                JSONObject subjects = subjectsMain.getJSONObject(VARIABLES.course.getValue());
                for(int i=0;i<data.size();i++){
                    if(!subjects.has(data.get(i))){
                        subjects.put(data.get(i),new JSONArray());
                    }
                }
                subjectsMain.put(VARIABLES.course.getValue(),subjects);
                VARIABLES.userData.put("term",term);
                VARIABLES.userData.put("subjects",subjectsMain);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            this.VARIABLES.term.setValue(this.VARIABLES.tempValForSelectedItem.trim());
            this.VARIABLES.tempValForSelectedItem = "";
        }
    }

    public void delete(String text,String title){
        try {
            if(Objects.equals(title,"Courses")) {
                //Updated the courses
                JSONArray courses = VARIABLES.userData.getJSONArray("courses");
                for (int i = 0; i < courses.length(); i++) {
                    if (Objects.equals(courses.get(i), text)) {
                        courses.remove(i);
                        break;
                    }
                }
                VARIABLES.userData.put("courses", courses);
                //Updated the term
                JSONObject term = VARIABLES.userData.getJSONObject("term");
                term.remove(text);
                VARIABLES.userData.put("term", term);

                //Updated the subjects
                JSONObject subjects = VARIABLES.userData.getJSONObject("subjects");
                subjects.remove(text);
                VARIABLES.userData.put("subjects",subjects);
            }else if(Objects.equals(title,"Terms")){
                //Updated the terms
                JSONObject term = VARIABLES.userData.getJSONObject("term");
                JSONArray termArray = term.getJSONArray(VARIABLES.course.getValue());
                for(int i = 0;i<termArray.length();i++){
                    if(Objects.equals(termArray.get(i),text)){
                        termArray.remove(i);
                        break;
                    }
                }
                term.put(VARIABLES.course.getValue(),termArray);
                VARIABLES.userData.put("term",term);
                //Updated the subjects
                JSONObject subjects = VARIABLES.userData.getJSONObject("subjects");
                JSONObject subject_course = subjects.getJSONObject(VARIABLES.course.getValue());
                subject_course.remove(text);
                subjects.put(VARIABLES.course.getValue(),subject_course);
                VARIABLES.userData.put("subjects",subjects);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}