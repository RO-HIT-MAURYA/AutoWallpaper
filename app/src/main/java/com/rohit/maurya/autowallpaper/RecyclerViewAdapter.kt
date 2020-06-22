package com.rohit.maurya.autowallpaper

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.image_item.view.*
import org.json.JSONObject

class RecyclerViewAdapter(private val context: Context, private val iFace: MainActivity.Interface) :
    RecyclerView.Adapter<RecyclerViewAdapter.InnerClass>() {

    private var temp = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InnerClass {
        return InnerClass(LayoutInflater.from(context).inflate(R.layout.image_item, parent, false))
    }

    override fun getItemCount(): Int {
        return when (MainActivity.v) {
            0 -> MainActivity.homeList.size
            1 -> MainActivity.recentList.size
            else -> MainActivity.favouriteList.size
        }
    }

    override fun onBindViewHolder(holder: InnerClass, position: Int) {

        Log.e("positionIs", position.toString())
        if (position == itemCount - 1 && MainActivity.v == 0 && MainActivity.loadMore) {
            MainActivity.pageNo = MainActivity.pageNo + 1
            iFace.callBack()
        }

        when {
            position < 10 -> iFace.hideBottomView(false)
            temp > position -> iFace.hideBottomView(false)
            else -> iFace.hideBottomView(true)
        }

        temp = position

        var jsonObject = when (MainActivity.v) {
            0 -> MainActivity.homeList[position]
            1 -> MainActivity.recentList[position]
            else -> MainActivity.favouriteList[position]
        }

        jsonObject = jsonObject.get("src") as JSONObject
        val string = jsonObject.getString("portrait")
        if (string.isNotEmpty()) {
            Picasso.get().load(string).placeholder(R.drawable.tenor).into(holder.imageView)
            holder.imageView.tag = string
            holder.imageView.setOnClickListener {
                var string : String = it.tag as String
                val intent : Intent = Intent(context,ImageActivity::class.java)
                intent.putExtra("url",string)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                context.startActivity(intent)
            }
        }
    }

    class InnerClass(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView = itemView.imageView!!
    }
}