package tqs.midterm.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tqs.midterm.project.cache.Cache;
import tqs.midterm.project.model.City;
import tqs.midterm.project.repository.CityDAO;

import javax.transaction.Transactional;

@Service
public class CityServiceImplementation implements CityService{

    @Autowired
    private CityDAO cityDAO;

    @Transactional
    @Override
    public City getCityByName(String name) throws Exception {
        City result = cityDAO.findByName(name);
        return result;
    }

    @Override
    public City getCityByCoordinates(double latitude, double longitude) throws Exception {
        return cityDAO.findByCoordinates(latitude, longitude);
    }

    @Override
    public City getCityByCoordinatesAndDay(double latitude, double longitude, int year, int month, int day) throws Exception {
        return cityDAO.findByCoordinatesAndDate(latitude, longitude, year, month, day);
    }

    @Override
    public Cache getCacheStats() throws Exception {
        return cityDAO.findCacheStats();
    }

}
