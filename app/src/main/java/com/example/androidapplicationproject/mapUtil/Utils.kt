package com.example.androidapplicationproject.mapUtil

import android.content.Intent
import android.util.Log
import androidx.activity.viewModels
import com.example.androidapplicationproject.AddressActivity
import com.example.androidapplicationproject.LandlordApplication
import com.example.androidapplicationproject.database.PropertyTable
import com.example.androidapplicationproject.database.PropertyViewModel
import com.example.androidapplicationproject.database.PropertyViewModelFactory
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class Utils {
    companion object {
        fun startMaps(pairs: MutableList<LatLng>){
            var intent = Intent(AddressActivity.appContext, MapActivity::class.java)
            Log.d("pair", ArrayList(pairs).toString())
            Log.d("pair", ArrayList(pairs)[0].toString())
            intent.putParcelableArrayListExtra("key", ArrayList(pairs))

            AddressActivity.appContext.startActivity(intent)
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
            AddressActivity.appContext.startActivity(shareIntent)
        }
        // if called from a tenant, use sharedPrefs.getInt("specific landlordId",1)
        // if called from a landlord, use sharedPrefs.getInt("id",1)
        fun viewAll(addresses: MutableList<PropertyTable>?, id:Int){
            CoroutineScope(Job() + Dispatchers.IO).launch {
//                var list = propertyViewModel.loadPropertiesFrom(id).value
                Log.d("viewall", addresses.toString())
                var pairs = mutableListOf<LatLng>()
                if (addresses != null) {
                    for(property in addresses){
                        pairs.add(LatLng(property.latitude,property.longitude))
                    }
                    Log.d("viewall", "start map")
                    startMaps(pairs)
                }

            }


        }
    }
}