package tqs.midterm.project.repository;

import org.json.JSONException;
import tqs.midterm.project.model.AirQuality;
import tqs.midterm.project.model.City;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import org.json.JSONObject;

public class CityDAOImplementation implements CityDAO {

    private static final String BASE_URL = "https://api.breezometer.com/air-quality/v2/current-conditions"; // access url
    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String KEY = "31b992ce630e4cb7a4157fb2f97b4a50"; // access token

    @Override
    public City findByLatitudeAndLongitude(float latitude, float longitude, String name) throws IOException, JSONException {
        String request = BASE_URL + "?lat=" + latitude + "&lon=" + longitude + "&key=" + KEY + "&features=pollutants_concentrations&metadata=true"; // create complete request url

        URL my_final_url = new URL(request);
        HttpURLConnection con = (HttpURLConnection) my_final_url.openConnection(); // open HTTP connection

        con.setRequestMethod("GET"); // since we are using the api, our request method is a "GET"
        con.setRequestProperty("User-Agent", USER_AGENT);

        if (con.getResponseCode() == HttpURLConnection.HTTP_OK) { // success
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
            City newcity = new City(latitude, longitude, name, country, new AirQuality(co, 03, no2, so2, pm10, pm25));
            return newcity;
        }
        return null;
    }
}