package com.example.androidapplicationproject.mapUtil

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.androidapplicationproject.R
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

// Implement OnMapReadyCallback.
class MapActivity : AppCompatActivity(), OnMapReadyCallback {
    var lat: Double = 0.0
    var long: Double = 0.0
    lateinit var latLngList: ArrayList<LatLng>
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
//        val extras = intent.extras
//
//        if (extras != null) {
////            lat = extras.getDouble("lat")
////            long = extras.getDouble("long")
////            Log.e("latMap", lat.toString())
//            Log.d("MAP", "MAP")
//
//            latLngList = extras.getParcelableArray("LatLng", LatLng::class.java)!!
//            Log.e("longMap", latLngList[0].toString())
//        }
        latLngList = intent.getParcelableArrayListExtra("key", LatLng::class.java)!!
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
                    .title("Some locations"))
        }
    }
}