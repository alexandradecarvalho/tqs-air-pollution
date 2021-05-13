package tqs.midterm.project.cache;

import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tqs.midterm.project.model.AirQuality;
import tqs.midterm.project.model.City;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class CacheUnitTest {
    private Cache cache;

    @BeforeEach
    void setUp(){
        cache = new Cache();

        City valid_city = new City(41.14961, -8.61099, "Porto", "Portugal");
        valid_city.setQuality(new AirQuality(64.31, 3.0, 24.68, 7.79, 72.91, 21.27));
        cache.save(valid_city, System.currentTimeMillis()); // city cached recently - still valid

        City expired_city = new City(40.6443, -8.6455, "Aveiro", "Portugal");
        expired_city.setQuality(new AirQuality(182.67, 3.0, 8.48, 0.02, 17.04, 6.88));
        cache.save(expired_city, System.currentTimeMillis() - 3600000); // city cached 3600000ms ago - just turned invalid
    }

    @Test
    public void searchForMissingCity() {
        assertNull(cache.get(1111,1111));
        assertEquals(1, cache.getNumberOfRequests());
        assertEquals(0, cache.getNumberOfHits());
        assertEquals(1, cache.getNumberOfMisses());
        assertEquals(1, cache.size());  // outlived item has been removed
    }

    @Test
    public void searchForValidCity() {
        assertNotNull(cache.get(41.14961,-8.61099));
        assertEquals(1, cache.getNumberOfRequests());
        assertEquals(1, cache.getNumberOfHits());
        assertEquals(0, cache.getNumberOfMisses());
    }

    @Test
    public void searchForInvalidCity() {
        assertNull(cache.get(40.6443,-8.6455));
        assertEquals(1, cache.getNumberOfRequests());
        assertEquals(0, cache.getNumberOfHits());
        assertEquals(1, cache.getNumberOfMisses());
        assertEquals(1, cache.size());  // invalid city has been removed
    }

}
