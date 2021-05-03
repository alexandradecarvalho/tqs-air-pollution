package tqs.midterm.project.model;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.List;

public class CityDAOImplementation implements CityDAO{

    private static final String BASE_URL = "https://api.breezometer.com/air-quality/v2/current-conditions";
    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String KEY = "31b992ce630e4cb7a4157fb2f97b4a50";

    @Override
    public City findByLatitudeAndLongitude(float latitude, float longitude) {
        String request = BASE_URL + "?lat=" + latitude + "&lon=" + longitude + "&key=" + KEY + "&features=pollutants_concentrations";
        URL my_final_url =  new URL(request);

        HttpURLConnection con = (HttpURLConnection) my_final_url.openConnection();

        con.setRequestMethod("GET");

        con.setRequestProperty("User-Agent", USER_AGENT);

    }
}
