package tqs.midterm.project.repository;

import tqs.midterm.project.cache.Cache;
import tqs.midterm.project.model.City;

public interface CityDAO {
    City findByName(String name) throws Exception;

    City findByCoordinates(double latitude, double longitude) throws Exception;

    City findByCoordinatesAndDate(double latitude, double longitude, int year, int month, int day) throws Exception;

    Cache findCacheStats() throws Exception;
}
