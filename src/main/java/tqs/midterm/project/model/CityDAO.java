package tqs.midterm.project.model;

import java.util.List;

public interface CityDAO {
    City findByLatitudeAndLongitude(float latitude, float longitude);
}
