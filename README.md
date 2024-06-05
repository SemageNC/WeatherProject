Overview
This project is a simple weather application for Android that displays current weather information for the user's current location. The app shows the current latitude and longitude, reverse geo-coded address, current system time, temperature, humidity, and weather description. The weather data is fetched from the OpenWeatherMap API and updated every minute.

Features
Display current latitude and longitude of the user's location.
Show reverse geo-coded address of the user's location.
Display the current system time, updated every second.
Fetch and display current weather information including temperature, humidity, and weather description.
Weather information is updated every minute.
Prerequisites
Android Studio
OpenWeatherMap API Key
Setup Instructions
Clone the Repository

bash
Copy code
git clone <repository-url>
Open the Project in Android Studio

Open Android Studio and select "Open an existing Android Studio project". Navigate to the cloned repository and open it.

Add Your OpenWeatherMap API Key

In MainActivity.java, replace YOUR_API_KEY with your actual OpenWeatherMap API key.

java
Copy code
private static final String API_KEY = "YOUR_API_KEY"; // Your OpenWeatherMap API key
Ensure Correct Permissions

Make sure the following permissions are added in your AndroidManifest.xml file:

xml
Copy code
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
<uses-permission android:name="android.permission.INTERNET"/>
Run the Project

Connect your Android device or start an emulator. Click on the "Run" button in Android Studio to build and run the application on your device.

Project Structure
MainActivity.java: The main activity that handles fetching and displaying location and weather data.
activity_main.xml: The layout file for the main activity, including TextViews and CardViews for displaying information.
network: Package containing Retrofit client setup and API response classes.
Additional Features Implemented
Real-Time System Time: The current system time is updated every second.
Periodic Weather Updates: The weather information is updated every minute.
Detailed Logging: Detailed logging is implemented to help with debugging API responses and failures.
Screenshots
Include some screenshots of the application running on your device to give users a visual overview of the app.
