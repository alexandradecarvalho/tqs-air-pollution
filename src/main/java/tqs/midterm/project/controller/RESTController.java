package tqs.midterm.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tqs.midterm.project.model.City;
import tqs.midterm.project.service.CityService;

@RestController
@RequestMapping("/")
public class RESTController {
    @Autowired
    private CityService cityService;

    @GetMapping("/search/{cityname}")
    public City get(@RequestParam(value = "cityname", defaultValue = "Porto")String cityname, Model model) throws Exception {
        return cityService.getCityByName(cityname);
    }

    @GetMapping("/coords/{latitude}&{longitude}")
    public City getCoords(@RequestParam(value = "latitude", defaultValue = "41.1496")double latitude, @RequestParam(value = "longitude", defaultValue = "-8.611")double longitude, Model model) throws Exception {
        return cityService.getCityByCoordinates(latitude, longitude);
    }
}
// TODO : log the API