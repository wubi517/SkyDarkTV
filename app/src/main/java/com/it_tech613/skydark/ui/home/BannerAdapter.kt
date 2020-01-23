package com.it_tech613.skydark.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.it_tech613.skydark.R
import kotlinx.android.synthetic.main.banner_item.view.*

class BannerAdapter(val list:ArrayList<String>) : RecyclerView.Adapter<BannerAdapter.BannerViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.banner_item,parent,false)
        return BannerViewHolder(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        holder.itemView.image.setImageURI(list[position])
    }

    class BannerViewHolder(view: View) : RecyclerView.ViewHolder(view)
}