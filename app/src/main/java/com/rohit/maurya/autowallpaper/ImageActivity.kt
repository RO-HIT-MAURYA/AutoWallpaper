package com.rohit.maurya.autowallpaper

import android.app.WallpaperManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.github.clans.fab.FloatingActionButton
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class ImageActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var str: String
    lateinit var bM: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)

        getAndSetImage()

        handleFloatingButton()

        reloadImageIfNotLoaded()
    }

    private val handler = Handler()
    private lateinit var runnable: Runnable
    private fun reloadImageIfNotLoaded() {
        runnable = Runnable {
            handler.postDelayed(runnable, 1230)
            if (this::bM.isInitialized)
                handler.removeCallbacks(runnable)
            else
                getAndSetImage()
        }
        runnable.run()
    }

    private fun getAndSetImage() {

        val imageView: ImageView = findViewById(R.id.imageView)

        str = intent.getStringExtra("url")

        if (str.isNotEmpty()) {
            if (RealmHelper.isFavourite(str)) {
                val iV: ImageView = findViewById(R.id.iV)
                iV.setColorFilter(ContextCompat.getColor(this, R.color.colorRed))

                imageView.setImageBitmap(RealmHelper.getBitmap(str))
            } else {
                Picasso.get().load(str).placeholder(R.drawable.tenor).into(object : Target {
                    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                    }

                    override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                    }

                    override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                        Log.e("tag", "onBitmapLoaded")

                        if (bitmap != null) {
                            imageView.setImageBitmap(bitmap)
                            bM = bitmap
                        }
                    }
                })

                RealmHelper.storeDataIntoDb(str)
            }
        }
    }

    private fun handleFloatingButton() {
        val lock: FloatingActionButton = findViewById(R.id.lockScreenButton)
        lock.colorNormal = ContextCompat.getColor(this, R.color.colorPrimary)
        lock.colorPressed = ContextCompat.getColor(this, R.color.colorPrimaryDark)
        lock.setOnClickListener(this)

        val sys: FloatingActionButton = findViewById(R.id.systemScreenButton)
        lock.colorNormal = ContextCompat.getColor(this, R.color.colorPrimary)
        lock.colorPressed = ContextCompat.getColor(this, R.color.colorPrimaryDark)
        sys.setOnClickListener(this)
    }

    fun onFavClick(view: View) {
        if (!this::bM.isInitialized)
            return

        RealmHelper.updateFavourite(str)

        val imageView: ImageView = view as ImageView
        if (RealmHelper.isFavourite(str)) {
            RealmHelper.storeBase64(str, bM, object : MainActivity.Interface {
                override fun callBack() {
                    imageView.setColorFilter(resources.getColor(R.color.colorRed))
                    showToastMessage("Image saved as favourite")
                }

                override fun hideBottomView(boolean: Boolean) {
                }
            })
        } else
            imageView.setColorFilter(resources.getColor(R.color.colorPrimary))


    }

    private fun showToastMessage(s: String) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show()
    }

    fun onDlClick(view: View) {
        if (!this::bM.isInitialized)
            return

        val byteArrayOutputStream = ByteArrayOutputStream()
        bM.compress(Bitmap.CompressFormat.WEBP, 100, byteArrayOutputStream)
        val bytes: ByteArray = byteArrayOutputStream.toByteArray()
        try {
            var path = Environment.getExternalStorageDirectory()
                .toString() + "//AutoWallpaper"
            var file = File(path)
            if (!file.exists()) {
                file.mkdirs()
            }
            path = "$path//${System.currentTimeMillis().toString() + ".WEBP"}"
            file = File(path)
            if (!file.exists()) {
                file.createNewFile()
            }
            val stream = FileOutputStream(path)
            stream.write(bytes)
            stream.close()

            showToastMessage("Image downloaded successfully")
            Log.e("statusIs", "successful")
        } catch (e1: Exception) {
            e1.printStackTrace()
            Log.e("errorIs", e1.toString())
        }
    }

    override fun onClick(v: View?) {

        val byteArrayOutputStream = ByteArrayOutputStream()
        bM.compress(Bitmap.CompressFormat.WEBP, 100, byteArrayOutputStream)
        val bytes: ByteArray = byteArrayOutputStream.toByteArray()
        val b = ByteArrayInputStream(bytes)
        val wallpaperManager = WallpaperManager.getInstance(this)
        if (v?.id == R.id.lockScreenButton) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                wallpaperManager.setStream(b, null, true, WallpaperManager.FLAG_LOCK)
                showToastMessage("Lock screen wallpaper is changed successfully.")
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                wallpaperManager.setStream(b, null, true, WallpaperManager.FLAG_SYSTEM)
                showToastMessage("System screen wallpaper is changed successfully.")
            }
        }
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable)
    }
}