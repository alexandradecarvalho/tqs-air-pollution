package tqs.midterm.project.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class CityDAOConverterTest {

    @Autowired
    private CityDAOImplementation cityDAO = new CityDAOImplementation();

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
