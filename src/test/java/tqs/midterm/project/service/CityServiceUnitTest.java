package tqs.midterm.project.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import tqs.midterm.project.model.City;
import tqs.midterm.project.repository.CityDAOImplementation;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class CityServiceUnitTest {
    @Mock(lenient = true)
    private CityDAOImplementation cityDAO;

    @InjectMocks
    private CityServiceImplementation cityService;

    @BeforeEach
    public void setUp() throws Exception {
        City aveiro = new City(40.6443, -8.6455, "Aveiro", "Portugal");

        Mockito.when(cityDAO.findByName(aveiro.getName())).thenReturn(aveiro);
        Mockito.when(cityDAO.findByName("wrong_name")).thenReturn(null);
        Mockito.when(cityDAO.findByCoordinates(aveiro.getLatitude(), aveiro.getLongitude())).thenReturn(aveiro);
        Mockito.when(cityDAO.findByCoordinates(11111, 11111)).thenReturn(null);
        Mockito.when(cityDAO.findByCoordinatesAndDate(aveiro.getLatitude(), aveiro.getLongitude(), 2021, 05, 05)).thenReturn(aveiro);
        Mockito.when(cityDAO.findByCoordinatesAndDate(11111, 11111, 2021, 05, 05)).thenReturn(null);
        Mockito.when(cityDAO.findByCoordinatesAndDate(aveiro.getLatitude(), aveiro.getLongitude(), 2021, 05, 50)).thenReturn(null);
        Mockito.when(cityDAO.findByCoordinatesAndDate(11111, 11111, 2021, 05, 50)).thenReturn(null);
    }

    @Test
    public void findByValidName() throws Exception {
        assertThat(cityService.getCityByName("Aveiro").getName()).isEqualTo("Aveiro");
    }

    @Test
    public void findByInvalidName() throws Exception {
        assertThat(cityService.getCityByName("wrong_name")).isNull();
    }

    @Test
    public void findByValidCoordinates() throws Exception {
        assertThat(cityService.getCityByCoordinates(40.6443, -8.6455).getName()).isEqualTo("Aveiro");
    }

    @Test
    public void findByInvalidCoordinates() throws Exception {
        assertThat(cityService.getCityByCoordinates(11111, 11111)).isNull();
    }

    @Test
    public void findByValidDate() throws Exception {
        assertThat(cityService.getCityByCoordinatesAndDay(40.6443, -8.6455, 2021, 05, 05).getName()).isEqualTo("Aveiro");
    }

    @Test
    public void findByInvalidDate() throws Exception {
        assertThat(cityService.getCityByCoordinatesAndDay(11111, 11111, 2021, 05, 05)).isNull();
    }

    @Test
    public void findByInvalidCoordinatesAndDate() throws Exception {
        assertThat(cityService.getCityByCoordinatesAndDay(11111, 11111, 2021, 05, 50)).isNull();
    }
}
