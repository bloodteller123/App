package com.example.androidapplicationproject

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.androidapplicationproject.Util.Consts
import com.example.androidapplicationproject.database.PropertyViewModel
import com.example.androidapplicationproject.database.PropertyViewModelFactory
import com.example.androidapplicationproject.database.UserTable
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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
    private lateinit var signinbtn: Button
    lateinit var scope: CoroutineScope
    private val propertyViewModel: PropertyViewModel by viewModels {
        PropertyViewModelFactory((application as LandlordApplication).repository)
    }
    private lateinit var requestPermissionLauncher:ActivityResultLauncher<String>
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(applicationContext, "notification permission granted", Toast.LENGTH_SHORT)
                    .show()
            } else {
                if (shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {
                    showNotificationPermissionRationale()
                } else {
                    showSettingDialog()
                }
            }
        }

        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            -> {}
            else -> {
                requestPermissionLauncher.launch(
                    Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        appContext = this
        uname = findViewById(R.id.editTextuserName)
        pword = findViewById(R.id.editTextTextPassword)
        cpword = findViewById(R.id.editTextTextPassword2)
        signupbtn = findViewById(R.id.signup_button)
        signinbtn = findViewById(R.id.sign_in_button_in_signup_page)
        email = findViewById(R.id.email_edittext)

//        db = DBHelper(this)
        scope = CoroutineScope(Job() + Dispatchers.IO)
        signupbtn.setOnClickListener {
            val unametext = uname.text.toString()
            val pwordtext = pword.text.toString()
            val cpwordtext = cpword.text.toString()
            val emailtext = email.text.toString()
//            val savedata = db.insertdata(unametext, pwordtext)
            registerChannel()
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

        signinbtn.setOnClickListener {
            val intent = Intent(appContext, Login::class.java)
            startActivity(intent)
        }
    }
    companion object{
        lateinit  var appContext: Context
    }

    fun registerChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel.
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel(Consts.CHANNEL_ID, name, importance)
            mChannel.description = descriptionText
            // Register the channel with the system. You can't change the importance
            // or other notification behaviors after this.
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }
    }

    private fun showSettingDialog() {
        MaterialAlertDialogBuilder(this, com.google.android.material.R.style.MaterialAlertDialog_Material3)
            .setTitle("Notification Permission")
            .setMessage("Notification permission is required, Please allow notification permission from setting")
            .setPositiveButton("Ok") { _, _ ->
                val intent = Intent(ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse("package:$packageName")
                startActivity(intent)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showNotificationPermissionRationale() {

        MaterialAlertDialogBuilder(this, com.google.android.material.R.style.MaterialAlertDialog_Material3)
            .setTitle("Alert")
            .setMessage("Notification permission is required, to show notification")
            .setPositiveButton("Ok") { _, _ ->
                if (Build.VERSION.SDK_INT >= 33) {
                    requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}