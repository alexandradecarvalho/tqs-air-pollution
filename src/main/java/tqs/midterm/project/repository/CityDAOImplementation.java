package tqs.midterm.project.repository;

import org.json.JSONArray;
import org.springframework.stereotype.Repository;
import tqs.midterm.project.cache.Cache;
import tqs.midterm.project.model.AirQuality;
import tqs.midterm.project.model.City;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;

@Repository
public class CityDAOImplementation implements CityDAO {
    private static final Logger logger = Logger.getLogger(CityDAOImplementation.class.getName());
    private static final String BASE_URL = "https://api.breezometer.com/air-quality/v2/current-conditions"; // access url
    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String KEY = "31b992ce630e4cb7a4157fb2f97b4a50"; // access token
    private Cache cache = new Cache();

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
            try{
                Double latitude = jsonObj.getJSONObject(0).getDouble("lat");
                Double longitude = jsonObj.getJSONObject(0).getDouble("lon");
                return new Double[]{latitude, longitude};
            } catch (Exception e){

            }

        } else
            logger.log(Level.WARNING, "unsuccessful attempt to connect to openweather to get coordinates from name");

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
            try{
                Double latitude = jsonObj.getJSONObject("data").getJSONArray("results").getJSONObject(0).getDouble("latitude");
                Double longitude = jsonObj.getJSONObject("data").getJSONArray("results").getJSONObject(0).getDouble("longitude");
                return new Double[]{latitude, longitude};
            } catch (Exception e){

            }
        } else
            logger.log(Level.WARNING, "unsuccessful attempt to connect to positionstack to get coordinates from name");

        return new Double[]{0.0, 0.0};
    }

    @Override
    public City findByName(String name) throws Exception {
        Double[] coordinates = getCoordinates(name);
        double latitude = coordinates[0];
        double longitude = coordinates[1];

        City cachedcity = cache.get(latitude, longitude);
        if(cachedcity != null){
            return cachedcity;
        } else
            logger.log(Level.WARNING, "Cache miss in findbyname");

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
            try{
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
                City newcity = new City(latitude, longitude, name, country);
                newcity.setQuality(new AirQuality(co, o3, no2, so2, pm10, pm25));
                cache.save(newcity, System.currentTimeMillis());
                return newcity;
            }catch (Exception e){

            }
        } else
            logger.log(Level.WARNING, "unsuccessful attempt to connect to breezometer to search by name");

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
            try {
                JSONObject pollutants = jsonObj.getJSONArray("stations").getJSONObject(1);

                // get each pollutant concentration
                double co = pollutants.getDouble("CO");
                double no2 = pollutants.getDouble("NO2");
                double o3 = pollutants.getDouble("OZONE");
                double so2 = pollutants.getDouble("SO2");
                double pm25 = pollutants.getJSONObject("aqiInfo").getDouble("concentrartion");
                double pm10 = 0.0;

                // new City
                City newcity = new City(latitude, longitude, name, "Undefined");
                newcity.setQuality(new AirQuality(co, o3, no2, so2, pm10, pm25));
                cache.save(newcity, System.currentTimeMillis());
                return newcity;
            } catch (Exception e){

            }
                    } else
            logger.log(Level.WARNING, "unsuccessful attempt to connect to ambee to search by name");

        return null;
    }

    public String getName(double latitude, double longitude) throws Exception {
        String base = "http://api.openweathermap.org/geo/1.0/reverse?lat=" + latitude + "&lon=" + longitude + "&limit=1&appid=a00b1675e92f09504fd9f127bd0a056e";
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
            } else
                logger.log(Level.WARNING, "invalid return from openweather to get name of city");
        } else
            logger.log(Level.WARNING, "unsuccessful attempt to connect to openweather to get name of city");

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
                logger.log(Level.WARNING, "invalid result from positionstack to search by name");
            }
        } else
            logger.log(Level.WARNING, "unsuccessful attempt to connect to positionstack to search by name");

        return "Undefined";
    }

    @Override
    public City findByCoordinates(double latitude, double longitude) throws Exception {

        City cachedcity = cache.get(latitude, longitude);
        if(cachedcity != null){
            return cachedcity;
        } else{
            logger.log(Level.WARNING, "missed cache in findbycoords");
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
            try{
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
                City newcity = new City(latitude, longitude, name, country);
                newcity.setQuality(new AirQuality(co, o3, no2, so2, pm10, pm25));
                cache.save(newcity, System.currentTimeMillis());
                return newcity;
            } catch (Exception e){
            logger.log(Level.WARNING, "invalid result from positionstack to search by name");
        }
        } else
            logger.log(Level.WARNING, "unsuccessful attempt to connect to breezometer to search by coords");

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
            try{
                JSONObject pollutants = jsonObj.getJSONArray("stations").getJSONObject(1);

                // get each pollutant concentration
                double co = pollutants.getDouble("CO");
                double no2 = pollutants.getDouble("NO2");
                double o3 = pollutants.getDouble("OZONE");
                double so2 = pollutants.getDouble("SO2");
                double pm25 = pollutants.getJSONObject("aqiInfo").getDouble("concentration");
                double pm10 = 0.0;

                // new City
                String name = getName(latitude, longitude);
                City newcity = new City(latitude, longitude, name, "Undefined");
                newcity.setQuality(new AirQuality(co, o3, no2, so2, pm10, pm25));
                cache.save(newcity, System.currentTimeMillis());
                return newcity;
            } catch (Exception e){

            }
        } else
            logger.log(Level.WARNING, "unsuccessful attempt to connect to ambee to search by coordinates");

        return null;
    }

    @Override
    public City findByCoordinatesAndDate(double latitude, double longitude, int year, int month, int day) throws Exception {
        String request = "https://api.breezometer.com/air-quality/v2/historical/hourly?key="+ KEY + "&lat=" + latitude + "&lon=" + longitude + "&features=pollutants_concentrations&metadata=true&datetime=" + year + "-" + month + "-" + day + "T09:00:00"; // create complete request url
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
            try{
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
                City newcity = new City(latitude, longitude, name, country);
                newcity.setQuality(new AirQuality(co, o3, no2, so2, pm10, pm25));
                cache.save(newcity, System.currentTimeMillis());
                return newcity;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else
            logger.log(Level.WARNING, "unsuccessful attempt to connect to breezometer to search by history");


        request = "https://api.ambeedata.com/history/by-lat-lng?lat=" + latitude + "&lng=" + longitude + " " + year + "-" + month + "-" + day + " 09:00:00&to=" + year + "-" + month + "-" + day + " 21:00:00"; // create complete request url
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
            try{
                JSONObject pollutants = jsonObj.getJSONArray("stations").getJSONObject(1);

                // get each pollutant concentration
                double co = pollutants.getDouble("CO");
                double no2 = pollutants.getDouble("NO2");
                double o3 = pollutants.getDouble("OZONE");
                double so2 = pollutants.getDouble("SO2");
                double pm25 = pollutants.getJSONObject("aqiInfo").getDouble("concentrartion");
                double pm10 = 0.0;

                // new City
                String name = getName(latitude, longitude);
                City newcity = new City(latitude, longitude, name, "Undefined");
                newcity.setQuality(new AirQuality(co, o3, no2, so2, pm10, pm25));
                cache.save(newcity, System.currentTimeMillis());
                return newcity;
            } catch (Exception e){

            }
        } else
            logger.log(Level.WARNING, "unsuccessful attempt to connect to ambee to search by history");

        return null;
    }

    @Override
    public Cache findCacheStats(){
        return cache;
    }


}