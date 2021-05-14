package tqs.midterm.project.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tqs.midterm.project.JsonUtil;
import tqs.midterm.project.cache.Cache;
import tqs.midterm.project.model.AirQuality;
import tqs.midterm.project.model.City;
import tqs.midterm.project.service.CityService;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RESTController.class)
public class RESTController_WithMockServiceIT {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private CityService service;
    private City c;

    @BeforeEach
    public void setUp(){
        c = new City(40.6443, -8.6455, "Aveiro", "Portugal");
        c.setQuality(new AirQuality(10.0,10.0,10.0,10.0,10.0,10.0));
    }

    @Test
    public void getSearch() throws Exception {
        when(service.getCityByName(Mockito.any())).thenReturn(c);

        mvc.perform(get("/api/search").contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(c))).andExpect(status().isOk()).andExpect(content().string(containsString("Aveiro")));

        verify(service, times(1)).getCityByName(Mockito.any());
    }

    @Test
    public void getCoords() throws Exception {
        when(service.getCityByCoordinatesAndDay(Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(c);

        mvc.perform(get("/api/hourly").contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(c))).andExpect(status().isOk());

        verify(service, times(1)).getCityByCoordinatesAndDay(Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt());
    }

    @Test
    public void getCache() throws Exception {
        Cache cache = new Cache();
        cache.save(c, System.currentTimeMillis());

        cache.get(40.6443, -8.6455);

        when(service.getCacheStats()).thenReturn(cache);

        mvc.perform(get("/api/cache").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andExpect(content().string(containsString("requests=1&hits=1")));

        verify(service, times(1)).getCacheStats();
    }
}
