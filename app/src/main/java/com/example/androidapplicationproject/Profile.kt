package com.example.androidapplicationproject

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.androidapplicationproject.database.PropertyViewModel
import com.example.androidapplicationproject.database.PropertyViewModelFactory
import com.example.androidapplicationproject.database.UserTable
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class Profile : AppCompatActivity() {
    private val propertyViewModel: PropertyViewModel by viewModels {
        PropertyViewModelFactory((application as LandlordApplication).repository)
    }
    private val sharedPrefs by lazy{ getSharedPreferences("preferences", Context.MODE_PRIVATE)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        val actionbar = supportActionBar
        actionbar?.setDisplayHomeAsUpEnabled(true)
        actionbar?.setHomeButtonEnabled(true)
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

        val scope = CoroutineScope(Job() + Dispatchers.IO)
        val name = findViewById<TextView>(R.id.user_profile_editText_person_name)
        val email = findViewById<TextView>(R.id.user_profile_editText_email)
        val password = findViewById<TextView>(R.id.user_profile_editText_password)
        val userId = sharedPrefs.getInt("id",1)
        scope.launch {
            val users: List<UserTable> = propertyViewModel.loadusers(userId)
            name.text = users[0].userName
            email.text = users[0].email
            password.text = users[0].password
        }
    }
}