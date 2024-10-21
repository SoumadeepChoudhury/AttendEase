package com.example.attendease;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
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
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;

import com.example.attendease.database.DB;
import com.example.attendease.model.JSONParser;
import com.example.attendease.model.Variables;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    final int REQUEST_EXTERNAL_STORAGE = 1;
    final String PERMISSION_READ_EXTERNAL_STORAGE =  Manifest.permission.READ_EXTERNAL_STORAGE;
    final String PERMISSION_WRITE_EXTERNAL_STORAGE = android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

    public static DB db;
    public static JSONParser jsonParser;

    Variables VARIABLES = Variables.getInstance();

    ViewGroup.LayoutParams params;
    int dpToPixel(int dp){ // For example, 200 dp
        float scale = getResources().getDisplayMetrics().density;
        int heightInPixels = (int)(dp * scale + 0.5f);
        return heightInPixels;
    }


    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==REQUEST_EXTERNAL_STORAGE){
            if(permissions.length>=2){
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    //Code
                }
            }
        }
    }


    final private ActivityResultLauncher<Intent> storageActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.R){
                        if(Environment.isExternalStorageManager()) {
                            ActivityCompat.requestPermissions(
                                    MainActivity.this,
                                    new String[]{PERMISSION_READ_EXTERNAL_STORAGE,PERMISSION_WRITE_EXTERNAL_STORAGE},
                                    REQUEST_EXTERNAL_STORAGE
                            );
                            Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        if(ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.MANAGE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
    );

    public boolean checkPermission(){
        boolean isGranted = false;
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.R){
            isGranted=Environment.isExternalStorageManager();
        }else{
            if(ActivityCompat.checkSelfPermission(this,PERMISSION_READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this,PERMISSION_WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED){
                isGranted=true;
            }
        }
        return isGranted;
    }

    void requestPermissions(){

        String[] PERMISSIONS_STORAGE = {
               PERMISSION_READ_EXTERNAL_STORAGE,
                PERMISSION_WRITE_EXTERNAL_STORAGE
        };

        if(!checkPermission()) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, PERMISSION_READ_EXTERNAL_STORAGE) && !ActivityCompat.shouldShowRequestPermissionRationale(this, PERMISSION_WRITE_EXTERNAL_STORAGE)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("This app requires MANAGE_EXTERNAL_STORAGE permission for particular feature to work as expected.")
                        .setTitle("Permission Required")
                        .setCancelable(false)
                        .setPositiveButton("Ok", ((dialog, which) -> {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                //Android is R or above
                                try {
                                    Intent intent = new Intent();
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                                    Uri uri = Uri.fromParts("package", this.getPackageName(), null);
                                    intent.setData(uri);
                                    storageActivityResultLauncher.launch(intent);
                                } catch (Exception e) {
                                    Intent intent = new Intent();
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                                    storageActivityResultLauncher.launch(intent);
                                }
                            } else {
                                ActivityCompat.requestPermissions(this, new String[]{PERMISSION_READ_EXTERNAL_STORAGE, PERMISSION_WRITE_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE);
                            }
                        })).setNegativeButton("Cancel", ((dialog, which) -> dialog.dismiss()));
                builder.show();


            } else {
                ActivityCompat.requestPermissions(
                        this,
                        PERMISSIONS_STORAGE,
                        REQUEST_EXTERNAL_STORAGE
                );
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        if(new File(Environment.getExternalStorageDirectory()+"/.attendEase_userProfile.json").exists()){
            jsonParser = new JSONParser();
            jsonParser.setJSONData();
            Intent homeIntent = new Intent(MainActivity.this,HomeActivity.class);
            startActivity(homeIntent);
            finish();
        }
        setContentView(R.layout.activity_main);

        requestPermissions();
        db = new DB(this);
        jsonParser = new JSONParser();


        //HANDLING KEYBOARD HIDING THE VIEW.
        // Get the root view of the layout
        View rootView = findViewById(android.R.id.content);
        ScrollView scrollView = findViewById(R.id.scrollview);
        // Get the current LayoutParams of the ScrollView
        params = scrollView.getLayoutParams();
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
                 params.height = dpToPixel(300);
                 scrollView.setLayoutParams(params);
             } else if (!isKeyboardNowVisible && VARIABLES.isKeyboardVisible.getValue()) {
                 // Keyboard just became hidden
                 VARIABLES.isKeyboardVisible.setValue(false);
                 params.height = dpToPixel(650);
                 scrollView.setLayoutParams(params);
             }
         }
        });


        EditText name = findViewById(R.id.name);
        EditText email = findViewById(R.id.email);
        EditText course = findViewById(R.id.courseField);
        EditText term = findViewById(R.id.termField);
        ChipGroup subjectChipGroup = findViewById(R.id.subjectChipGroup);
        ImageButton addSubject = findViewById(R.id.addButton);
        //Get Subjects
        LinearLayout done = findViewById(R.id.doneLayout);

        addSubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FrameLayout subjectView = (FrameLayout) LayoutInflater.from(MainActivity.this).inflate(R.layout.chip_layout,null);
                LinearLayout subjectEditLayout = subjectView.findViewById(R.id.subjectEditLayout);
                Chip subject = subjectView.findViewById(R.id.subject);
                EditText subjectEdit = subjectView.findViewById(R.id.subjectEdit);
                ImageView deleteSubject = subjectView.findViewById(R.id.deleteSubject);

                ArrayList<String> existingSubjects = VARIABLES.subjects.getValue();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);


                VARIABLES.isKeyboardVisible.observe(MainActivity.this, new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean aBoolean) {
                        if(!aBoolean){
                            subjectEdit.clearFocus();
                        }
                    }
                });
                subjectEdit.requestFocus();

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

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VARIABLES.name.setValue(name.getText().toString());
                VARIABLES.email.setValue(email.getText().toString());
                VARIABLES.course.setValue(course.getText().toString());
                VARIABLES.term.setValue(term.getText().toString());
                jsonParser.createJSONFile();

                if(!VARIABLES.name.getValue().isEmpty() && !VARIABLES.email.getValue().isEmpty() && !VARIABLES.course.getValue().isEmpty() && !VARIABLES.term.getValue().isEmpty() && !VARIABLES.subjects.getValue().isEmpty()){
                    jsonParser.setJSONData();
                    Intent homeIntent = new Intent(MainActivity.this,HomeActivity.class);
                    startActivity(homeIntent);
                    finish();
                }else {
                    Toast.makeText(MainActivity.this, "Fill up all details...", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}