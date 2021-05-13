package tqs.midterm.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import tqs.midterm.project.service.CityService;

@Controller
@RequestMapping("/")
public class CityController {

    @Autowired
    private CityService cityService;

    @GetMapping("/")
    public String index(){
        return "index";
    }
    @GetMapping("/search")
    public String get(@RequestParam(value = "cityname", defaultValue = "Porto")String cityname, Model model) throws Exception {
            model.addAttribute("city", cityService.getCityByName(cityname));
            return "results";
    }

    @GetMapping("/names")
    public String indexbyname(){
        return "indexbyname";
    }
    @GetMapping("/coords")
    public String getCoords(@RequestParam(value = "latitude", defaultValue = "41.1496")double latitude, @RequestParam(value = "longitude", defaultValue = "-8.611")double longitude, Model model) throws Exception {
        model.addAttribute("city", cityService.getCityByCoordinates(latitude, longitude));
        return "results";
    }


    @GetMapping("/error")
    public String getError(){
        return "error";
    }
}

