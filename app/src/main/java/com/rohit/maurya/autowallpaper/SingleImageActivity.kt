package com.rohit.maurya.autowallpaper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import com.squareup.picasso.Picasso
import io.realm.Realm
import org.json.JSONObject

class SingleImageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_image)

        var string: String = intent.getStringExtra("json") as String
        val str = string
        var jsonObject = JSONObject(string)

        jsonObject = if (jsonObject.get("src") is JSONObject)
            jsonObject.get("src") as JSONObject
        else
            JSONObject(jsonObject.get("src").toString())

        if (jsonObject.has("src"))
            jsonObject = jsonObject.getJSONObject("src")
        Log.e("jsonIs",string)

        //jsonObject = jsonObject.get("src") as JSONObject
        string = jsonObject.getString("portrait")
        val imageView = findViewById<ImageView>(R.id.imageView)

        if (string.isNotEmpty())
            Picasso.get().load(string).placeholder(R.drawable.tenor).into(imageView)

        RealmHelper.storeDataIntoDb(str)
    }
}