package com.example.androidapplicationproject.database

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.androidapplicationproject.R
import com.example.androidapplicationproject.Util.NewsData
import com.example.androidapplicationproject.databinding.NewsItemBinding
import com.example.androidapplicationproject.Util.Utils
import com.google.android.gms.maps.model.LatLng
import com.squareup.picasso.Picasso
import net.cachapa.expandablelayout.ExpandableLayout
import org.w3c.dom.Text


class NewsListAdapter(val newsData: MutableList<NewsData>)
    :  RecyclerView.Adapter<NewsListAdapter.NewsViewHolder>(){
//    private lateinit var newsData: MutableList<NewsData>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        return NewsViewHolder( LayoutInflater.from(parent.context).inflate(R.layout.news_item, parent, false))
    }
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        //set values
        val newsdata = newsData[position]
        holder.title.text = newsdata.title
        holder.desc.text = newsdata.description
        holder.url = newsdata.url
        Picasso.get().load(newsdata.urlToImage).into(holder.imageView)
    }

    inner class NewsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var title: TextView =view.findViewById(R.id.news_title)
        var desc: TextView =view.findViewById(R.id.news_desc)
        var imageView: ImageView = view.findViewById(R.id.news_image)
        var llnews: LinearLayout =view.findViewById(R.id.llnews)
        lateinit var url: String
    }

    override fun getItemCount(): Int {
        return newsData.size
    }
}