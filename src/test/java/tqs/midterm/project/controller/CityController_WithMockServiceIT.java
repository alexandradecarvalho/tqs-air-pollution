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

@WebMvcTest(CityController.class)
public class CityController_WithMockServiceIT {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private CityService service;

    @Test
    public void getIndex() throws Exception {
        this.mvc.perform(get("/")).andExpect(status().isOk()).andExpect(view().name("index"));
    }

    @Test
    public void getNames() throws Exception {
        this.mvc.perform(get("/names")).andExpect(status().isOk()).andExpect(view().name("indexbyname"));
    }

    @Test
    public void getError() throws Exception {
        this.mvc.perform(get("/error")).andExpect(status().isOk()).andExpect(view().name("error"));
    }
}
