package com.example.weathrr

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import kotlinx.android.synthetic.main.fragment_daily_screen.view.*

class DailyScreen : Fragment() {

    private lateinit var adapter : DailyDataClass

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val dailyScreenView = inflater.inflate(R.layout.fragment_daily_screen, container, false)

        adapter = DailyDataClass(mutableListOf())
        dailyScreenView.daily_recycler_view.adapter = adapter
        dailyScreenView.daily_recycler_view.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)

        val bundle = this.arguments
        val latitude = bundle?.getString("LATITUDE")
        val longitude = bundle?.getString("LONGITUDE")

        val url = "https://pro.openweathermap.org/data/2.5/onecall?lat=" +  latitude + "&lon=" +
                longitude + "&exclude=current,hourly,minutely,alerts" + "&appid=" + getString(R.string.api_key) + "&units=metric"

        val jsonDailyObject = JsonObjectRequest(Request.Method.GET,url,null,
            {response->
                val timezone = response.getInt("timezone_offset")

                val jsonDailyArray = response.getJSONArray("daily")

                for(item in 0 until jsonDailyArray.length()){
                    val jsonSingleDay = jsonDailyArray.getJSONObject(item)

                    val listData = DailyData(jsonSingleDay.getInt("dt"),
                            jsonSingleDay.getInt("sunrise")+timezone,jsonSingleDay.getInt("sunset")+timezone,
                        jsonSingleDay.getInt("moonrise")+timezone,jsonSingleDay.getString("moon_phase"),
                        jsonSingleDay.getJSONObject("temp").getInt("day"),
                        jsonSingleDay.getJSONObject("temp").getInt("min"),
                        jsonSingleDay.getJSONObject("temp").getInt("max"),
                        jsonSingleDay.getJSONObject("temp").getInt("night"),
                        jsonSingleDay.getJSONObject("temp").getInt("eve"),
                        jsonSingleDay.getJSONObject("temp").getInt("morn"),
                        jsonSingleDay.getJSONObject("feels_like").getInt("day"),
                        jsonSingleDay.getJSONObject("feels_like").getInt("night"),
                        jsonSingleDay.getJSONObject("feels_like").getInt("eve"),
                        jsonSingleDay.getJSONObject("feels_like").getInt("morn"),
                        jsonSingleDay.getJSONArray("weather").getJSONObject(0).getString("description"),
                        jsonSingleDay.getJSONArray("weather").getJSONObject(0).getString("icon"),
                        jsonSingleDay.getString("pop"),jsonSingleDay.getString("clouds"),
                        jsonSingleDay.getString("uvi"))

                    adapter.addData(listData)
                }

            },
            { error->
                error.printStackTrace()
            })

        MySingleton(requireContext()).addToRequestQueue(jsonDailyObject)

        return dailyScreenView
    }
}