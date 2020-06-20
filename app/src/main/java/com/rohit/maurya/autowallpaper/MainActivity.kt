package com.rohit.maurya.autowallpaper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity() {

    private var baseUrl = "https://api.pexels.com/v1/search?query="
    private val homeAdapter = RecyclerViewAdapter(this, object : Interface {
        override fun callBack() {
            hitApi()
        }

        override fun hideBottomView(boolean: Boolean) {
            if (boolean)
                findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility = View.GONE
            else
                findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility =
                    View.VISIBLE

        }
    })
    private val recentAdapter = RecyclerViewAdapter(this, object : Interface {
        override fun callBack() {
        }

        override fun hideBottomView(boolean: Boolean) {
        }
    })
    private val favouriteAdapter = RecyclerViewAdapter(this, object : Interface {
        override fun callBack() {
        }

        override fun hideBottomView(boolean: Boolean) {
        }
    })
    private lateinit var keyword: String
    private var tempString = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        decideDayWiseKeyword()
        handleBottomNavigation()
        setUpRecyclerView()
        hitApi()
    }

    private fun decideDayWiseKeyword() {
        keyword = when (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
            1 -> "trending"
            2 -> "business"
            3 -> "entertainment"
            4 -> "health"
            5 -> "science"
            6 -> "sports"
            else -> "technology"
        }
    }

    private fun setUpRecyclerView() {
        val homeRecyclerView = findViewById<RecyclerView>(R.id.homeRecyclerView)
        homeRecyclerView.adapter = homeAdapter

        val recentRecyclerView = findViewById<RecyclerView>(R.id.recentRecyclerView)
        recentRecyclerView.adapter = recentAdapter

        val favouriteRecyclerView = findViewById<RecyclerView>(R.id.favouriteRecyclerView)
        favouriteRecyclerView.adapter = favouriteAdapter
    }

    public fun hitApi() {
        val queue = Volley.newRequestQueue(this)
        val url = "$baseUrl$keyword&page=$pageNo"
        //Log.e("urlIs", url)

        if (tempString.contentEquals(url) || loadMore.not())
            return
        else
            tempString = url

        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.GET, url, JSONObject(),
            Response.Listener
            { response ->
                if (response is JSONObject) {
                    val jsonArray = response.getJSONArray("photos")
                    loadMore = jsonArray.length() != 0

                    updateLists(jsonArray)
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

    private fun updateLists(jsonArray: JSONArray) {
        for (i in 0..jsonArray.length() - 1) {
            when (v) {
                0 -> {
                    homeList.add(jsonArray[i] as JSONObject)
                    if (i == jsonArray.length() - 1)
                        homeAdapter.notifyDataSetChanged()
                }
                1 -> {
                    recentList.add(jsonArray[i] as JSONObject)
                    if (i == jsonArray.length() - 1)
                        recentAdapter.notifyDataSetChanged()
                }
                else -> {
                    favouriteList.add(jsonArray[i] as JSONObject)
                    if (i == jsonArray.length() - 1)
                        favouriteAdapter.notifyDataSetChanged()
                }
            }
        }
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
        var pageNo = 0
        var homeList = ArrayList<JSONObject>()
        var recentList = ArrayList<JSONObject>()
        var favouriteList = ArrayList<JSONObject>()
        var loadMore: Boolean = true
    }

    interface Interface {
        fun callBack()
        fun hideBottomView(boolean: Boolean)
    }
}