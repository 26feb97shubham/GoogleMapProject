package com.example.googlemapstracking

import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.example.googlemapstracking.databinding.ActivityMapsBinding
import com.google.android.gms.maps.model.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, LocationListener,
    SeekBar.OnSeekBarChangeListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var locationManager: LocationManager
    private val MIN_TIME : Int = 1000 //1 sec
    private val MIN_DISTANCE : Int = 1 //1 meter
    private var polyline: Polyline? = null
    private lateinit var latlanlist : ArrayList<LatLng>
    private lateinit var markerlist : ArrayList<Marker>
    private lateinit var markerOptions: MarkerOptions
    private lateinit var marker : Marker
    private lateinit var polylineOptions: PolylineOptions
    private var red : Int = 0; var green : Int = 0; var blue : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.buttonDraw.setOnClickListener {
            polylineOptions = PolylineOptions()
            polylineOptions.addAll(latlanlist)
            polyline = mMap.addPolyline(polylineOptions)
        }

        binding.buttonClear.setOnClickListener {
            if (polyline!=null) {
                polyline!!.remove()
            }
            for (marker : Marker in markerlist){
                marker.remove()
            }
            latlanlist.clear()
            markerlist.clear()
            binding.seekbarWidth.progress = 3
            binding.seekbarRed.progress = 0
            binding.seekbarGreen.progress = 0
            binding.seekbarBlue.progress = 0
        }

        binding.seekbarRed.setOnSeekBarChangeListener(this)
        binding.seekbarBlue.setOnSeekBarChangeListener(this)
        binding.seekbarGreen.setOnSeekBarChangeListener(this)

    }

    private fun getLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this,
            android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            &&
            ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
            &&
            ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED)
                {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    MIN_TIME.toLong(),
                    MIN_DISTANCE.toFloat(),
                    this
                )
            } else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    MIN_TIME.toLong(),
                    MIN_DISTANCE.toFloat(),
                    this
                )
            } else {
                Toast.makeText(
                    this,
                    "No Provider Enabled",
                    Toast.LENGTH_LONG
                )
                    .show()
            }
        }else{
            ActivityCompat.requestPermissions(this, arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION
            ),101)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.setOnMapClickListener {
            markerOptions = MarkerOptions()
            markerOptions.position(it)
            marker = mMap.addMarker(markerOptions)
            latlanlist = ArrayList()
            markerlist = ArrayList()
            latlanlist.add(it)
            markerlist.add(marker)
        }
    }

    override fun onLocationChanged(location: Location) {
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode==101){
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLocationUpdates()
            }else{
                Toast.makeText(this,
                "Permission Required", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    override fun onProgressChanged(seekBar: SeekBar?, p1: Int, p2: Boolean) {
        when(seekBar!!.id){
            R.id.seekbar_red -> {
                red = p1
            }
            R.id.seekbar_green -> {
                green = p1
            }
            R.id.seekbar_blue -> {
                blue = p1
            }
        }
        polyline!!.color = Color.rgb(red, green, blue)
        setWidth()
    }

    private fun setWidth() {
        binding.seekbarWidth.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                val width : Int = p0!!.progress
                if (polyline!=null){
                    polyline!!.width = width.toFloat()
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }

        })
    }

    override fun onStartTrackingTouch(p0: SeekBar?) {
    }

    override fun onStopTrackingTouch(p0: SeekBar?) {
    }
}