package com.it_tech613.skydark.ui.series;

import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.it_tech613.skydark.R;
import com.it_tech613.skydark.models.SeasonModel;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

import java.util.List;

public class SeasonListAdapter extends RecyclerView.Adapter<ViewHolder> {

    private List<SeasonModel> seasonModels;
    private Function2<Integer,SeasonModel, Unit> onClickListener;
    private Function2<Integer,SeasonModel, Unit> onFocusListener;
    private int selected_i=0;
    private boolean isVerticalMode;
    SeasonListAdapter(List<SeasonModel> videoItems,boolean isVerticalMode , Function2<Integer,SeasonModel, Unit> onClickListener, Function2<Integer,SeasonModel, Unit> onFocusListener) {
        seasonModels = videoItems;
        this.onClickListener = onClickListener;
        this.onFocusListener = onFocusListener;
        this.isVerticalMode = isVerticalMode;
    }

    void setSeasonModels(List<SeasonModel> seasonModels){
        this.seasonModels = seasonModels;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (isVerticalMode) return new TitleViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cat_list,parent,false));
        else return new VideoViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.video_list_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final SeasonModel seasonModel = seasonModels.get(position);
        if (isVerticalMode){
            TitleViewHolder titleViewHolder = (TitleViewHolder)holder;
            titleViewHolder.itemView.setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus){
//                    holder.itemView.setBackgroundColor(Color.parseColor("#2962FF"));
                    onFocusListener.invoke(position,seasonModel);
                    selected_i = position;
                }else{
//                    holder.itemView.setBackgroundColor(Color.parseColor("#000096a6"));
                }
            });
            titleViewHolder.itemView.setOnClickListener(v -> onClickListener.invoke(position,seasonModel));
            titleViewHolder.title.setText(seasonModel.getName());
        }else {
            VideoViewHolder videoViewHolder = (VideoViewHolder)holder;
            videoViewHolder.image.setImageURI(Uri.parse(seasonModel.getIcon()));
            videoViewHolder.itemView.setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus){
                    holder.itemView.setBackgroundColor(Color.parseColor("#2962FF"));
                    onFocusListener.invoke(position,seasonModel);
                    selected_i = position;
                }else{
                    holder.itemView.setBackgroundColor(Color.parseColor("#000096a6"));
                }
            });
            videoViewHolder.itemView.setOnClickListener(v -> onClickListener.invoke(position,seasonModel));
            videoViewHolder.title.setText(seasonModel.getName());
            videoViewHolder.description.setText(seasonModel.getOverview());
            videoViewHolder.duration.setText(seasonModel.getAir_date());
            if(position==selected_i) holder.itemView.requestFocus();
        }
    }

    @Override
    public int getItemCount() {
        return seasonModels.size();
    }

    class VideoViewHolder extends ViewHolder{
        SimpleDraweeView image;
        TextView title, duration, description;
        VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.textView);
            description = itemView.findViewById(R.id.textView2);
            duration = itemView.findViewById(R.id.textView3);
        }
    }

    class TitleViewHolder extends ViewHolder{
        TextView title;
        TitleViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.cat_item_txt);
        }
    }
}