package com.rohit.maurya.autowallpaper

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.image_item.view.*
import org.json.JSONObject

class RecyclerViewAdapter(private val context: Context) :
    RecyclerView.Adapter<RecyclerViewAdapter.InnerClass>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InnerClass {
        return InnerClass(LayoutInflater.from(context).inflate(R.layout.image_item, parent, false))
    }

    override fun getItemCount(): Int {
        return when (MainActivity.v) {
            0 -> MainActivity.homeArray.length()
            1 -> MainActivity.recentArray.length()
            else -> MainActivity.favouriteArray.length()
        }
    }

    override fun onBindViewHolder(holder: InnerClass, position: Int) {
        var jsonObject = when (MainActivity.v) {
            0 -> MainActivity.homeArray[position] as JSONObject
            1 -> MainActivity.recentArray[position] as JSONObject
            else -> MainActivity.favouriteArray[position] as JSONObject
        }

        jsonObject = jsonObject.get("src") as JSONObject
        val string = jsonObject.getString("portrait")
        if (string.isNotEmpty())
            Picasso.get().load(string).placeholder(R.drawable.tenor).into(holder.imageView)
    }

    class InnerClass(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView = itemView.imageView
    }
}