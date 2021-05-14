package tqs.midterm.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import tqs.midterm.project.cache.Cache;
import tqs.midterm.project.model.City;
import tqs.midterm.project.repository.CityDAOImplementation;
import tqs.midterm.project.service.CityService;

import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api")
public class RESTController {
    @Autowired
    private CityService cityService;
    private static final Logger logger = Logger.getLogger(RESTController.class.getName());

    @GetMapping("/search")
    public String get(@RequestParam(value = "cityname", defaultValue = "Porto")String cityname) throws Exception {
        logger.log(Level.WARNING, cityname + " recieved by rest controller");
        City answer = cityService.getCityByName(cityname);
        if(answer != null)
            return "{" + "city=" + answer.getName() + "," + answer.getCountry() + "&coordinates=(" + answer.getLatitude() + "," + answer.getLongitude() + ")" + "&pollutants=" + "{co=" + answer.getQuality().getCo() + ",no2=" + answer.getQuality().getNo2() + ",o3=" + answer.getQuality().getO3() + ",so2=" + answer.getQuality().getSo2() + ",pm10=" + answer.getQuality().getPm10() + ",pm25=" + answer.getQuality().getPm25() + "}}\n";
        return "null\n";
    }

    @GetMapping("/hourly")
    public String getCoords(@RequestParam(value = "latitude", defaultValue = "41.1496")double latitude, @RequestParam(value = "longitude", defaultValue = "-8.611")double longitude, @RequestParam(value = "year", defaultValue = "2021")int year, @RequestParam(value = "month", defaultValue = "01")int month, @RequestParam(value = "day", defaultValue = "14")int day) throws Exception {
        // avoid sending blatantly wrong values
        if(month > 12){
             throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"The month parameter specified: " + month + " - is invalid");
         }
         if(day > 31){
             throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"The day parameter specified: " + day + " - is invalid");
         }

        City answer = cityService.getCityByCoordinatesAndDay(latitude, longitude, year, month, day);
         if(answer!= null)
            return "{" + "city=" + answer.getName() + "," + answer.getCountry() + "&coordinates=(" + answer.getLatitude() + "," + answer.getLongitude() + ")"  + "&day=(" + year + "-" + month + "-" + day + ")" + "&pollutants=" + "{co=" + answer.getQuality().getCo() + ",no2=" + answer.getQuality().getNo2() + ",o3=" + answer.getQuality().getO3() + ",so2=" + answer.getQuality().getSo2() + ",pm10=" + answer.getQuality().getPm10() + ",pm25=" + answer.getQuality().getPm25() + "}}\n";
         return "null\n";
    }

    @GetMapping("/cache")
    public String getCacheStats() throws Exception {
        Cache answer = cityService.getCacheStats();
        return "{requests=" + answer.getNumberOfRequests() + "&hits=" + answer.getNumberOfHits() + "&misses=" + answer.getNumberOfMisses() + "&ratio=" + answer.getRatio() + "}\n";
    }

}
