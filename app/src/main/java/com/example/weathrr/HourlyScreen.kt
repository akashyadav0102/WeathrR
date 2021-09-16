package com.example.weathrr

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import kotlinx.android.synthetic.main.fragment_hourly_screen.view.*

class HourlyScreen : Fragment() {

    private lateinit var adapter: DataAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        adapter = DataAdapter(mutableListOf())

        val bundle = this.arguments
        val latitude = bundle?.getString("LATITUDE")
        val longitude = bundle?.getString("LONGITUDE")

        val hourlyScreenView : View = inflater.inflate(R.layout.fragment_hourly_screen,container,false)

        hourlyScreenView.recycler_view.adapter = adapter
        hourlyScreenView.recycler_view.layoutManager = LinearLayoutManager(requireContext())

        val url = "https://pro.openweathermap.org/data/2.5/onecall?lat=" +  latitude + "&lon=" +
                longitude + "&exclude=current,minutely,daily,alerts" + "&appid=" + getString(R.string.api_key) + "&units=metric"

        val jsonHourlyRequest = JsonObjectRequest(Request.Method.GET,url, null,
            {response ->
                val jsonMainArray = response.getJSONArray("hourly")

                val timeZone = response.getInt("timezone_offset")

                for(item in 0 until jsonMainArray.length()){
                    val jsonHourData = jsonMainArray.getJSONObject(item)

                    val listData = HourlyData(jsonHourData.getInt("dt") + timeZone,jsonHourData.getInt("temp"),
                        jsonHourData.getInt("feels_like"),
                        jsonHourData.getJSONArray("weather").getJSONObject(0).getString("icon"),
                        jsonHourData.getDouble("pop"))

                    adapter.addList(listData)
                }
            },
            {error->
                error.printStackTrace()
            })

        MySingleton.getInstance(requireContext()).addToRequestQueue(jsonHourlyRequest)

        return hourlyScreenView
    }

}