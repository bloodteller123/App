package com.example.androidapplicationproject.Util

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import com.example.androidapplicationproject.R
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


// Implement OnMapReadyCallback.
class MapActivity : AppCompatActivity(), OnMapReadyCallback {
    var isTenant = false
    lateinit var latLngList: ArrayList<LatLng>
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val actionbar = supportActionBar
        actionbar?.setDisplayHomeAsUpEnabled(true)
        actionbar?.setHomeButtonEnabled(true)

        //https://issuetracker.google.com/issues/240585930#comment6
        latLngList = intent.getParcelableArrayListExtra("key")!!
        isTenant = intent.getBooleanExtra("isTenant",false)
        Log.d("longMap", latLngList[0].toString())
        // Get a handle to the fragment and register the callback.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
    }

    // Get a handle to the GoogleMap object and display marker.
    override fun onMapReady(googleMap: GoogleMap) {
        googleMap.uiSettings.isZoomControlsEnabled = true
        for(ind in latLngList.indices){
            if(ind==0){
                googleMap.moveCamera(CameraUpdateFactory
                    .newCameraPosition(CameraPosition(latLngList[ind], 17.0F, 5F,0F)))
            }
            googleMap.addMarker(
                MarkerOptions()
                    .position(latLngList[ind])
//                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.home_marker))
                    .title("Property"))
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                Log.d("Click", "Back")
                val intent = Intent()
                intent.putExtra("isTenant", isTenant)
                setResult(Activity.RESULT_OK, intent)
                onBackPressedDispatcher.onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


}