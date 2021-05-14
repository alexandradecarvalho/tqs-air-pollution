package tqs.midterm.project.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import tqs.midterm.project.ProjectApplication;
import tqs.midterm.project.service.CityService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = ProjectApplication.class)
@AutoConfigureMockMvc
public class CityControllerTemplate {
    @Autowired
    private MockMvc mvc;

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
