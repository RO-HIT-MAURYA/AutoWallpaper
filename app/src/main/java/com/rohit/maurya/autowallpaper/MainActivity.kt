package com.rohit.maurya.autowallpaper

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.realm.Realm
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity() {

    private var baseUrl = "https://api.pexels.com/v1/search?query="
    private lateinit var textView: TextView
    private val homeAdapter = RecyclerViewAdapter(this, object : Interface {
        override fun callBack() {
            hitApi()
        }

        override fun hideBottomView(boolean: Boolean) {
            handleHideBottomView(boolean)
        }
    })

    private val recentAdapter = RecyclerViewAdapter(this, object : Interface {
        override fun callBack() {
        }

        override fun hideBottomView(boolean: Boolean) {
            handleHideBottomView(boolean)
        }
    })

    private val favouriteAdapter = RecyclerViewAdapter(this, object : Interface {
        override fun callBack() {
        }

        override fun hideBottomView(boolean: Boolean) {
            handleHideBottomView(boolean)
        }
    })

    private lateinit var keyword: String
    private var tempString = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Realm.init(this)

        textView = findViewById(R.id.textView)

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

    fun hitApi() {
        val queue = Volley.newRequestQueue(this)
        val url = "$baseUrl$keyword&page=$pageNo"
        Log.e("urlIs", url)

        if (tempString.contentEquals(url) || loadMore.not())
            return
        else
            tempString = url

        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.GET, url, JSONObject(),
            Response.Listener
            { response ->
                if (response is JSONObject) {
                    Log.e("responseIs", response.toString())
                    val jsonArray = response.getJSONArray("photos")
                    if (jsonArray.length() == 0) {
                        textView.visibility = View.VISIBLE
                        textView.text = "No result found"
                    } else
                        textView.visibility = View.GONE


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
        var jsonObject: JSONObject

        for (i in 0..jsonArray.length() - 1) {
            jsonObject = jsonArray[i] as JSONObject
            jsonObject = jsonObject.get("src") as JSONObject
            val string = jsonObject.getString("portrait")
            homeList.add(string)
            if (i == jsonArray.length() - 1)
                homeAdapter.notifyDataSetChanged()
        }
    }

    private fun handleBottomNavigation() {
        findViewById<BottomNavigationView>(R.id.bottomNavigationView).setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeItem -> {
                    findViewById<RecyclerView>(R.id.homeRecyclerView).visibility = View.VISIBLE
                    findViewById<FloatingActionButton>(R.id.floatingActionButton).visibility = View.VISIBLE
                    findViewById<RecyclerView>(R.id.recentRecyclerView).visibility = View.GONE
                    findViewById<RecyclerView>(R.id.favouriteRecyclerView).visibility = View.GONE
                    v = 0

                    if (homeList.size == 0) {
                        textView.visibility = View.VISIBLE
                        textView.text =
                            "Please check your internet.\n No worry you can still view your saved images."
                    } else
                        textView.visibility = View.GONE


                    true
                }
                R.id.recentItem -> {
                    findViewById<RecyclerView>(R.id.homeRecyclerView).visibility = View.GONE
                    findViewById<RecyclerView>(R.id.recentRecyclerView).visibility = View.VISIBLE
                    findViewById<RecyclerView>(R.id.favouriteRecyclerView).visibility = View.GONE
                    findViewById<FloatingActionButton>(R.id.floatingActionButton).visibility = View.GONE
                    v = 1

                    recentList.clear()
                    recentList = RealmHelper.getRecent()
                    if (recentList.size == 0) {
                        textView.visibility = View.VISIBLE
                        textView.text = "Nothing is view yet"
                    } else
                        textView.visibility = View.GONE

                    recentAdapter.notifyDataSetChanged()

                    Log.e("arrayIs", RealmHelper.getRecent().toString())
                    true
                }
                else -> {
                    findViewById<RecyclerView>(R.id.homeRecyclerView).visibility = View.GONE
                    findViewById<RecyclerView>(R.id.recentRecyclerView).visibility = View.GONE
                    findViewById<RecyclerView>(R.id.favouriteRecyclerView).visibility = View.VISIBLE
                    findViewById<FloatingActionButton>(R.id.floatingActionButton).visibility = View.GONE
                    v = 2

                    favouriteList.clear()
                    favouriteList = RealmHelper.getFavourite()
                    if (favouriteList.size == 0) {
                        textView.visibility = View.VISIBLE
                        textView.text = "Nothing is added to favourite"
                    } else
                        textView.visibility = View.GONE

                    favouriteAdapter.notifyDataSetChanged()

                    true
                }
            }
        }
    }

    private fun handleHideBottomView(boolean: Boolean) {
        if (boolean) {
            findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility = View.GONE
            findViewById<FloatingActionButton>(R.id.floatingActionButton).visibility = View.GONE
        } else {
            findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility =
                View.VISIBLE
            findViewById<FloatingActionButton>(R.id.floatingActionButton).visibility =
                View.VISIBLE
        }
    }

    companion object {
        var v = 0
        var pageNo = 0
        var homeList = ArrayList<String>()
        var recentList = ArrayList<String>()
        var favouriteList = ArrayList<String>()
        var loadMore: Boolean = true

        var imgUrl = ""
        var position = 0
    }

    interface Interface {
        fun callBack()
        fun hideBottomView(boolean: Boolean)
    }

    fun onMicClick(view: View) {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to Image Type")
        startActivityForResult(intent, 31)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (data != null) {
                val arrayList =
                    data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                if (arrayList != null && arrayList.size > 0) {
                    pageNo = 0
                    keyword = arrayList[0]
                    loadMore = true
                    homeList.clear()
                    homeAdapter.notifyDataSetChanged()
                    hitApi()
                }
            }
        }
    }
}