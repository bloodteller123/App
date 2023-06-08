package com.example.androidapplicationproject

//import android.support.v7.widget.Toolbar

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidapplicationproject.Util.Utils
import com.example.androidapplicationproject.database.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.*
import okhttp3.*
import java.util.*


class Listing : AppCompatActivity() {

    private lateinit var locations: MutableList<Pair<Long,Long>>
    private var addresses: MutableList<PropertyTable>? = null
    lateinit var scope: CoroutineScope
    //    lateinit var client: OkHttpClient
    private var landLordId: Int = 0
    private val propertyViewModel: PropertyViewModel by viewModels {
        PropertyViewModelFactory((application as LandlordApplication).repository)
    }
    private var isTenant: Boolean = false
    private val sharedPrefs by lazy{ getSharedPreferences("preferences", Context.MODE_PRIVATE)}
    private val activities = arrayListOf("Dashboard","Profile", "Login")
    lateinit var recyclerView: RecyclerView
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address)

        val actionbar = supportActionBar
        actionbar?.setDisplayHomeAsUpEnabled(true)
        actionbar?.setHomeButtonEnabled(true)


        appContext = this
        model = propertyViewModel
        inflator = this.layoutInflater
//        client = OkHttpClient()

//        propertyViewModel.insertUser(UserTable(1, password = "1234", "Test","asd@123.ca"))
//        db = AppDatabase.getDatabase(this)
//        dao = db.landlordDao()
        Log.d("Listing", "call")
        scope = CoroutineScope(Job() + Dispatchers.IO)
        isTenant = intent.getBooleanExtra("isTenant", false)

        landLordId = if(!isTenant) sharedPrefs.getInt("id",1) else intent.getIntExtra("landlrdId",1)

//        if(!isTenant) {
        recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val adapter = PropertyListAdapter(propertyViewModel, isTenant, recyclerView)
        val llManager = LinearLayoutManager(this)
        propertyViewModel.loadPropertiesFrom(landLordId).observe(this) { properties ->
            // Update the cached copy of the words in the adapter.
            properties.let {
                adapter.submitList(it?.toMutableList())
                addresses = it?.toMutableList()
            }
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = llManager

        val addBtn = findViewById<Button>(R.id.addNewAddress)
        if(isTenant) addBtn.visibility = View.GONE
        else addBtn.visibility = View.VISIBLE
        addBtn.setOnClickListener {
            val username = sharedPrefs.getString("name", "name")
            if (username != null) {
                Utils.buildAddAddressDialog(isTenant,landLordId,username)
            }
        }
        val viewAllBtn = findViewById<Button>(R.id.viewAllAddress)

        viewAllBtn.setOnClickListener {
            Log.d("ViewALl", "Click")
            Utils.viewAll(addresses,landLordId, isTenant)
        }
        val voiceBtn = findViewById<ImageButton>(R.id.voiceBtn)
        voiceBtn.setOnClickListener {
            Log.d("Voice", "Voice")

            openVoiceActivityForResult()
        }
        val menus = findViewById<BottomNavigationView>(R.id.navigation_menu)
        menus.setOnItemSelectedListener { item->
            val id = item.itemId
            when (id) {
                R.id.action_home -> {
                    val intent = Intent(this, Dashboard::class.java)
                    startActivity(intent)
                    true
                }
                R.id.action_property ->{
                    Log.d("Listing", "Add")
                    val intt = Intent(this, Listing::class.java)
                    intt.putExtra("isTenant", false);
//                    recreate()
                    startActivity(intt)
                    finish()
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
    }


    private fun openVoiceActivityForResult() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Which page do you want to open?")
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
        voiceLauncher.launch(intent)
    }

    var voiceLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        Log.d("Voice", result.toString())
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            if (data != null) {
                val command = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.get(0)
                if (command != null) {
                    val newStr = command.split(" ").map { it.replaceFirstChar { firstChar ->
                        firstChar.uppercase()
                    } }.joinToString(" ")
                    Log.d("Voice newStr", newStr)
                    val intent = Intent(this,
                        Class.forName("com.example.androidapplicationproject." +
                                activities.filter { newStr.contains(it)}[0]
                        ))
                    startActivity(intent)
                }
            }
        }
    }

    //    var mapLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//        Log.d("Map", result.toString())
//        if (result.resultCode == Activity.RESULT_OK) {
//            val data: Intent? = result.data
//            if (data != null) {
//                    Log.d("map", data.toString())
//                }
//        }
//    }
    companion object{
        lateinit  var appContext: Context
        lateinit var model: PropertyViewModel
        lateinit var inflator: LayoutInflater
    }
}