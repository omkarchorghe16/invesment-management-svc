package com.foliopro.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foliopro.api.dto.ApiDtos.*;
import com.foliopro.api.repository.WatchlistRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest @AutoConfigureMockMvc @ActiveProfiles("test")
class ApiIntegrationTest {
 @Autowired MockMvc mvc; @Autowired ObjectMapper mapper; @Autowired WatchlistRepository watchlists;
 @Test void createsAndPagesWatchlists() throws Exception {
  mvc.perform(post("/api/v1/watchlists").contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Test list\",\"market\":\"US\"}"))
    .andExpect(status().isOk()).andExpect(jsonPath("$.id").exists());
  mvc.perform(get("/api/v1/watchlists?market=US&page=0&size=15")).andExpect(status().isOk()).andExpect(jsonPath("$.page").value(0)).andExpect(jsonPath("$.size").value(15));
 }
 @Test void validatesWatchlistAndBulkDuplicateSymbols() throws Exception {
  mvc.perform(post("/api/v1/watchlists").contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"\",\"market\":\"bad\"}"))
   .andExpect(status().isBadRequest()).andExpect(jsonPath("$.details").exists());
  mvc.perform(post("/api/v1/watchlists").contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Dups\",\"market\":\"US\"}"));
  Long id=watchlists.findAll().get(0).getId();
  mvc.perform(post("/api/v1/watchlists/"+id+"/items").contentType(MediaType.APPLICATION_JSON).content("{\"items\":[{\"symbol\":\"AAPL\",\"name\":\"Apple\"},{\"symbol\":\"aapl\",\"name\":\"Apple duplicate\"}]}"))
   .andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Duplicate symbol")));
 }
 @Test void returnsSampleDashboard(){try{mvc.perform(get("/api/v1/dashboard")).andExpect(status().isOk()).andExpect(jsonPath("$.sampleData").value(true)).andExpect(jsonPath("$.usAllocation").isArray());}catch(Exception e){throw new RuntimeException(e);}}
}
