import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class WeatherApp {
    public static JSONObject getWeatherData(String locationName) {
        try {
            JSONArray locationData = getLocationData(locationName);

            if (locationData != null && locationData.size() > 0) {
                JSONObject location = (JSONObject) locationData.get(0);
                double latitude = (double) location.get("latitude");
                double longitude = (double) location.get("longitude");

                String urlString = "https://api.open-meteo.com/v1/forecast?" +
                        "latitude=" + latitude + "&longitude=" + longitude +
                        "&hourly=temperature_2m,relativehumidity_2m,weathercode,windspeed_10m&timezone=America%2FLos_Angeles";

                HttpURLConnection conn = fetchApiResponse(urlString);

                if (conn != null && conn.getResponseCode() == 200) {
                    try (InputStream inputStream = conn.getInputStream()) {
                        Scanner scanner = new Scanner(inputStream);
                        StringBuilder resultJson = new StringBuilder();
                        while (scanner.hasNext()) {
                            resultJson.append(scanner.nextLine());
                        }
                        scanner.close();

                        conn.disconnect();

                        JSONParser parser = new JSONParser();
                        JSONObject resultJsonObj = (JSONObject) parser.parse(resultJson.toString());

                        JSONObject hourly = (JSONObject) resultJsonObj.get("hourly");
                        JSONArray time = (JSONArray) hourly.get("time");
                        int index = findIndexOfCurrentTime(time);

                        JSONArray temperatureData = (JSONArray) hourly.get("temperature_2m");
                        double temperature = (double) temperatureData.get(index);

                        JSONArray weatherCode = (JSONArray) hourly.get("weathercode");
                        String weatherCondition = convertWeatherCode((long) weatherCode.get(index));

                        JSONArray relativeHumidity = (JSONArray) hourly.get("relativehumidity_2m");
                        long humidity = (long) relativeHumidity.get(index);

                        JSONArray windspeedData = (JSONArray) hourly.get("windspeed_10m");
                        double windspeed = (double) windspeedData.get(index);

                        JSONObject weatherData = new JSONObject();
                        weatherData.put("temperature", temperature);
                        weatherData.put("weather_condition", weatherCondition);
                        weatherData.put("humidity", humidity);
                        weatherData.put("windspeed", windspeed);

                        return weatherData;
                    }
                } else {
                    System.out.println("Error: Could not connect to API. Response code: " + conn.getResponseCode());
                }
            } else {
                System.out.println("Error: Location data is missing or empty.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static JSONArray getLocationData(String locationName) {
        JSONArray locationData = null;
        locationName = locationName.replaceAll(" ", "+");

        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name=" +
                locationName + "&count=10&language=en&format=json";

        try {
            HttpURLConnection conn = fetchApiResponse(urlString);

            if (conn != null && conn.getResponseCode() == 200) {
                try (InputStream inputStream = conn.getInputStream()) {
                    Scanner scanner = new Scanner(inputStream);
                    StringBuilder resultJson = new StringBuilder();
                    while (scanner.hasNext()) {
                        resultJson.append(scanner.nextLine());
                    }
                    scanner.close();

                    conn.disconnect();

                    JSONParser parser = new JSONParser();
                    JSONObject resultJsonObj = (JSONObject) parser.parse(resultJson.toString());

                    // Check if the "result" array exists in the JSON response
                    if (resultJsonObj.containsKey("results")) {
                        locationData = (JSONArray) resultJsonObj.get("results");
                    } else {
                        System.out.println("Error: Location data is missing in the API response.");
                    }
                }
            } else {
                System.out.println("Error: Could not connect to API. Response code: " + conn.getResponseCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return locationData;
    }

    private static HttpURLConnection fetchApiResponse(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            return conn;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static int findIndexOfCurrentTime(JSONArray timeList) {
        String currentTime = getCurrentTime();

        for (int i = 0; i < timeList.size(); i++) {
            String time = (String) timeList.get(i);
            if (time.equalsIgnoreCase(currentTime)) {
                return i;
            }
        }

        return 0;
    }

    public static String getCurrentTime() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

        return currentDateTime.format(formatter);
    }

    private static String convertWeatherCode(long weatherCode) {
        String weatherCondition = "";
        if (weatherCode == 0L) {
            weatherCondition = "Clear";
        } else if (weatherCode <= 3L && weatherCode > 0L) {
            weatherCondition = "Cloudy";
        } else if ((weatherCode >= 51L && weatherCode <= 67L) || (weatherCode >= 80L && weatherCode <= 99L)) {
            weatherCondition = "Rain";
        } else if (weatherCode >= 71L && weatherCode <= 77L) {
            weatherCondition = "Snow";
        }
        return weatherCondition;
    }
}
