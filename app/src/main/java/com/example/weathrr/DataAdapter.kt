package com.example.weathrr

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.hourly_list.view.*

class DataAdapter( private val dataList : MutableList<HourlyData>) : RecyclerView.Adapter<DataAdapter.DataViewHolder>() {

    class DataViewHolder(itemView : View) :RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        return DataViewHolder(LayoutInflater.from(parent.context).inflate(
            R.layout.hourly_list,parent,false))
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        val currentData = dataList[position]
        holder.itemView.apply {
            list_time.text = calcTime(currentData.hour)
            list_temperature.text = resources.getString(R.string.current_temp_text,currentData.temp)
            list_feels_like.text = resources.getString(R.string.feels_like_text,currentData.feels_like)

            val url = "https://openweathermap.org/img/wn/" + currentData.icon + "@2x.png"
            Picasso.get().load(url).into(list_icon)

            list_pop.text = resources.getString(R.string.percent_string,calcpercent(currentData.pop),"%")
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun addList(hourlyData: HourlyData){
        dataList.add(hourlyData)
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

    private fun calcpercent(per: Double): String {
        var percent = per
        percent *= 100

        return percent.toInt().toString()
    }

}