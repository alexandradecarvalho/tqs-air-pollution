package tqs.midterm.project.repository;

import tqs.midterm.project.model.City;

import java.io.IOException;
import java.util.List;

public interface CityDAO {
    City findByLatitudeAndLongitude(float latitude, float longitude, String name) throws Exception;
}
