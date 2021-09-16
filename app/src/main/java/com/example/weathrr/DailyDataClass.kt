package com.example.weathrr

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.daily_list.view.*
import java.time.Month
import java.time.temporal.TemporalAccessor
import java.util.*

class DailyDataClass(private val dataList : MutableList<DailyData>)
    : RecyclerView.Adapter<DailyDataClass.DataViewHolder>() {

    class DataViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        return DataViewHolder(LayoutInflater.from(parent.context).inflate(
            R.layout.daily_list,parent,false))
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        val currentData = dataList[position]
        holder.itemView.apply {
            Picasso.get().load("https://openweathermap.org/img/wn/" + currentData.weatherIcon + "@2x.png")
                .into(weather_icon_daily)
            daily_min_max.text = resources.getString(R.string.daily_temp_min_max,currentData.maxTemp,currentData.minTemp)
            daily_description.text = currentData.weatherDesc
            cloud_percent.text = resources.getString(R.string.percent_string,currentData.clouds,"%")
            pop_percent.text = resources.getString(R.string.percent_string,currentData.pop,"%")

            daily_sunrise.text = calcTime(currentData.sunrise)
            morning_temp.text = resources.getString(R.string.current_temp_text,currentData.mornTemp)
            morning_feels_like.text = resources.getString(R.string.current_temp_text,currentData.flMorn)

            daily_aqi.text = currentData.uvi
            daily_afternoon.text = resources.getString(R.string.current_temp_text,currentData.dayTemp)
            afternoon_feels_like.text = resources.getString(R.string.current_temp_text,currentData.flDay)

            daily_sunset.text = calcTime(currentData.sunset)
            daily_evening.text = resources.getString(R.string.current_temp_text,currentData.eveTemp)
            evening_feels_like.text = resources.getString(R.string.current_temp_text,currentData.flEve)

            daily_moonrise.text = calcTime(currentData.moonrise)
            daily_night.text = resources.getString(R.string.current_temp_text,currentData.nightTemp)
            night_feels_like.text = resources.getString(R.string.current_temp_text,currentData.flNight)
            daily_moonphase.text = currentData.moonphase

            val calendar = Calendar.getInstance()
            val date = Date(currentData.time*1000L)

            calendar.time = date
            month_text.text = resources.getString(R.string.date,setDay(calendar.get(Calendar.DAY_OF_WEEK)),
                calendar.get(Calendar.DAY_OF_MONTH),setMonth(calendar.get(Calendar.MONTH)))
        }
    }

    private fun setDay(no : Int) : String{
        when(no){
            1-> return "Sunday"
            2-> return "Monday"
            3-> return "Tuesday"
            4-> return "Wednesday"
            5-> return "Thursday"
            6-> return "Friday"
            7-> return "Saturday"
        }
        return ""
    }

    private fun setMonth(no : Int) : String {
        when(no){
            0-> return "Jan"
            1-> return "Feb"
            2-> return "Mar"
            3-> return "Apr"
            4-> return "May"
            5-> return "Jun"
            6-> return "Jul"
            7-> return "Aug"
            8-> return "Sept"
            9-> return "Oct"
            10-> return "Nov"
            11-> return "Dec"
        }
        return  ""
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun addData(data : DailyData){
        dataList.add(data)
        notifyItemInserted(dataList.size-1)
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

        if(Hour%12 == 0){
            if(Hour/12 == 0)
                time = "12" + ":" + Min + " A.M."
            else
                time = Hour.toString() + ":" + Min + " P.M."
        }

        else if(Hour%12 <10){

            if(Hour/12 == 0)
                time = "0" + Hour.toString() + ":" + Min + " A.M."
            else {
                Hour %= 12
                time = "0" + Hour.toString() + ":" + Min + " P.M."
            }
        }

        else if(Hour%12 == 10 || Hour%12 == 11){

            if(Hour/12 == 0)
                time = Hour.toString() + ":" + Min + " A.M."

            else{
                Hour %= 12
                time = Hour.toString() + ":" + Min + " P.M."
            }
        }

        return time
    }


}