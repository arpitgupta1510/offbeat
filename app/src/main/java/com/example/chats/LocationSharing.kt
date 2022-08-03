package com.example.chats

import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import com.example.chats.databinding.ActivityLocationSharingBinding
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import java.io.IOException
import java.util.jar.Manifest

class LocationSharing : AppCompatActivity(), OnMapReadyCallback,GoogleMap.OnMarkerClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityLocationSharingBinding
    companion object {
        private const val MY_PERMISSION_FINE_LOCATION = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLocationSharingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        // Add a marker in Sydney and move the camera
        val india = LatLng(20.5937, 78.9629)
        mMap.addMarker(MarkerOptions().position(india).title("Marker in India"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(india))
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true
        }
        else {//condition for Marshmello and above
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), MY_PERMISSION_FINE_LOCATION)
            }
        }
        mMap.setOnMarkerClickListener(this)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSION_FINE_LOCATION -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {//permission to access location grant
                if (ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mMap.isMyLocationEnabled = true
                }
            }
            //permission to access location denied
            else {
                Toast.makeText(applicationContext, "This app requires location permissions to be granted", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    override fun onMarkerClick(p0: Marker): Boolean {
        return false
    }
    fun searchLocation(view:View){
        var location=binding.searchedLocation.text.toString()
        var addressList:List<Address>?=null
        if(location=="")
        {
            Toast.makeText(this, "Enter Location Name", Toast.LENGTH_SHORT).show()
        }else{
            val geocoder=Geocoder(this)
            try{
                addressList=geocoder.getFromLocationName(location,1)
            }catch (e:IOException)
            {
                e.printStackTrace()
            }
            val address=addressList!![0]
            val latLng=LatLng(address.latitude,address.longitude)
            mMap.addMarker(MarkerOptions().position(latLng).title(location))
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))
            Toast.makeText(applicationContext,address.latitude.toString()+" "+address.longitude.toString(), Toast.LENGTH_SHORT).show()
        }

    }

}