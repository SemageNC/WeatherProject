package com.naduni.weatherproject.network;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class WeatherResponse {
    @SerializedName("main")
    public Main main;
    @SerializedName("weather")
    public List<Weather> weather;
    @SerializedName("name")
    public String name;

    public class Main {
        @SerializedName("temp")
        public float temp;
        @SerializedName("humidity")
        public int humidity;
    }

    public class Weather {
        @SerializedName("description")
        public String description;
    }
}
