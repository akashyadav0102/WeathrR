package com.example.weathrr

import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.ui.text.capitalize
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.fragment_today_screen.*
import kotlinx.android.synthetic.main.fragment_today_screen.view.*
import org.json.JSONObject


class TodayScreen : Fragment()  {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        val todayScreenView : View = inflater.inflate(R.layout.fragment_today_screen,container,false)
        todayScreenView.rain_snow.visibility = INVISIBLE

        val bundle = this.arguments
        val latitude = bundle?.getString("LATITUDE")
        val longitude = bundle?.getString("LONGITUDE")

        val url = "https://pro.openweathermap.org/data/2.5/onecall?lat=" + latitude + "&lon=" + longitude +
                "&APPID=" + getString(R.string.api_key) + "&units=metric"


        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET,url,null,
            { response ->

                val hour = response.getInt("timezone_offset")
                val jsonCurrentObject = response.getJSONObject("current")

                todayScreenView.sunrise_data.text = calcTime(hour + jsonCurrentObject.getInt("sunrise")).toString()
                todayScreenView.sunset_data.text = calcTime(hour + jsonCurrentObject.getInt("sunset")).toString()

                todayScreenView.current_temp.text = getString(R.string.current_temp_text,jsonCurrentObject.getInt("temp"))
                todayScreenView.feels_like.text = getString(R.string.feels_like_text,jsonCurrentObject.getInt("feels_like"))

                todayScreenView.pressure_bar.text = getString(R.string.pressure_text,jsonCurrentObject.getInt("pressure"))
                todayScreenView.humidity_percent.text = getString(R.string.humidity_text,
                    jsonCurrentObject.getInt("humidity"),"%")
                todayScreenView.dew_temperature.text = getString(R.string.dew_text,jsonCurrentObject.getInt("dew_point"))
                todayScreenView.cloud_percent.text = getString(R.string.clouds_text,jsonCurrentObject.getInt("clouds"),"%")
                todayScreenView.wind_speed.text = getString(R.string.wind_speed_text,jsonCurrentObject.getString("wind_speed"))
                todayScreenView.wind_gust_detail.text = getString(R.string.wind_gusts_text,jsonCurrentObject.getString("wind_gust"))

                todayScreenView.visibility_data.text = getString(R.string.visibility_text,jsonCurrentObject.getInt("visibility"))
                todayScreenView.uv_data.text = getString(R.string.uvi_text,jsonCurrentObject.getInt("uvi"))

                val jsonWeatherArray = jsonCurrentObject.getJSONArray("weather")

                for(item in 0 until jsonWeatherArray.length() ){
                    val weatherCondition = jsonWeatherArray.getJSONObject(item)
                    todayScreenView.weather_description.text = weatherCondition
                        .getString("description").uppercase()
                }

                if(jsonCurrentObject.has("rain")){
                    todayScreenView.rain_snow.visibility = VISIBLE
                    rain_snow.text = getString(R.string.past_rain_snow,"rain",
                        jsonCurrentObject.getJSONObject("rain").getString("1h"))
                }

                if(jsonCurrentObject.has("snow")){
                    todayScreenView.rain_snow.visibility = VISIBLE
                    rain_snow.text = getString(R.string.past_rain_snow,"snow",
                        jsonCurrentObject.getJSONObject("snow").getString("1h"))
                }


                val jsonDailyArray = response.getJSONArray("daily")

                val todayDetails = jsonDailyArray.getJSONObject(0)

                todayScreenView.moonrise_data.text = calcTime(hour + todayDetails.getInt("moonrise"))
                todayScreenView.moonset_data.text = calcTime(hour + todayDetails.getInt("moonset"))
                todayScreenView.moonphase_data.text = todayDetails.getString("moon_phase")

                todayScreenView.max_temp_data.text = getString(R.string.current_temp_text,
                    todayDetails.getJSONObject("temp").getInt("max"))

                todayScreenView.min_temp_data.text = getString(R.string.current_temp_text,
                    todayDetails.getJSONObject("temp").getInt("min"))

            },
            { error ->
                error.printStackTrace()
            }
        )

        MySingleton.getInstance(requireContext()).addToRequestQueue(jsonObjectRequest)

        val aqi_url = "https://pro.openweathermap.org/data/2.5/air_pollution?lat=" + latitude + "&lon=" + longitude +
                "&APPID=" + getString(R.string.api_key)

        val pollutionObjectRequest = JsonObjectRequest(Request.Method.GET,aqi_url,null,
            {   response->

                val jsonarray = response.getJSONArray("list")

                for(item in 0 until jsonarray.length()){
                    val airPollution = jsonarray.getJSONObject(item)

                    val main = airPollution.getJSONObject("main")
                    val aqi = main.getInt("aqi")

                    todayScreenView.aqi_pieChart.setAqiValue(aqi)
                    todayScreenView.aqi_data.text = getString(R.string.aqi_level,aqi)

                    when(aqi){
                        1->todayScreenView.info.text = getString(R.string.aqi_1)
                        2->todayScreenView.info.text = getString(R.string.aqi_2)
                        3->todayScreenView.info.text = getString(R.string.aqi_3)
                        4->todayScreenView.info.text = getString(R.string.aqi_4)
                        5->todayScreenView.info.text = getString(R.string.aqi_5)
                    }

                    val components = airPollution.getJSONObject("components")
                    val co_conc = components.getDouble("co")
                    val no2_conc = components.getDouble("no2")
                    val o3_conc = components.getDouble("o3")
                    val so2_conc = components.getDouble("so2")
                    val pm2_5_conc = components.getDouble("pm2_5")
                    val pm10_conc = components.getDouble("pm10")
                }
            },
            {   error->
                error.printStackTrace()
            }
        )

        MySingleton.getInstance(requireContext()).addToRequestQueue(pollutionObjectRequest)

        return todayScreenView
    }

    private fun calcTime(hour : Int): CharSequence {
        var Hour = hour
        Hour %= 86400
        var min = Hour % 3600
        min /= 60
        Hour /= 3600

        val Min : String
        if(min<10)
            Min = "0$min"

        else
            Min = "$min"

        var time = ""

        time = if (Hour<12){
            Hour.toString() + ":" + Min + " A.M."
        } else{
            Hour %= 12
            Hour.toString() + ":" + Min + " P.M."
        }

        return time
    }

}