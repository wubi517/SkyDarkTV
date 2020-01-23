package com.it_tech613.skydark.ui.liveTv;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.it_tech613.skydark.R;
import com.it_tech613.skydark.apps.Constants;
import com.it_tech613.skydark.models.EPGEvent;

import java.util.Date;
import java.util.List;

public class LiveTvProgramsAdapter extends RecyclerView.Adapter<LiveTvProgramsAdapter.CategoryViewHolder> {

    private List<EPGEvent> epgModels;
    @SuppressLint("SimpleDateFormat")
    LiveTvProgramsAdapter(List<EPGEvent> epgModels){
        this.epgModels=epgModels;
    }
    void setEpgModels(List<EPGEvent> epgModels){
        this.epgModels=epgModels;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CategoryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.livetv_program_item,parent,false));
    }

    @Override
    public int getItemCount() {
        return epgModels.size();
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        if(position==0) {
            holder.name.setTextColor(Color.parseColor("#ffaa3f"));
            holder.time.setTextColor(Color.parseColor("#ffaa3f"));
        }else {
            holder.name.setTextColor(Color.parseColor("#ffffff"));
            holder.time.setTextColor(Color.parseColor("#ffffff"));
        }
        EPGEvent epgEvent=epgModels.get(position);
        holder.name.setText(new String (Base64.decode(epgEvent.getTitle(), Base64.DEFAULT)));
        Date that_date = new Date();
        that_date.setTime(epgEvent.getStartTime().getTime()+ Constants.SEVER_OFFSET);
        holder.time.setText(Constants.clockFormat.format(that_date));
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder{
        TextView name,time;
        CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            time = itemView.findViewById(R.id.time);
        }
    }
}