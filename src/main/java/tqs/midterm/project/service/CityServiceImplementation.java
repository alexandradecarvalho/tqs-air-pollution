package tqs.midterm.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tqs.midterm.project.model.City;
import tqs.midterm.project.repository.CityDAO;

import javax.transaction.Transactional;
import java.util.List;

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

}
