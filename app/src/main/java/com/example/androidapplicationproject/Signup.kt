package com.example.androidapplicationproject

import android.content.Context
import android.content.Intent
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

class Signup : AppCompatActivity() {

    private lateinit var uname: EditText
    private lateinit var pword: EditText
    private lateinit var cpword: EditText
    private lateinit var email: EditText
    private lateinit var signupbtn: Button
    private lateinit var db: DBHelper
    lateinit var scope: CoroutineScope
    private val propertyViewModel: PropertyViewModel by viewModels {
        PropertyViewModelFactory((application as LandlordApplication).repository)
    }
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        appContext = this
        uname = findViewById(R.id.editTextuserName)
        pword = findViewById(R.id.editTextTextPassword)
        cpword = findViewById(R.id.editTextTextPassword2)
        signupbtn = findViewById(R.id.signup_button)
        email = findViewById(R.id.email_edittext)

//        db = DBHelper(this)
        scope = CoroutineScope(Job() + Dispatchers.IO)
        signupbtn.setOnClickListener {
            val unametext = uname.text.toString()
            val pwordtext = pword.text.toString()
            val cpwordtext = cpword.text.toString()
            val emailtext = email.text.toString()
//            val savedata = db.insertdata(unametext, pwordtext)

            if (TextUtils.isEmpty(unametext) || TextUtils.isEmpty(pwordtext) || TextUtils.isEmpty(
                    cpwordtext
                )
            ) {
                Toast.makeText(
                    this,
                    "Add username, password, and confirm password",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                if (pwordtext.equals(cpwordtext)) {
                    scope.launch {
                        val users: List<UserTable> = propertyViewModel.checkuserpass(unametext,pwordtext)
                        if (users.isEmpty()) {
                            propertyViewModel.insertUser(UserTable(
                                userName = unametext,
                                password = pwordtext,
                                email = emailtext
                            ))
                            runOnUiThread{
                                Toast.makeText(appContext, "Signup successful", Toast.LENGTH_SHORT).show()
                            }

                            val intent = Intent(appContext, Login::class.java)
                            startActivity(intent)
                        } else {
                            runOnUiThread{
                                Toast.makeText(appContext, "User Exists", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } else {
                    Toast.makeText(this, "Password Not Match", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    companion object{
        lateinit  var appContext: Context
    }
}