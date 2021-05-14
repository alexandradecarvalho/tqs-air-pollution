package tqs.midterm.project.repository;

import org.junit.jupiter.api.Test;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.springframework.beans.factory.annotation.Autowired;
import tqs.midterm.project.model.City;

import static org.assertj.core.api.Assertions.assertThat;


public class CityDAOTest {
    private static  final long CITY_ID = 2732265L;

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

    @Test
    public void findNameByValidCoordinates() throws Exception {
        assertThat(cityDAO.getName(41.14961, -8.61099), is("Porto"));
    }

    @Test
    public void findNameByInvalidCoordinates() throws Exception {
        assertThat(cityDAO.getName(111111, 11111), is("Undefined"));
    }

    @Test
    public void findCoordinatesByValidName() throws Exception {
        assertThat(cityDAO.getCoordinates("Porto"), is(new Double[]{41.1496, -8.611}));
    }

    @Test
    public void findCoordinatesByInvalidName() throws Exception {
        assertThat(cityDAO.getCoordinates("Alexa"), is(new Double[]{0.0, 0.0}));
    }
}