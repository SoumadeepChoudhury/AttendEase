package com.example.attendease.adapter;

import com.example.attendease.R;
import com.example.attendease.model.ClassOverview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SubjectWiseClassAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context ctx;
    MutableLiveData<ArrayList<ClassOverview>> subectWiseClassArrayList;
    LifecycleOwner lifecycleOwner;

    public SubjectWiseClassAdapter(Context ctx, MutableLiveData<ArrayList<ClassOverview>> subectWiseClassArrayList,LifecycleOwner lifecycleOwner) {
        this.ctx = ctx;
        this.subectWiseClassArrayList = subectWiseClassArrayList;
        this.lifecycleOwner = lifecycleOwner;
    }

    @Override
    public int getItemCount() {
        return this.subectWiseClassArrayList.getValue().size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(this.ctx).inflate(R.layout.class_overview_card,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        this.subectWiseClassArrayList.observe(this.lifecycleOwner, new Observer<ArrayList<ClassOverview>>() {
            @Override
            public void onChanged(ArrayList<ClassOverview> classOverviews) {
                ClassOverview item = subectWiseClassArrayList.getValue().get(holder.getAdapterPosition());

                TextView subject = holder.itemView.findViewById(R.id.subject);
                TextView totalClassesConducted = holder.itemView.findViewById(R.id.totalClassesConducted);
                TextView totalClassesAttended = holder.itemView.findViewById(R.id.totalClassesAttended);
                TextView percentAttendance = holder.itemView.findViewById(R.id.percentAttendance);

                subject.setText(item.getSubject());
                totalClassesConducted.setText(item.getTotalClassesConducted()+"");
                totalClassesAttended.setText(item.getTotalClassesAttended()+"");
                percentAttendance.setText(Math.round(item.getPercentage())+"%");
            }
        });

    }



    public static class ViewHolder extends RecyclerView.ViewHolder{
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

}
