package com.example.weathrr

data class DailyData(val time : Int, val sunrise : Int,val sunset : Int,val moonrise: Int,val moonphase : String,
val dayTemp : Int, val minTemp : Int,val maxTemp : Int,val nightTemp : Int,val eveTemp : Int,
                     val mornTemp : Int,val flDay : Int,val flNight : Int,val flEve : Int,
                     val flMorn : Int, val weatherDesc : String, val weatherIcon : String,
val pop : String,val clouds : String, val uvi : String)