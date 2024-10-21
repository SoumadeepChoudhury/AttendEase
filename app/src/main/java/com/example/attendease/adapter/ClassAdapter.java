package com.example.attendease.adapter;

import com.example.attendease.database.DB;
import com.example.attendease.model.Class;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.example.attendease.R;
import com.example.attendease.model.ClassOverview;
import com.example.attendease.model.Variables;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.Objects;

public class ClassAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context ctx;
    MutableLiveData<ArrayList<Class>> classesList;
    LifecycleOwner lifecycleOwner;

    public ClassAdapter(Context ctx,MutableLiveData<ArrayList<Class>> classesList,LifecycleOwner lifecycleOwner) {
        this.classesList = classesList;
        this.ctx = ctx;
        this.lifecycleOwner = lifecycleOwner;
    }



    @Override
    public int getItemCount() {
        return classesList.getValue().size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(ctx).inflate(R.layout.class_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Class item = classesList.getValue().get(holder.getAdapterPosition());
        Variables VARIABLES = Variables.getInstance();

        if (!item.getSubject().isEmpty()) {
            TextView subjectName = holder.itemView.findViewById(R.id.subjectName);
            TextView classStatus = holder.itemView.findViewById(R.id.classStatus);
            subjectName.setText(item.getSubject());
            classStatus.setText(item.getStatus());
        } else {
            ConstraintLayout finalCard = holder.itemView.findViewById(R.id.finalCard);
            ConstraintLayout itemToBeAddedCard = holder.itemView.findViewById(R.id.itemToBeAddedCard);
            finalCard.setVisibility(View.GONE);
            itemToBeAddedCard.setVisibility(View.VISIBLE);
            ImageView deleteClassCard = holder.itemView.findViewById(R.id.deleteClassCard);
            ChipGroup subjectChipGroup = holder.itemView.findViewById(R.id.subjectChipGroup);
            TextView statusPresent = holder.itemView.findViewById(R.id.statusPresent);
            TextView statusAbsent = holder.itemView.findViewById(R.id.statusAbsent);

            subjectChipGroup.removeAllViews();
            for (int i = 0; i < VARIABLES.subjects.getValue().size(); i++) {
                FrameLayout chip = (FrameLayout) LayoutInflater.from(ctx).inflate(R.layout.chip_layout, null);
                Chip subjectChip = chip.findViewById(R.id.subject);
                LinearLayout subjectEditLayout = chip.findViewById(R.id.subjectEditLayout);

                subjectChip.setText(VARIABLES.subjects.getValue().get(i));
                subjectChip.setClickable(true);
                subjectChip.setCheckable(true);
                subjectChip.setVisibility(View.VISIBLE);
                subjectEditLayout.setVisibility(View.GONE);


                subjectChip.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        for (int i = 0; i < subjectChipGroup.getChildCount(); i++) {
                            Chip eachChip = subjectChipGroup.getChildAt(i).findViewById(R.id.subject);
                            eachChip.setChecked(false);
                        }
                        subjectChip.setChecked(true);
                        item.setSubject(subjectChip.getText().toString());
                    }
                });


                subjectChipGroup.addView(chip);
            }

            deleteClassCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finalCard.setVisibility(View.VISIBLE);
                    itemToBeAddedCard.setVisibility(View.GONE);
                    ArrayList<Class> classes = classesList.getValue();
                    classes.remove(holder.getAdapterPosition());
                    classesList.setValue(classes);
                    notifyItemRemoved(holder.getAdapterPosition());
                    VARIABLES.isLocked.setValue(false);
                }
            });

            statusPresent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    item.setStatus("Present");
                    changeCardState(holder, item);
                }
            });

            statusAbsent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    item.setStatus("Absent");
                    changeCardState(holder, item);
                }
            });

        }

    }

    public void changeCardState(RecyclerView.ViewHolder holder,Class item){
        if(!item.getSubject().isEmpty() && !item.getStatus().isEmpty()) {
            Variables VAR = Variables.getInstance();
            ConstraintLayout finalCard = holder.itemView.findViewById(R.id.finalCard);
            ConstraintLayout itemToBeAddedCard = holder.itemView.findViewById(R.id.itemToBeAddedCard);
            finalCard.setVisibility(View.VISIBLE);
            itemToBeAddedCard.setVisibility(View.GONE);

            TextView subjectName = holder.itemView.findViewById(R.id.subjectName);
            TextView classStatus = holder.itemView.findViewById(R.id.classStatus);
            subjectName.setText(item.getSubject());
            classStatus.setText(item.getStatus());
            VAR.totalClassOfDate.setValue(getItemCount());
            VAR.isLocked.setValue(false);
            ClassOverview classItem = new DB(ctx).insertData(item.getDate(),item.getSubject(),item.getStatus());
            ArrayList<ClassOverview> classList = VAR.subjectWiseClassOverview.getValue();
            for(int i=0;i<classList.size();i++){
                if(Objects.equals(classList.get(i).getSubject(), item.getSubject())){
                    classList.set(i,classItem);
                    break;
                }
            }
            VAR.subjectWiseClassOverview.setValue(classList);
            VAR.totalClasses.setValue(VAR.totalClasses.getValue()+1);
            VAR.totalClassesAttended.setValue(VAR.totalClassesAttended.getValue()+ (Objects.equals(item.getStatus(),"Present")?1:0));
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
