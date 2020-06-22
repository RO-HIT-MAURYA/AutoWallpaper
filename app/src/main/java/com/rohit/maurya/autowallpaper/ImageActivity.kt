package com.rohit.maurya.autowallpaper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.squareup.picasso.Picasso

class ImageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)

        val iV : ImageView = findViewById(R.id.imageView)

        val str : String = intent.getStringExtra("url")

        Picasso.get().load(str).placeholder(R.drawable.tenor).into(iV)
    }
}