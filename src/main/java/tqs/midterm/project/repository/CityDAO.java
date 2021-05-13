package tqs.midterm.project.repository;

import tqs.midterm.project.model.City;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

public interface CityDAO {
    City findByName(String name) throws Exception;

    City findByCoordinates(double latitude, double longitude) throws Exception;
}
