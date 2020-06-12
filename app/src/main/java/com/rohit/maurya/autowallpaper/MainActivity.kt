package com.rohit.maurya.autowallpaper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private var baseUrl = "https://api.pexels.com/v1/search?query="
    private val recyclerViewAdapter = RecyclerViewAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        handleBottomNavigation()
        setUpRecyclerView()
        hitApiForTrending()
    }

    private fun setUpRecyclerView() {
        val homeRecyclerView = findViewById<RecyclerView>(R.id.homeRecyclerView)
        homeRecyclerView.adapter = recyclerViewAdapter
    }

    private fun hitApiForTrending() {
        val queue = Volley.newRequestQueue(this)
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.GET, baseUrl + "trending", JSONObject(),
            Response.Listener
            { response ->
                if (response is JSONObject)
                {
                    val jsonObject = response as JSONObject
                    homeArray = jsonObject.getJSONArray("photos")
                    recyclerViewAdapter.notifyDataSetChanged()
                }

            }, Response.ErrorListener
            { error ->
                Log.e("errorIs", error.toString())
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json"
                headers["Authorization"] =
                    "563492ad6f91700001000001af4739256e91471aaa55014e76f3b3f3"
                return headers
            }
        }
        queue.add(jsonObjectRequest)
    }

    private fun handleBottomNavigation() {
        findViewById<BottomNavigationView>(R.id.bottomNavigationView).setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeItem -> {
                    findViewById<RecyclerView>(R.id.homeRecyclerView).visibility = View.VISIBLE
                    findViewById<RecyclerView>(R.id.recentRecyclerView).visibility = View.GONE
                    findViewById<RecyclerView>(R.id.favouriteRecyclerView).visibility = View.GONE
                    v = 0
                    true
                }
                R.id.recentItem -> {
                    findViewById<RecyclerView>(R.id.homeRecyclerView).visibility = View.GONE
                    findViewById<RecyclerView>(R.id.recentRecyclerView).visibility = View.VISIBLE
                    findViewById<RecyclerView>(R.id.favouriteRecyclerView).visibility = View.GONE
                    v = 1
                    true
                }
                else -> {
                    findViewById<RecyclerView>(R.id.homeRecyclerView).visibility = View.GONE
                    findViewById<RecyclerView>(R.id.recentRecyclerView).visibility = View.GONE
                    findViewById<RecyclerView>(R.id.favouriteRecyclerView).visibility = View.VISIBLE
                    v = 2
                    true
                }
            }
        }
    }

    companion object {
        var v = 0
        var homeArray = JSONArray()
        var recentArray = JSONArray()
        var favouriteArray = JSONArray()
    }
}