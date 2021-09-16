
package com.example.weathrr

import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentSender
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.fragment.app.Fragment
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.menu_button.*
import kotlinx.android.synthetic.main.menu_button.view.*
import kotlinx.android.synthetic.main.settings_gear_layout.*
import kotlinx.android.synthetic.main.settings_gear_layout.view.*
import kotlinx.android.synthetic.main.fragment_today_screen.*

class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var settingsClient: SettingsClient
    private lateinit var locationRequest : LocationRequest
    private lateinit var locationCallback: LocationCallback

    val todayFragment = TodayScreen()
    val hourlyFragment = HourlyScreen()
    val dailyFragment = DailyScreen()
    val radarFragment = RadarScreen()

    private val REQUEST_CHECK_SETTINGS = 500
    private var requestingLocationUpdates: Boolean = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.startService(Intent(this, LocationServices::class.java))
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        createLocationRequest()

        locationCallback = object : LocationCallback(){
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations){

                    val lat = location.latitude.toString()
                    val lon = location.longitude.toString()
                    val result = Geocoder(this@MainActivity).getFromLocation(
                        location.latitude,location.longitude,1).elementAt(0).locality.toString() + ", " +
                            Geocoder(this@MainActivity).getFromLocation(
                                location.latitude,location.longitude,1).elementAt(0).countryCode.toString()


                    val bundleToday = Bundle()
                    bundleToday.putString("LATITUDE",lat)
                    bundleToday.putString("LONGITUDE",lon)

                    todayFragment.arguments = bundleToday
                    hourlyFragment.arguments = bundleToday
                    dailyFragment.arguments = bundleToday

                    val bundleRadar = Bundle()
                    bundleRadar.putDouble("LATITUDE",location.latitude)
                    bundleRadar.putDouble("LONGITUDE",location.longitude)
                    radarFragment.arguments = bundleRadar

                    addFragment(todayFragment)
                    showFragment(todayFragment)
                    addFragment(radarFragment)
                    addFragment(dailyFragment)
                    addFragment(hourlyFragment)

                    top_bar.top_bar_text.text = result
                }
            }
        }


        navBar.setOnNavigationItemSelectedListener(BottomNavigationView
            .OnNavigationItemSelectedListener {item ->
            when(item.itemId){
                R.id.today_button->{
                    changeFragment(fragment = todayFragment)
                    showFragment(todayFragment)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.hourly_button->{
                    changeFragment(fragment = hourlyFragment)
                    showFragment(hourlyFragment)
                    return@OnNavigationItemSelectedListener true
                }

                R.id.daily_button->{
                    changeFragment(fragment = dailyFragment)
                    showFragment(dailyFragment)
                    return@OnNavigationItemSelectedListener true
                }

                else->{
                    changeFragment(fragment = radarFragment)
                    showFragment(radarFragment)
                    return@OnNavigationItemSelectedListener true
                }

            }
        })


        drawer_layout.addDrawerListener(ActionBarDrawerToggle(this,drawer_layout,
            top_bar,R.string.nav_open,R.string.nav_close))


        val headerView: View = navigation_drawer.getHeaderView(0)
        headerView.settings_btn.setOnClickListener(View.OnClickListener {
            Toast.makeText(this, "Icon Clicked!", Toast.LENGTH_SHORT).show()
            return@OnClickListener
        })


        navigation_drawer.setNavigationItemSelectedListener(NavigationView.OnNavigationItemSelectedListener { item ->
            when(item.itemId){
                R.id.item_favourite->{
                    //doSomething
                    Toast.makeText(this, "Clicked!", Toast.LENGTH_SHORT).show()
                    return@OnNavigationItemSelectedListener true
                }

                //Don't forget location 2 and 3

                else->{return@OnNavigationItemSelectedListener false}
            }
        })


//        top_bar.setOnMenuItemClickListener { MenuItem->
//            when(MenuItem.itemId){
//                R.id.add_button->{
//                    //
//                    return@setOnMenuItemClickListener true
//                }
//
//                else->{return@setOnMenuItemClickListener false}
//            }
//
//        }


    }

    private fun createLocationRequest(){
        locationRequest = LocationRequest.create().apply {
            interval = 20
            fastestInterval = 10
            priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
            numUpdates = 1
        }

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

        settingsClient  = LocationServices.getSettingsClient(this)

        val task: Task<LocationSettingsResponse> =
            settingsClient.checkLocationSettings(builder.build())

        task.addOnSuccessListener { LocationSettingsResponse ->
            startLocationUpdates()
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {

                try {
                    exception.startResolutionForResult(this,REQUEST_CHECK_SETTINGS)
                    startLocationUpdates()

                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }

    private fun addFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.fragmentContainer,fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.hide(fragment)
        fragmentTransaction.commit()
    }

    private fun showFragment(fragment: Fragment){
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.show(fragment)
        fragmentTransaction.commit()
    }

    private fun hideFragment(fragment: Fragment){
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.hide(fragment)
        fragmentTransaction.commit()
    }

    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(locationRequest,
            locationCallback,
            Looper.getMainLooper())
    }


    private fun changeFragment(fragment: Fragment){
        val fragments = supportFragmentManager.fragments
        var activeFragment = Fragment()

        for (item in fragments){
            if(item.isVisible){
                activeFragment = item
                break
            }
        }

        if (activeFragment != fragment){
            hideFragment(activeFragment)
            showFragment(fragment)
        }
    }

    override fun onBackPressed() {
        if(navBar.selectedItemId == R.id.today_button){
            super.onBackPressed()
            finish()
        }

        else{
            navBar.selectedItemId = R.id.today_button
            changeFragment(fragment = todayFragment)
        }

    }

    fun managelocations(view: View) {
        //
        Toast.makeText(this, "Button Clicked", Toast.LENGTH_SHORT).show()
    }
}