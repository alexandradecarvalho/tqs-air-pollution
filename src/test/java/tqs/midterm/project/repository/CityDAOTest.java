package tqs.midterm.project.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import tqs.midterm.project.model.City;

import static org.assertj.core.api.Assertions.assertThat;


public class CityDAOTest {
    @Autowired
    private CityDAOImplementation cityDAO = new CityDAOImplementation();

    @Test
    public void findByValidName() throws Exception {
        assertThat(cityDAO.findByName("Porto")).isInstanceOf(City.class);
    }

    @Test
    public void findByInvalidName() throws Exception {
        assertThat(cityDAO.findByName("Alexa")).isNull();
    }

    @Test
    public void findByValidCoordinates() throws Exception {
        assertThat(cityDAO.findByCoordinates(41.14961, -8.61099)).isInstanceOf(City.class);
    }

    @Test
    public void findByInvalidCoordinates() throws Exception {
        assertThat(cityDAO.findByCoordinates(1111,1111)).isNull();
    }

    @Test
    public void findByValidCoordinatesAndValidDate() throws Exception {
        assertThat(cityDAO.findByCoordinatesAndDate(41.14961, -8.61099, 2021, 05, 04)).isNotNull();
    }

    @Test
    public void findByValidCoordinatesAndInvalidDate() throws Exception {
        assertThat(cityDAO.findByCoordinatesAndDate(41.14961, -8.61099, 2021, 05, 40)).isNull();
    }

    @Test
    public void findByInvalidCoordinatesAndValidDate() throws Exception {
        assertThat(cityDAO.findByCoordinatesAndDate(11111, 11111, 2021, 05, 04)).isNull();
    }

    @Test
    public void findByInvalidCoordinatesAndInvalidDate() throws Exception {
        assertThat(cityDAO.findByCoordinatesAndDate(11111, 11111, 2021, 05, 40)).isNull();
    }

}