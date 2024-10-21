package com.example.attendease.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.example.attendease.R;
import com.example.attendease.database.DB;
import com.example.attendease.model.Methods;
import com.example.attendease.model.Variables;

import java.util.HashMap;

public class DayDateAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context ctx;
    MutableLiveData<HashMap<String, Methods.dateParser>> dayDateHashMap;
    LifecycleOwner lifecycleOwner;

    public DayDateAdapter(Context ctx, MutableLiveData<HashMap<String, Methods.dateParser>> dayDateHashMap, LifecycleOwner lifecycleOwner) {
        this.ctx = ctx;
        this.dayDateHashMap = dayDateHashMap;
        this.lifecycleOwner = lifecycleOwner;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(ctx).inflate(R.layout.day_card,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        this.dayDateHashMap.observe(this.lifecycleOwner, new Observer<HashMap<String, Methods.dateParser>>() {
            @Override
            public void onChanged(HashMap<String, Methods.dateParser> stringdateParserHashMap) {
                String[] daysOfWeek = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
                TextView day = holder.itemView.findViewById(R.id.day);
                TextView date = holder.itemView.findViewById(R.id.date);
                day.setText(daysOfWeek[holder.getAdapterPosition()]);
                date.setText(stringdateParserHashMap.get(daysOfWeek[holder.getAdapterPosition()]).getDate()+"");

            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Variables VAR = Variables.getInstance();
                VAR.selectedDate.setValue(dayDateHashMap.getValue().get(((TextView)holder.itemView.findViewById(R.id.day)).getText()).date);
//                VAR.existingData.setValue(new DB(ctx).getData(dayDateHashMap.getValue().get(((TextView)holder.itemView.findViewById(R.id.day)).getText()).toString()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return dayDateHashMap.getValue().size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
