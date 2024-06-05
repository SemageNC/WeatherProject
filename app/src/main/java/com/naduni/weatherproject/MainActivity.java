package com.naduni.weatherproject;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.naduni.weatherproject.network.RetrofitClient;
import com.naduni.weatherproject.network.WeatherResponse;
import com.naduni.weatherproject.network.WeatherService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final String TAG = "MainActivity";
    private static final String API_KEY = "9b42abb37074595ab4d922c1ae3c6786"; // Your OpenWeatherMap API key
    private FusedLocationProviderClient fusedLocationClient;
    private TextView latitudeLongitudeTextView;
    private TextView addressTextView;
    private TextView systemTimeTextView;
    private TextView weatherInfoTextView;

    private TextView temperatureInfoTextView;
    private Handler handler;
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        latitudeLongitudeTextView = findViewById(R.id.latitude_longitude);
        addressTextView = findViewById(R.id.address);
        systemTimeTextView = findViewById(R.id.system_time);
        weatherInfoTextView = findViewById(R.id.weather_info);
        temperatureInfoTextView = findViewById(R.id.temperature_info);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        handler = new Handler(Looper.getMainLooper());

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            fetchLocation();
        }

        // Update system time in real-time
        updateSystemTime();

        // Schedule weather data updates every minute
        scheduleWeatherUpdates();
    }

    private void updateSystemTime() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                String currentTime = sdf.format(new Date());
                systemTimeTextView.setText(currentTime);
                handler.postDelayed(this, 1000); // Update every second
            }
        });
    }

    private void fetchLocation() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (locationResult == null) {
                    Log.d(TAG, "LocationResult is null");
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        latitudeLongitudeTextView.setText(String.format(Locale.getDefault(), "Lat: %.5f, Lon: %.5f", latitude, longitude));
                        Log.d(TAG, "Location: " + latitude + ", " + longitude);

                        // Reverse geocode to get address
                        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                        try {
                            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                            if (addresses != null && !addresses.isEmpty()) {
                                Address address = addresses.get(0);
                                addressTextView.setText(address.getAddressLine(0));
                                Log.d(TAG, "Address: " + address.getAddressLine(0));
                            } else {
                                addressTextView.setText("Unable to get address");
                                Log.d(TAG, "No address found");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            addressTextView.setText("Geocoder failed");
                            Log.e(TAG, "Geocoder failed", e);
                        }

                        // Fetch weather information
                        fetchWeatherInfo(latitude, longitude);
                    } else {
                        Log.d(TAG, "Location is null");
                    }
                }
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private void fetchWeatherInfo(double latitude, double longitude) {
        WeatherService weatherService = RetrofitClient.getClient().create(WeatherService.class);
        Call<WeatherResponse> call = weatherService.getWeather(latitude, longitude, API_KEY, "metric");
        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse weatherResponse = response.body();
                    String weatherInfo = String.format(Locale.getDefault(),
                            "Temp: %.1f°C\nHumidity: %d%%\nDescription: %s",
                            weatherResponse.main.temp,
                            weatherResponse.main.humidity,
                            weatherResponse.weather.get(0).description);
                    weatherInfoTextView.setText(weatherInfo);
                    String temperatureInfo = String.format(Locale.getDefault(), "%.1f°C", weatherResponse.main.temp);
                    temperatureInfoTextView.setText(temperatureInfo);
                    Log.d(TAG, "Weather Info: " + weatherInfo);
                } else {
                    weatherInfoTextView.setText("Failed to get weather info");
                    Log.d(TAG, "Response unsuccessful or body is null");
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                weatherInfoTextView.setText("Failed to get weather info");
                Log.e(TAG, "API call failed", t);
            }
        });
    }

    private void scheduleWeatherUpdates() {
        runnable = new Runnable() {
            @Override
            public void run() {
                fetchLocation(); // Update location and weather info
                handler.postDelayed(this, 60000); // Schedule next update in 1 minute
            }
        };
        handler.post(runnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchLocation();
            } else {
                // Permission denied
                latitudeLongitudeTextView.setText("Permission denied");
                addressTextView.setText("Permission denied");
                weatherInfoTextView.setText("Permission denied");
                Log.d(TAG, "Permission denied");
            }
        }
    }
}
