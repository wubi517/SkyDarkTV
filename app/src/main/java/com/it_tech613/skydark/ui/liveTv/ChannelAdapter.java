package com.it_tech613.skydark.ui.liveTv;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.it_tech613.skydark.R;
import com.it_tech613.skydark.apps.Constants;
import com.it_tech613.skydark.apps.MyApp;
import com.it_tech613.skydark.models.EPGChannel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;

public class ChannelAdapter extends RecyclerView.Adapter<ChannelAdapter.CategoryViewHolder> {
    private List<EPGChannel> list;
    private int selected = 0;
    private int focus = 0;
    private Function2<EPGChannel,Integer, Unit> channelModelIntegerUnitFunction;
    private Function2<EPGChannel, Integer, Unit> onFocusListener;
    ChannelAdapter(List<EPGChannel> list, Function2<EPGChannel, Integer, Unit> channelModelIntegerUnitFunction, Function2<EPGChannel, Integer, Unit> onFocusListener) {
        this.list = list;
//        sortList(list);
        this.channelModelIntegerUnitFunction = channelModelIntegerUnitFunction;
        this.onFocusListener= onFocusListener;
    }

    public void setList(List<EPGChannel> list){
        this.list = list;
//        sortList(list);
        notifyDataSetChanged();
    }

    private void sortList(List<EPGChannel> epgChannels){
        list = new ArrayList<>(epgChannels);
        int sorting_id;
        if (MyApp.instance.getPreference().get(Constants.getCurrentSorting())!=null)
            sorting_id = (int) MyApp.instance.getPreference().get(Constants.getCurrentSorting());
        else sorting_id = 0;
        switch (sorting_id){
            case 0://last added
                Collections.sort(list, (o1, o2) -> o2.getAdded().compareTo(o1.getAdded()));
                break;
            case 1://top to bottom
                break;
            case 2://bottom to top
                Collections.reverse(list);
                break;
        }
    }

    void setSelected(int selected){
        this.selected = selected;
        notifyDataSetChanged();
    }

    void setFocus(int focus){
        this.focus = focus;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CategoryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item,parent,false));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onBindViewHolder(@NonNull final CategoryViewHolder holder, final int position) {
        holder.name.setText(list.get(position).getName());
        holder.itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    focus=position;
                    holder.card.setCardElevation(8f);
                    holder.itemView.setBackgroundColor(Color.parseColor("#2962FF"));
                    onFocusListener.invoke(list.get(position),position);
                }else{
                    holder.card.setCardElevation(0f);
                    holder.itemView.setBackgroundColor(Color.parseColor("#000096a6"));
                }
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                channelModelIntegerUnitFunction.invoke(list.get(position),position);
                if(selected!=position){
                    //clickeListenerFunction(list[position])
                    int previouslySelected = selected;
                    selected = position;
                    notifyItemChanged(previouslySelected);
                    notifyItemChanged(selected);
                }

            }
        });

        if(selected==position || focus == position) {
            holder.itemView.setBackgroundColor(Color.parseColor("#602962FF"));
            if (selected==position)
                holder.image_play.setVisibility(View.VISIBLE);
            holder.itemView.requestFocus();
        }
        else {
            holder.itemView.setBackgroundColor(Color.parseColor("#002962FF"));
            holder.image_play.setVisibility(View.GONE);
        }

    }

    class CategoryViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        CardView card;
        ImageView image_play;
        CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            card = itemView.findViewById(R.id.card);
            image_play = itemView.findViewById(R.id.image_play);
        }
    }
}