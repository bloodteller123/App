package com.example.androidapplicationproject

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.androidapplicationproject.database.PropertyViewModel
import com.example.androidapplicationproject.database.PropertyViewModelFactory
import com.example.androidapplicationproject.database.UserTable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class Login : AppCompatActivity() {
    private lateinit var loginbtn : Button
    private lateinit var edituser : EditText
    private lateinit var editpword : EditText
//    private lateinit var dbh : DBHelper
    lateinit var scope: CoroutineScope
    private val propertyViewModel: PropertyViewModel by viewModels {
        PropertyViewModelFactory((application as LandlordApplication).repository)
    }
    private val sharedPrefs by lazy{ getSharedPreferences("preferences", Context.MODE_PRIVATE)}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        appContext = this
        loginbtn = findViewById(R.id.login_button)
        edituser = findViewById(R.id.editTextTextPersonName2)
        editpword = findViewById(R.id.editTextTextPassword3)
//        dbh = DBHelper(this)
        scope = CoroutineScope(Job() + Dispatchers.IO)
        loginbtn.setOnClickListener{
            val userdtx = edituser.text.toString()
            val passedtx = editpword.text.toString()

            if(TextUtils.isEmpty(userdtx) || TextUtils.isEmpty(passedtx)){
                Toast.makeText(this,"Add Username & Password", Toast.LENGTH_SHORT).show()
            }
            else{
                scope.launch {
                    val users: List<UserTable> = propertyViewModel.checkuserpass(userdtx,passedtx)

//                val checkuser = dbh.checkuserpass(userdtx,passedtx)
                    if(users.isNotEmpty()){
                        runOnUiThread{
                            Toast.makeText(appContext,"Login Successful", Toast.LENGTH_SHORT).show()
                        }
                        val editor: SharedPreferences.Editor =  sharedPrefs.edit()
                        editor.putInt("id",users[0].userId)
                        editor.apply()
                        editor.apply()

                        val intent = Intent(appContext, TempThirdActivity::class.java)
                        startActivity(intent)
                    }
                    else{
                        runOnUiThread{
                            Toast.makeText(appContext,"Wrong longin info.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
    companion object{
        lateinit  var appContext: Context
    }
}