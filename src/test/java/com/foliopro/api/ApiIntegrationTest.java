package com.foliopro.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foliopro.api.dto.ApiDtos.*;
import com.foliopro.api.repository.WatchlistRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest @AutoConfigureMockMvc @ActiveProfiles("test")
class ApiIntegrationTest {
 @Autowired MockMvc mvc; @Autowired ObjectMapper mapper; @Autowired WatchlistRepository watchlists; @Autowired JdbcTemplate jdbc;
 @Test void createsAndPagesWatchlists() throws Exception {
   String response=mvc.perform(post("/api/v1/watchlists").contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Test list\",\"market\":\"US\",\"userId\":1}"))
    .andExpect(status().isOk()).andExpect(jsonPath("$.id").exists()).andReturn().getResponse().getContentAsString();
   long id=mapper.readTree(response).get("id").asLong();
   org.junit.jupiter.api.Assertions.assertEquals(1L,
     jdbc.queryForObject("SELECT user_id FROM watchlists_usa WHERE id=?",Long.class,id));
   org.junit.jupiter.api.Assertions.assertEquals(1L,mapper.readTree(response).get("userId").asLong());
  mvc.perform(post("/api/v1/watchlists/"+id+"/items").contentType(MediaType.APPLICATION_JSON)
      .content("{\"items\":[{\"symbol\":\"AAPL\",\"name\":\"Apple Inc.\",\"isFavorite\":true}]}"))
    .andExpect(status().isOk()).andExpect(jsonPath("$[0].isFavorite").value(true));
  org.junit.jupiter.api.Assertions.assertEquals(1,
    jdbc.queryForObject("SELECT COUNT(*) FROM watchlist_items_usa WHERE watchlist_id=? AND is_favorite=TRUE",Integer.class,id));
  mvc.perform(get("/api/v1/watchlists?market=US&page=0&size=15")).andExpect(status().isOk()).andExpect(jsonPath("$.page").value(0)).andExpect(jsonPath("$.size").value(15));
 }
 @Test void defaultsUsaWatchlistUserIdToOneWhenOmitted() throws Exception {
   String response=mvc.perform(post("/api/v1/watchlists").contentType(MediaType.APPLICATION_JSON)
       .content("{\"name\":\"Default user list\",\"market\":\"US\"}"))
     .andExpect(status().isOk()).andExpect(jsonPath("$.userId").value(1))
     .andReturn().getResponse().getContentAsString();
   long id=mapper.readTree(response).get("id").asLong();
   org.junit.jupiter.api.Assertions.assertEquals(1L,
     jdbc.queryForObject("SELECT user_id FROM watchlists_usa WHERE id=?",Long.class,id));
 }
 @Test void listsPaginatedWatchlistsWithAllTickerItems() throws Exception {
  String response=mvc.perform(post("/api/v1/watchlists").contentType(MediaType.APPLICATION_JSON)
      .content("{\"name\":\"Paginated ticker list\",\"market\":\"US\"}"))
    .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
  long id=mapper.readTree(response).get("id").asLong();
  mvc.perform(post("/api/v1/watchlists/"+id+"/items").contentType(MediaType.APPLICATION_JSON)
      .content("{\"items\":[{\"symbol\":\"AAPL\",\"name\":\"Apple Inc.\",\"isFavorite\":true},{\"symbol\":\"MSFT\",\"name\":\"Microsoft Corporation\"}]}"))
    .andExpect(status().isOk());

  String page=mvc.perform(get("/api/v1/watchlists").param("market","US").param("page","0").param("size","100"))
    .andExpect(status().isOk()).andExpect(jsonPath("$.page").value(0))
    .andExpect(jsonPath("$.size").value(100)).andExpect(jsonPath("$.totalElements").isNumber())
    .andExpect(jsonPath("$.totalPages").isNumber()).andReturn().getResponse().getContentAsString();
  var content=mapper.readTree(page).get("content");
  var matching=java.util.stream.StreamSupport.stream(content.spliterator(),false)
      .filter(entry->entry.get("id").asLong()==id).findFirst().orElseThrow();
  org.junit.jupiter.api.Assertions.assertEquals("Paginated ticker list",matching.get("name").asText());
  org.junit.jupiter.api.Assertions.assertEquals("AAPL",matching.get("items").get(0).get("symbol").asText());
  org.junit.jupiter.api.Assertions.assertEquals("Apple Inc.",matching.get("items").get(0).get("name").asText());
  org.junit.jupiter.api.Assertions.assertTrue(matching.get("items").get(0).get("isFavorite").asBoolean());
  org.junit.jupiter.api.Assertions.assertEquals("MSFT",matching.get("items").get(1).get("symbol").asText());
 }
 @Test void validatesWatchlistAndBulkDuplicateSymbols() throws Exception {
  mvc.perform(post("/api/v1/watchlists").contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"\",\"market\":\"bad\"}"))
   .andExpect(status().isBadRequest()).andExpect(jsonPath("$.details").exists());
  mvc.perform(post("/api/v1/watchlists").contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Dups\",\"market\":\"US\"}"));
  Long id=watchlists.findAll().get(0).getId();
  mvc.perform(post("/api/v1/watchlists/"+id+"/items").contentType(MediaType.APPLICATION_JSON).content("{\"items\":[{\"symbol\":\"AAPL\",\"name\":\"Apple\"},{\"symbol\":\"aapl\",\"name\":\"Apple duplicate\"}]}"))
   .andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Duplicate symbol")));
 }
 @Test void importsColumnOrientedWatchlistsFromCsv() throws Exception {
  long usaBefore=jdbc.queryForObject("SELECT COUNT(*) FROM watchlists_usa",Long.class);
  long usaItemsBefore=jdbc.queryForObject("SELECT COUNT(*) FROM watchlist_items_usa",Long.class);
  MockMultipartFile file=new MockMultipartFile("file","watchlists.csv","text/csv",
   "\uFEFFGrowth Picks,\"Semiconductors, AI\"\r\nAAPL,NVDA\r\nMSFT,\r\n".getBytes(java.nio.charset.StandardCharsets.UTF_8));
   mvc.perform(multipart("/api/v1/watchlists/import?market=USA&userId=1").file(file))
   .andExpect(status().isCreated())
   .andExpect(jsonPath("$.watchlistsCreated").value(2))
   .andExpect(jsonPath("$.itemsCreated").value(3))
   .andExpect(jsonPath("$.watchlistNames[0]").value("Growth Picks"))
   .andExpect(jsonPath("$.watchlistNames[1]").value("Semiconductors, AI"));
  org.junit.jupiter.api.Assertions.assertEquals(usaBefore+2,
   jdbc.queryForObject("SELECT COUNT(*) FROM watchlists_usa",Long.class));
   org.junit.jupiter.api.Assertions.assertEquals(usaItemsBefore+3,
    jdbc.queryForObject("SELECT COUNT(*) FROM watchlist_items_usa",Long.class));
   org.junit.jupiter.api.Assertions.assertEquals(1L,
    jdbc.queryForObject("SELECT MIN(user_id) FROM watchlists_usa",Long.class));
 }
 @Test void importsIntoIndiaWatchlistTable() throws Exception {
  long indiaBefore=jdbc.queryForObject("SELECT COUNT(*) FROM watchlists_india",Long.class);
  long indiaItemsBefore=jdbc.queryForObject("SELECT COUNT(*) FROM watchlist_items_india",Long.class);
  MockMultipartFile file=new MockMultipartFile("file","india.csv","text/csv",
   "India Picks\nRELIANCE\n".getBytes(java.nio.charset.StandardCharsets.UTF_8));
  mvc.perform(multipart("/api/v1/watchlists/import?market=India").file(file))
   .andExpect(status().isCreated())
   .andExpect(jsonPath("$.watchlistsCreated").value(1))
   .andExpect(jsonPath("$.itemsCreated").value(1));
  org.junit.jupiter.api.Assertions.assertEquals(indiaBefore+1,
   jdbc.queryForObject("SELECT COUNT(*) FROM watchlists_india",Long.class));
  org.junit.jupiter.api.Assertions.assertEquals(indiaItemsBefore+1,
   jdbc.queryForObject("SELECT COUNT(*) FROM watchlist_items_india",Long.class));
 }
 @Test void rejectsDuplicateSymbolsBeforeCreatingImportedWatchlists() throws Exception {
  long before=watchlists.count();
  MockMultipartFile file=new MockMultipartFile("file","duplicates.csv","text/csv",
   "Duplicate Test\nAAPL\naapl\n".getBytes(java.nio.charset.StandardCharsets.UTF_8));
  mvc.perform(multipart("/api/v1/watchlists/import").file(file))
   .andExpect(status().isBadRequest())
   .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Duplicate symbol")));
  org.junit.jupiter.api.Assertions.assertEquals(before,watchlists.count());
 }
 @Test void returnsSampleDashboard(){try{mvc.perform(get("/api/v1/dashboard")).andExpect(status().isOk()).andExpect(jsonPath("$.sampleData").value(true)).andExpect(jsonPath("$.usAllocation").isArray());}catch(Exception e){throw new RuntimeException(e);}}
}
