package com.androiddevs.exxx

data class Data(
    val app_temp: Double? = 0.0,
    val snow: Int? = 0,
    val solar_rad: Int? = 0,
    val sources: List<String>? = listOf(),
    val weather: WeatherX? = WeatherX(),
    val wind_cdir: String? = "",
    val wind_cdir_full: String? = "",
    val wind_dir: Int? = 0,
    val wind_spd: Int? = 0
)