package com.rohit.maurya.autowallpaper

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import java.lang.Exception

class ImageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)

        val iV: ImageView = findViewById(R.id.imageView)

        val str: String = intent.getStringExtra("url")

        Picasso.get().load(str).placeholder(R.drawable.tenor).into(object : Target {
            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
            }

            override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
            }

            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                Log.e("tag","onBitmapLoaded")

                if (bitmap != null)
                    iV.setImageBitmap(bitmap)
            }
        })
//        Picasso.get().load(str).placeholder(R.drawable.tenor).into(iV)

        RealmHelper.storeDataIntoDb(str)
    }
}