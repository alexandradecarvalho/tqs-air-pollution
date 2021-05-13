package tqs.midterm.project.service;

import tqs.midterm.project.cache.Cache;
import tqs.midterm.project.model.City;

public interface CityService {
    City getCityByName(String name) throws Exception;

    City getCityByCoordinates(double latitude, double longitude) throws Exception;

    City getCityByCoordinatesAndDay(double latitude, double longitude, int year, int month, int day) throws Exception;

    Cache getCacheStats() throws Exception;
}
