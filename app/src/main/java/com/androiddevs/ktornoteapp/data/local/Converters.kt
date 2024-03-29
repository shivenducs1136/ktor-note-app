package com.androiddevs.ktornoteapp.data.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {

    @TypeConverter
    fun fromList(s:List<String>):String{
        return Gson().toJson(s)
    }

    @TypeConverter
    fun toList(string :String):List<String>{
        return Gson().fromJson(string,object :TypeToken<List<String>>() {}.type)
    }

}