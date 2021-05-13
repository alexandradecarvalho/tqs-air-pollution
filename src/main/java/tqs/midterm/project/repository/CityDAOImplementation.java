package tqs.midterm.project.repository;

import org.json.JSONArray;
import org.springframework.stereotype.Repository;
import tqs.midterm.project.model.AirQuality;
import tqs.midterm.project.model.City;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.*;
import java.util.HashMap;

import org.json.JSONObject;

@Repository
public class CityDAOImplementation implements CityDAO {
    private static long CACHE_TTL = 3600000;
    private static final String BASE_URL = "https://api.breezometer.com/air-quality/v2/current-conditions"; // access url
    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String KEY = "31b992ce630e4cb7a4157fb2f97b4a50"; // access token
    private HashMap<City, Long> cache = new HashMap<>();

    // TODO : implement cache logging: count of requests and hit and miss
    // TODO : implement full event and actions logging

    public Double[] getCoordinates(String name) throws Exception {
        String base = "http://api.openweathermap.org/geo/1.0/direct?q=" + name + "&limit=1&appid=ed8382a331f5f8f2f5573254e1031f89";
        URL my_final_url = new URL(base);
        HttpURLConnection con = (HttpURLConnection) my_final_url.openConnection(); // open HTTP connection

        con.setRequestMethod("GET"); // since we are using the api, our request method is a "GET"
        con.setRequestProperty("User-Agent", USER_AGENT);

        if (con.getResponseCode() == HttpURLConnection.HTTP_OK){ // success with openweather
            // read response
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // parse response to JSON
            JSONArray jsonObj = new JSONArray(response.toString());

            Double latitude = jsonObj.getJSONObject(0).getDouble("lat");
            Double longitude = jsonObj.getJSONObject(0).getDouble("lon");
            return new Double[]{Double.valueOf(latitude), Double.valueOf(longitude)};
        }

        base = "https://api.positionstack.com/v1/forward?access_key=3ec2e7b179eacbbcfab405aff27b3c8e&query=" + name;

        my_final_url = new URL(base);
        con = (HttpURLConnection) my_final_url.openConnection(); // open HTTP connection

        con.setRequestMethod("GET"); // since we are using the api, our request method is a "GET"
        con.setRequestProperty("User-Agent", USER_AGENT);

        if (con.getResponseCode() == HttpURLConnection.HTTP_OK){ // success with positionstack
            // read response
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // parse response to JSON
            JSONObject jsonObj = new JSONObject(response.toString());
            Double latitude = jsonObj.getJSONObject("data").getJSONArray("results").getJSONObject(0).getDouble("latitude");
            Double longitude = jsonObj.getJSONObject("data").getJSONArray("results").getJSONObject(0).getDouble("longitude");
            return new Double[]{Double.valueOf(latitude), Double.valueOf(longitude)};
        }

        return new Double[]{Double.valueOf(0.0), Double.valueOf(0.0)};
    }

    @Override
    public City findByName(String name) throws Exception {
        Double[] coordinates = getCoordinates(name);
        double latitude = coordinates[0];
        double longitude = coordinates[1];

        for(City c : cache.keySet()){
            if(System.currentTimeMillis() - cache.get(c) > CACHE_TTL){
                cache.remove(c);
            } else{
                if(c.getLatitude() == latitude && c.getLongitude() == longitude){
                    return c;
                }
            }
        }

        String request = BASE_URL + "?lat=" + latitude + "&lon=" + longitude + "&key=" + KEY + "&features=pollutants_concentrations&metadata=true"; // create complete request url
        URL my_final_url = new URL(request);
        HttpURLConnection con = (HttpURLConnection) my_final_url.openConnection(); // open HTTP connection

        con.setRequestMethod("GET"); // since we are using the api, our request method is a "GET"
        con.setRequestProperty("User-Agent", USER_AGENT);

        if (con.getResponseCode() == HttpURLConnection.HTTP_OK) { // success with breezometer
            // read response
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // parse response to JSON
            JSONObject jsonObj = new JSONObject(response.toString());
            JSONObject pollutants = jsonObj.getJSONObject("data").getJSONObject("pollutants");

            // get each pollutant concentration
            double co = pollutants.getJSONObject("co").getJSONObject("concentration").getDouble("value");
            double no2 = pollutants.getJSONObject("no2").getJSONObject("concentration").getDouble("value");
            double o3 = pollutants.getJSONObject("o3").getJSONObject("concentration").getDouble("value");
            double so2 = pollutants.getJSONObject("so2").getJSONObject("concentration").getDouble("value");
            double pm25 = pollutants.getJSONObject("pm25").getJSONObject("concentration").getDouble("value");
            double pm10 = pollutants.getJSONObject("pm10").getJSONObject("concentration").getDouble("value");

            // get country from metadata
            String country = jsonObj.getJSONObject("metadata").getJSONObject("location").getString("country");

            // new City
            City newcity = new City((float)latitude, (float)longitude, name, country);
            newcity.setQuality(new AirQuality(co, 03, no2, so2, pm10, pm25));
            cache.put(newcity, System.currentTimeMillis());
            return newcity;
        }

        request = "https://api.ambeedata.com/latest/by-lat-lng?lat=" + latitude + "&lng=" + longitude; // create complete request url
        my_final_url = new URL(request);
        con = (HttpURLConnection) my_final_url.openConnection(); // open HTTP connection

        con.setRequestMethod("GET"); // since we are using the api, our request method is a "GET"
        con.setRequestProperty("User-Agent", USER_AGENT);

        if (con.getResponseCode() == HttpURLConnection.HTTP_OK) { // success with ambee
            // read response
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // parse response to JSON
            JSONObject jsonObj = new JSONObject(response.toString());
            JSONObject pollutants = jsonObj.getJSONArray("stations").getJSONObject(1);

            // get each pollutant concentration
            double co = pollutants.getDouble("CO");
            double no2 = pollutants.getDouble("NO2");
            double o3 = pollutants.getDouble("OZONE");
            double so2 = pollutants.getDouble("SO2");
            double pm25 = pollutants.getJSONObject("aqiInfo").getDouble("concentrartion");
            double pm10 = Double.valueOf(0.0);

            // new City
            City newcity = new City((float)latitude, (float)longitude, name, "Undefined");
            newcity.setQuality(new AirQuality(co, 03, no2, so2, pm10, pm25));
            cache.put(newcity, System.currentTimeMillis());
            return newcity;
        }

        return null;
    }

    public String getName(double longitude, double latitude) throws Exception {
        String base = "http://api.openweathermap.org/geo/1.0/reverse?lat=" + latitude + "&lon=" + longitude + "&limit=1&appid=ed8382a331f5f8f2f5573254e1031f89";
        URL my_final_url = new URL(base);
        HttpURLConnection con = (HttpURLConnection) my_final_url.openConnection(); // open HTTP connection

        con.setRequestMethod("GET"); // since we are using the api, our request method is a "GET"
        con.setRequestProperty("User-Agent", USER_AGENT);

        if (con.getResponseCode() == HttpURLConnection.HTTP_OK) { // success with openweather
            // read response
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // parse response to JSON
            JSONArray jsonObj = new JSONArray(response.toString());
            if(jsonObj.length() >= 1){
                return jsonObj.getJSONObject(0).getString("name");
            }
        }

        base = "http://api.positionstack.com/v1/reverse?access_key=3ec2e7b179eacbbcfab405aff27b3c8e&query=" + latitude + "," + longitude;

        my_final_url = new URL(base);
        con = (HttpURLConnection) my_final_url.openConnection(); // open HTTP connection

        con.setRequestMethod("GET"); // since we are using the api, our request method is a "GET"
        con.setRequestProperty("User-Agent", USER_AGENT);

        if (con.getResponseCode() == HttpURLConnection.HTTP_OK) { // success with positionstack
            // read response
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // parse response to JSON
            JSONObject jsonObj = new JSONObject(response.toString());
            try {
                return jsonObj.getJSONObject("data").getJSONArray("results").getJSONObject(0).getString("region");
            } catch (Exception e){

            }
        }

        return "Undefined";
    }


    @Override
    public City findByCoordinates(double latitude, double longitude) throws Exception {

        for(City c : cache.keySet()){
            if(System.currentTimeMillis() - cache.get(c) > CACHE_TTL){
                cache.remove(c);
            } else{
                if(c.getLatitude() == latitude && c.getLongitude() == longitude){
                    return c;
                }
            }
        }
        String request = BASE_URL + "?lat=" + latitude + "&lon=" + longitude + "&key=" + KEY + "&features=pollutants_concentrations&metadata=true"; // create complete request url
        URL my_final_url = new URL(request);
        HttpURLConnection con = (HttpURLConnection) my_final_url.openConnection(); // open HTTP connection

        con.setRequestMethod("GET"); // since we are using the api, our request method is a "GET"
        con.setRequestProperty("User-Agent", USER_AGENT);

        if (con.getResponseCode() == HttpURLConnection.HTTP_OK) { // success with breezometer
            // read response
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // parse response to JSON
            JSONObject jsonObj = new JSONObject(response.toString());
            JSONObject pollutants = jsonObj.getJSONObject("data").getJSONObject("pollutants");

            // get each pollutant concentration
            double co = pollutants.getJSONObject("co").getJSONObject("concentration").getDouble("value");
            double no2 = pollutants.getJSONObject("no2").getJSONObject("concentration").getDouble("value");
            double o3 = pollutants.getJSONObject("o3").getJSONObject("concentration").getDouble("value");
            double so2 = pollutants.getJSONObject("so2").getJSONObject("concentration").getDouble("value");
            double pm25 = pollutants.getJSONObject("pm25").getJSONObject("concentration").getDouble("value");
            double pm10 = pollutants.getJSONObject("pm10").getJSONObject("concentration").getDouble("value");

            // get country from metadata
            String country = jsonObj.getJSONObject("metadata").getJSONObject("location").getString("country");

            // new City
            String name = getName(latitude, longitude);
            City newcity = new City((float)latitude, (float)longitude, name, country);
            newcity.setQuality(new AirQuality(co, 03, no2, so2, pm10, pm25));
            cache.put(newcity, System.currentTimeMillis());
            return newcity;
        }

        request = "https://api.ambeedata.com/latest/by-lat-lng?lat=" + latitude + "&lng=" + longitude; // create complete request url
        my_final_url = new URL(request);
        con = (HttpURLConnection) my_final_url.openConnection(); // open HTTP connection

        con.setRequestMethod("GET"); // since we are using the api, our request method is a "GET"
        con.setRequestProperty("User-Agent", USER_AGENT);

        if (con.getResponseCode() == HttpURLConnection.HTTP_OK) { // success with ambee
            // read response
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // parse response to JSON
            JSONObject jsonObj = new JSONObject(response.toString());
            JSONObject pollutants = jsonObj.getJSONArray("stations").getJSONObject(1);

            // get each pollutant concentration
            double co = pollutants.getDouble("CO");
            double no2 = pollutants.getDouble("NO2");
            double o3 = pollutants.getDouble("OZONE");
            double so2 = pollutants.getDouble("SO2");
            double pm25 = pollutants.getJSONObject("aqiInfo").getDouble("concentrartion");
            double pm10 = Double.valueOf(0.0);

            // new City
            String name = getName(latitude, longitude);
            City newcity = new City((float)latitude, (float)longitude, name, "Undefined");
            newcity.setQuality(new AirQuality(co, 03, no2, so2, pm10, pm25));
            cache.put(newcity, System.currentTimeMillis());
            return newcity;
        }

        return null;
    }
}