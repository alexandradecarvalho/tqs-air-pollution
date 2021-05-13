package tqs.midterm.project.service;

import tqs.midterm.project.model.City;

import java.util.List;

public interface CityService {
    City getCityByName(String name) throws Exception;

    City getCityByCoordinates(double latitude, double longitude) throws Exception;
}
