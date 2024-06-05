package com.naduni.weatherproject.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherService {
    @GET("weather")
    Call<WeatherResponse> getWeather(@Query("lat") double lat,
                                     @Query("lon") double lon,
                                     @Query("appid") String apiKey,
                                     @Query("units") String units);
}
