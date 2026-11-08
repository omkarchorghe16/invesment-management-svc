package com.foliopro.api.controller;

import com.foliopro.api.dto.ApiDtos.*;
import com.foliopro.api.service.FolioService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/v1") @Validated
public class ApiController {
 private final FolioService service; private final com.foliopro.api.service.SampleMarketDataProvider marketData;
 public ApiController(FolioService s,com.foliopro.api.service.SampleMarketDataProvider m){service=s;marketData=m;}
 @GetMapping("/portfolios") public PageResponse<PortfolioResponse> portfolios(@RequestParam(defaultValue="0") @Min(0) int page,@RequestParam(defaultValue="15") @Min(1) @Max(100) int size){return service.listPortfolios(page,size);}
 @PostMapping("/portfolios") public PortfolioResponse createPortfolio(@Valid @RequestBody PortfolioRequest r){return service.savePortfolio(null,r);}
 @GetMapping("/portfolios/{id}") public PortfolioResponse portfolio(@PathVariable @Positive long id){return service.getPortfolio(id);}
 @PutMapping("/portfolios/{id}") public PortfolioResponse updatePortfolio(@PathVariable @Positive long id,@Valid @RequestBody PortfolioRequest r){return service.savePortfolio(id,r);}
 @DeleteMapping("/portfolios/{id}") public void deletePortfolio(@PathVariable @Positive long id){service.deletePortfolio(id);}
 @GetMapping("/watchlists") public PageResponse<WatchlistResponse> watchlists(@RequestParam(required=false) @Pattern(regexp="US|India") String market,@RequestParam(defaultValue="0") @Min(0) int page,@RequestParam(defaultValue="15") @Min(1) @Max(100) int size){return service.listWatchlists(market,page,size);}
 @PostMapping("/watchlists") public WatchlistResponse createWatchlist(@Valid @RequestBody WatchlistRequest r){return service.createWatchlist(r);}
 @GetMapping("/watchlists/{id}") public WatchlistResponse watchlist(@PathVariable @Positive long id,@RequestParam(defaultValue="0") @Min(0) int page,@RequestParam(defaultValue="15") @Min(1) @Max(100) int size){return service.getWatchlist(id,page,size);}
 @PatchMapping("/watchlists/{id}") public WatchlistResponse renameWatchlist(@PathVariable @Positive long id,@Valid @RequestBody WatchlistRequest r){return service.renameWatchlist(id,r);}
 @DeleteMapping("/watchlists/{id}") public void deleteWatchlist(@PathVariable @Positive long id){service.deleteWatchlist(id);}
 @PostMapping("/watchlists/{id}/items") public List<WatchlistItemResponse> addItems(@PathVariable @Positive long id,@Valid @RequestBody BulkItemsRequest r){return service.addItems(id,r);}
 @PutMapping("/watchlists/{id}/items/{symbol}") public WatchlistItemResponse updateItem(@PathVariable @Positive long id,@PathVariable String symbol,@Valid @RequestBody WatchlistItemRequest r){return service.updateItem(id,symbol,r);}
 @DeleteMapping("/watchlists/{id}/items/{symbol}") public void removeItem(@PathVariable @Positive long id,@PathVariable String symbol){service.removeItem(id,symbol);}
 @GetMapping("/preferences") public PreferencesResponse preferences(){return service.getPreferences();}
 @PutMapping("/preferences") public PreferencesResponse savePreferences(@Valid @RequestBody PreferencesRequest r){return service.savePreferences(r);}
 @GetMapping("/instruments") public List<InstrumentResponse> instruments(@RequestParam(required=false) @Size(max=80) String q,@RequestParam(required=false) @Pattern(regexp="US|India") String market){return marketData.search(q,market);}
 @GetMapping("/dashboard") public DashboardResponse dashboard(){return DashboardSamples.response();}
}
