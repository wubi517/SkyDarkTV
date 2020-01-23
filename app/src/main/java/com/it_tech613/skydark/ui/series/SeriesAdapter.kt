package com.it_tech613.skydark.ui.series

import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.it_tech613.skydark.R
import com.it_tech613.skydark.models.SeriesModel
import kotlinx.android.synthetic.main.home_category_list_item.view.*

@Suppress("DEPRECATION")
class SeriesAdapter(val list: MutableList<out SeriesModel>, val clickListenerFunction: (Int, SeriesModel, Boolean) -> Unit) : RecyclerView.Adapter<SeriesAdapter.HomeListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.home_category_list_item,parent,false)
        return HomeListViewHolder(view)
    }

    override fun getItemViewType(position: Int): Int {
        return 1
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: HomeListViewHolder, position: Int) {
        val seriesModel = list[position]
        if (seriesModel.stream_icon!=null && seriesModel.stream_icon != "") {
            holder.itemView.image.setImageURI(Uri.parse(seriesModel.stream_icon))
            holder.itemView.title.visibility = View.GONE
            holder.itemView.name.visibility = View.VISIBLE
            holder.itemView.name.text = seriesModel.name
        }
        else{
            holder.itemView.image.setActualImageResource(R.drawable.pkg_dlg_title_bg)
            holder.itemView.title.visibility = View.VISIBLE
            holder.itemView.title.text = seriesModel.name
            holder.itemView.name.visibility = View.INVISIBLE
        }
        holder.itemView.setOnClickListener {
            clickListenerFunction(position, seriesModel, true)
        }
        holder.itemView.setOnFocusChangeListener { _, hasFocus ->
            if(hasFocus){
                holder.itemView.card.cardElevation = 10f
                holder.itemView.card.setCardBackgroundColor(Color.parseColor("#FFD600"))
                holder.itemView.scaleX = 1f
                holder.itemView.scaleY = 1f
                holder.itemView.name.setTextColor(Color.parseColor("#212121"))
                holder.itemView.title.setTextColor(Color.parseColor("#eeeeee"))
                clickListenerFunction(position, seriesModel, false)
            }else{
                holder.itemView.card.cardElevation = 1f
                holder.itemView.card.setCardBackgroundColor(Color.parseColor("#25ffffff"))
                holder.itemView.scaleX = 0.95f
                holder.itemView.scaleY = 0.95f
                holder.itemView.name.setTextColor(Color.parseColor("#eeeeee"))
                holder.itemView.title.setTextColor(Color.parseColor("#eeeeee"))
            }
        }

    }

    class HomeListViewHolder(view: View):RecyclerView.ViewHolder(view)
}