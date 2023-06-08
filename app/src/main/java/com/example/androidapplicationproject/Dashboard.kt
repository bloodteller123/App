package com.example.androidapplicationproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.androidapplicationproject.Util.NewsData
import com.example.androidapplicationproject.database.NewsListAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.json.JSONArray
import org.json.JSONException

class Dashboard : AppCompatActivity() {

    private lateinit var myProperties: CardView
    private lateinit var allProperties: CardView

    private lateinit var recyclerView: RecyclerView
    private lateinit var newsList: MutableList<NewsData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        myProperties = findViewById(R.id.view_tenant)
        allProperties = findViewById(R.id.view_property)

        newsList = mutableListOf()
        prepareNewsData()
        Log.d("list", newsList.toString())
        recyclerView = findViewById(R.id.news)


        val actionbar = supportActionBar
        actionbar?.setDisplayHomeAsUpEnabled(true)
        actionbar?.setHomeButtonEnabled(true)

        val menus = findViewById<BottomNavigationView>(R.id.navigation_menu)
        menus.setOnItemSelectedListener { item->
            val id = item.itemId
            when (id) {
                R.id.action_home -> {
                    true
                }
                R.id.action_add ->{
                    myProperties.performClick()
                    true
                }
                R.id.action_property ->{
                    allProperties.performClick()
                    true
                }
                R.id.action_profile ->{
                    val intent = Intent(this, Profile::class.java)
                    startActivity(intent)
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }
        }
        myProperties.setOnClickListener{
            Log.d("Dashboard", "myProperties")
            intent = Intent(this, Listing::class.java)
            intent.putExtra("isTenant", false)
            startActivity(intent)
        }

        allProperties.setOnClickListener{
            Log.d("Dashboard", "all Landlords")
            intent = Intent(this, LandlordList::class.java)
//            intent.putExtra("isTenant", true)
            startActivity(intent)
        }
    }

    private fun prepareNewsData(){
        var url = "https://newsapi.org/v2/top-headlines?country=us&apiKey=d4cf5609afb14201bda82c5f7ba784f2"
        var queues = Volley.newRequestQueue(this)
        var request = object: JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                var jsonArray: JSONArray
                try {
                    jsonArray = response.getJSONArray("articles")
                    var data: NewsData
                    for(i in 0 until jsonArray.length()){
                        var obj = jsonArray.getJSONObject(i)
                        data = NewsData(
                            obj.get("title").toString(),
                            obj.get("description").toString(),
                            obj.get("url").toString(),
                            obj.get("urlToImage").toString(),
//                            image = image
                        )
                        newsList.add(data)
                    }
                    val newsAdapter = NewsListAdapter(newsList)
                    val llmanagrt = LinearLayoutManager(this)
                    recyclerView.layoutManager = llmanagrt
                    recyclerView.adapter = newsAdapter
                }catch (e: JSONException){
                    e.printStackTrace()
                }
            },
            { error ->
                error.printStackTrace()
                Toast.makeText(this, "Error",Toast.LENGTH_SHORT).show()
            }
        ){
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["User-Agent"] = "Mozilla/5.0"
                return headers
            }
        }
        queues.add(request)
    }
}