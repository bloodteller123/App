package com.example.androidapplicationproject.Util

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Build
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.androidapplicationproject.LandlordList
import com.example.androidapplicationproject.Listing
import com.example.androidapplicationproject.R
import com.example.androidapplicationproject.database.PropertyTable
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.*
import okhttp3.*
import org.json.JSONObject
import org.json.JSONTokener
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


class Utils {
    companion object {
        var client = OkHttpClient()
        val scope = CoroutineScope(Job() + Dispatchers.IO)
        fun startMaps(pairs: MutableList<LatLng>, isTenant:Boolean){
            var intent = Intent(Listing.appContext, MapActivity::class.java)
            Log.d("pair", ArrayList(pairs).toString())
            Log.d("pair", ArrayList(pairs)[0].toString())
            var list = ArrayList(pairs)
            Log.d("pair", list.toString())
            intent.putParcelableArrayListExtra("key", list)
            intent.putExtra("isTenant", isTenant)
            Listing.appContext.startActivity(intent)
//        finish()
        }

        fun shareLocation(address: String, lat:Double, lng:Double){
            val text = "This beautiful unit is now up for renting! Come check it out at https://www.google.com/maps/search/?api=1&query=$lat,$lng"
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, text)
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, "Share Location")
            Listing.appContext.startActivity(shareIntent)
        }
        // if called from a tenant, use sharedPrefs.getInt("specific landlordId",1)
        // if called from a landlord, use sharedPrefs.getInt("id",1)
        fun viewAll(addresses: MutableList<PropertyTable>?, id:Int, isTenant:Boolean){
            CoroutineScope(Job() + Dispatchers.IO).launch {
//                var list = propertyViewModel.loadPropertiesFrom(id).value
                Log.d("viewall", addresses.toString())
                var pairs = mutableListOf<LatLng>()
                if (addresses != null) {
                    for(property in addresses){
                        pairs.add(LatLng(property.latitude,property.longitude))
                    }
                    Log.d("viewall", "start map")
                    startMaps(pairs,isTenant)
                }
            }
        }
        fun deleteProperty(latitude: Double, longitude: Double){
            CoroutineScope(Job() + Dispatchers.IO).launch{
                Listing.model.deleteProperty(latitude,longitude)
            }
        }

        //
//        @SuppressLint("MissingPermission")
        @SuppressLint("MissingPermission")
        @RequiresApi(Build.VERSION_CODES.M)
        fun buildNotification(context:Context, name:String, address:String){
            val intent = Intent(context, Listing::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

            val id = SystemClock.uptimeMillis().toInt()
            val textTitle = "New Property listed"
            val textContent = "One new listing at $address from $name has been listed"
            var builder = NotificationCompat.Builder(context, Consts.CHANNEL_ID)
                .setSmallIcon(R.drawable.noti)
                .setContentTitle(textTitle)
                .setContentText(textContent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
            if (ActivityCompat.checkSelfPermission(context,android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED){
                Log.d("permission", "true")
                NotificationManagerCompat.from(context).notify(id, builder.build())
            }
        }

        @RequiresApi(Build.VERSION_CODES.M)
        fun buildAddAddressDialog(isTenant: Boolean, landLordId: Int, username:String) {
            val builder: AlertDialog.Builder = AlertDialog.Builder(Listing.appContext)

            val inflater: LayoutInflater = Listing.inflator
            val addAddressDialog: View = inflater.inflate(R.layout.alert_dialog, null)

            val addressLine: EditText = addAddressDialog.findViewById(R.id.property_address)
            val city: EditText = addAddressDialog.findViewById(R.id.property_city)
            val country: EditText = addAddressDialog.findViewById(R.id.property_country)
            val zip: EditText = addAddressDialog.findViewById(R.id.property_post)
            val description: EditText = addAddressDialog.findViewById(R.id.property_des)
            val price: EditText = addAddressDialog.findViewById(R.id.property_price)
            val name: EditText = addAddressDialog.findViewById(R.id.property_name)

            val addBtn: Button = addAddressDialog.findViewById(R.id.submit_button)
            val cancelBtn: Button = addAddressDialog.findViewById(R.id.cancel_button)
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

                Log.d("name", username)
                buildNotification(address = "$addressLine"+" "+ "$city"+" " +"$country", context = Listing.appContext, name = username)

                if(validated) {
                    scope.launch {
                        val addresString = addressLine.text.toString()+", "+ zip.text.toString().replace(
                            " ",
                            ""
                        ) + "," + city.text.toString() + "," + country.text.toString()
                        val geo: LatLng = getGeoPoint(addresString, country.text.toString()).await()
                        Log.d("address", geo.toString())
                        //            scope.launch {
                        Listing.model.insertProperty(
                            PropertyTable(
                                //                    propertyId = 0,
                                ownerId = landLordId,
//                            ownerId = 1,
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
                    Toast.makeText(Listing.appContext, "New address added", Toast.LENGTH_LONG).show()
                }
            }
            cancelBtn.setOnClickListener {
                alert.cancel()
            }

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

        fun startLandlordPage(landLordId: Int){
            val intent = Intent(LandlordList.appContext, Listing::class.java)
            intent.putExtra("isTenant", true)
            intent.putExtra("landlrdId", landLordId)
            LandlordList.appContext.startActivity(intent)
        }
    }
}