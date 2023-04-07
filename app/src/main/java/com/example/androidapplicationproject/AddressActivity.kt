package com.example.androidapplicationproject

import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.widget.Toolbar
//import android.support.v7.widget.Toolbar
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidapplicationproject.database.*
import com.google.android.gms.maps.model.LatLng
import androidx.activity.viewModels
import com.example.androidapplicationproject.mapUtil.MapActivity
import com.example.androidapplicationproject.mapUtil.Utils
import kotlinx.coroutines.*
import okhttp3.*
import org.json.JSONObject
import org.json.JSONTokener
import java.io.IOException
import java.util.*

class AddressActivity : AppCompatActivity() {

    private lateinit var locations: MutableList<Pair<Long,Long>>
    private var addresses: MutableList<PropertyTable>? = null
    lateinit var scope: CoroutineScope
    lateinit var client: OkHttpClient
    private val propertyViewModel: PropertyViewModel by viewModels {
        PropertyViewModelFactory((application as LandlordApplication).repository)
    }
    private val sharedPrefs by lazy{ getSharedPreferences("preferences", Context.MODE_PRIVATE)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address)
        appContext = this
        client = OkHttpClient()
        deleteDatabase("appDatabase.db")
        propertyViewModel.insertUser(UserTable(1, password = "1234", "Test","asd@123.ca"))
//        db = AppDatabase.getDatabase(this)
//        dao = db.landlordDao()
        scope = CoroutineScope(Job() + Dispatchers.IO)

        //get userId from intent
        // etc

//        buildAddresses()
//        initAddresses(userId)


        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val adapter = PropertyListAdapter(propertyViewModel, false,recyclerView)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false  )


        propertyViewModel.loadPropertiesFrom(1).observe(this){ properties ->
            // Update the cached copy of the words in the adapter.
            properties.let {
                adapter.submitList(it?.toMutableList())
                addresses = it?.toMutableList()
            }
        }


//        val viewBtn = findViewById<Button>(R.id.btn1)
//        val text = findViewById<TextView>(R.id.text1)
//        viewBtn.setOnClickListener{
//
//            //getGeoPoint will be called in new address form
//            //getGeoPoint(text.text.toString())
//        }
        val addBtn = findViewById<Button>(R.id.addNewAddress)
        addBtn.setOnClickListener {
            buildAddAddressDialog()
        }
        val viewAllBtn = findViewById<Button>(R.id.viewAllAddress)
        viewAllBtn.setOnClickListener {
            Log.d("ViewALl", "Click")
            Utils.viewAll(addresses,1)
        }
        val welcome_landlord_btn = findViewById<Button>(R.id.welcome_landlord)
        welcome_landlord_btn.setOnClickListener {
            viewWelcomeLandlordPage()
        }
    }

    companion object{
        lateinit  var appContext: Context
    }
//    private fun initAddresses(userId: Int){
//        scope.launch{
//            addresses = dao.loadPropertiesFrom(0)
//
//        }
//    }

    private fun buildAddAddressDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)

        val inflater: LayoutInflater = this.layoutInflater
        val addAddressDialog: View = inflater.inflate(R.layout.activity_temp_second, null)

        val addressLine: EditText = addAddressDialog.findViewById(R.id.property_address)
        val city: EditText = addAddressDialog.findViewById(R.id.property_city)
        val country: EditText = addAddressDialog.findViewById(R.id.property_country)
        val zip: EditText = addAddressDialog.findViewById(R.id.property_post)
        val description: EditText = addAddressDialog.findViewById(R.id.property_des)
        val price: EditText = addAddressDialog.findViewById(R.id.property_price)
        val name: EditText = addAddressDialog.findViewById(R.id.property_name)

        val addBtn: Button = addAddressDialog.findViewById(R.id.submit_button)
        val alert: AlertDialog = builder.create()

        addBtn.setOnClickListener {
            var validated = true
            if(zip.text.toString().trim() == "") {
                zip.error = "This field can not be blank"
                validated = false
            }
            if(country.text.toString().trim() == "") {
                country.error = "This field can not be blank"
                validated = false
            }
            if(city.text.toString().trim() == "") {
                city.error = "This field can not be blank"
                validated = false
            }
            if(name.text.toString().trim() == "") {
                name.error = "This field can not be blank"
                validated = false
            }
            if(price.text.toString().trim().toIntOrNull() == null){
                price.error = "This field must be integer"
                validated = false
            }

            if(validated) {
                scope.launch {
                    val addresString = zip.text.toString().replace(
                        " ",
                        ""
                    ) + "," + city.text.toString() + "," + country.text.toString()
                    val geo: LatLng = getGeoPoint(addresString, country.text.toString()).await()
                    Log.d("address", geo.toString())
                    //            scope.launch {
                    propertyViewModel.insertProperty(
                        PropertyTable(
                            //                    propertyId = 0,
//                            ownerId = sharedPrefs.getInt("id",1),
                            ownerId = 1,
                            latitude = geo.latitude, longitude = geo.longitude,
                            address = addressLine.text.toString(), postCode = zip.text.toString(),
                            description = description.text.toString(),
                            city = city.text.toString(),
                            country = country.text.toString(),
                            price = price.text.toString().toInt(),
                            name = name.text.toString()
                        )
                    )
                    //                dao.insertProperty()
                    //            }
                    Log.d("click", "add address")
                    alert.dismiss()
                }
                Toast.makeText(this, "New address added", Toast.LENGTH_LONG).show()
            }
        }


        alert.setView(addAddressDialog)
        alert.show()
    }

    private fun viewWelcomeLandlordPage(){
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)

        val inflater: LayoutInflater = this.layoutInflater
        val addAddressDialog: View = inflater.inflate(R.layout.activity_temp_third, null)

        val toolbar: Toolbar = addAddressDialog.findViewById(R.id.toolbar)

        toolbar.setNavigationOnClickListener(View.OnClickListener {
            @Override
            fun onClick() {
                //back button pressed
            }
        })

        val alert: AlertDialog = builder.create()

        alert.setView(addAddressDialog)
        alert.show()
    }

    private fun getGeoPoint(address: String, countryName: String): Deferred<LatLng> {
        Log.d("address", address)
        Log.d("address", Locale.getDefault().toString())
        val countryCode = Locale.getISOCountries().find {
            Locale("", it).displayCountry == countryName
        }.toString().lowercase()
        Log.d("address",countryCode)
        //GeoCode is defaulted to the US
        val coder:Geocoder =  Geocoder(this)
        var geoLat = 0.0
        var geoLong = 0.0

        val baseUrl = "https://maps.googleapis.com/maps/api/geocode/json"
        val url = baseUrl+ "?address=$address&region=$countryCode&key=AIzaSyCbaZKxT4jqdTLKz3kbkKo4JLrr7V1VR5g"
        var request = Request.Builder()
            .url(url)
            .get()
//            .post(formBody)
            .build()
        Log.d("address request", request.toString())
        val deferredValue = CompletableDeferred<LatLng>()
        scope.launch {
            client.newCall(request).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    val res = response.body?.string()
                    if (res != null) {
                        Log.d("addressr", res)
                    }
                    val jsonObject = JSONTokener(res).nextValue() as JSONObject
                    val geometry = jsonObject.getJSONArray("results")
                        .getJSONObject(0)
                        .getJSONObject("geometry")
                        .getJSONObject("location")
                    Log.d("geometry", geometry.toString())
                    val lat = geometry.get("lat")
                    val lng = geometry.get("lng")
                    deferredValue.complete(LatLng(lat as Double, lng as Double))
                }

                override fun onFailure(call: Call, e: IOException) {
                    e.message?.let { Log.d("address Error", it) }
                }
            })
        }
        return deferredValue
    }
}