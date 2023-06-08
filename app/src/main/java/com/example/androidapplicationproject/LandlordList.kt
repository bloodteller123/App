package com.example.androidapplicationproject

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.widget.ImageButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidapplicationproject.database.LandlordListAdapter
import com.example.androidapplicationproject.database.PropertyViewModel
import com.example.androidapplicationproject.database.PropertyViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView

class LandlordList : AppCompatActivity() {
    lateinit var recyclerView:RecyclerView
    private val propertyViewModel: PropertyViewModel by viewModels {
        PropertyViewModelFactory((application as LandlordApplication).repository)
    }
    private val activities = arrayListOf("Dashboard","Profile", "Login")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landlord_list)

        appContext = this
        recyclerView = findViewById<RecyclerView>(R.id.recyclerview_landlord)
        val adapter = LandlordListAdapter()
        val llManager = LinearLayoutManager(this)
        propertyViewModel.loadAllLandlords().observe(this){ landlords ->
            landlords.let {
                adapter.submitList(it?.toMutableList())
            }
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = llManager
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
                R.id.action_add ->{
                    intent = Intent(this, Listing::class.java)
                    intent.putExtra("isTenant", false)
                    true
                }
                R.id.action_property ->{
                    intent = Intent(this, LandlordList::class.java)
                    startActivity(intent)
                    true
                }
                R.id.action_profile ->{
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
    companion object{
        lateinit var appContext: Context
    }
}