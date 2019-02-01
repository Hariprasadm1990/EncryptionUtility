package com.haripm.encryptionkotlinutility.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.haripm.encryptionkotlinutility.R
import com.haripm.encryptionkotlinutility.model.AvengersModel
import com.squareup.picasso.Picasso

class AvengersAdapter(context: Context, heroList: List<AvengersModel>) : RecyclerView.Adapter<AvengersAdapter.HeroViewHolder>() {

    private var mContext: Context
    private var mAvengersList: List<AvengersModel>
    private var mLayoutInflater: LayoutInflater

     init {
        this.mContext = context
        this.mAvengersList = heroList
        mLayoutInflater = LayoutInflater.from(mContext)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeroViewHolder {
        val view: View = mLayoutInflater.inflate(R.layout.recyclerview_layout, parent, false)
        return HeroViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mAvengersList.size
    }

    override fun onBindViewHolder(holder: HeroViewHolder, position: Int) {
        val item: AvengersModel = mAvengersList[position]
      //  Glide.with(mContext.applicationContext).asBitmap().load(item.imageUrl).into(holder.hero_image)
        Picasso.with(mContext.applicationContext).load(item.imageurl)
                .placeholder(R.drawable.ic_launcher_background).into(holder.hero_image);
        holder.hero_text.setText(item.name)
    }


    class HeroViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val hero_image: ImageView = itemView.findViewById(R.id.hero_image)
        val hero_text: TextView = itemView.findViewById(R.id.hero_text);
    }
}