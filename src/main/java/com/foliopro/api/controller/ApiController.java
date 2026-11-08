package com.foliopro.api.controller;

import com.foliopro.api.dto.ApiDtos.*;
import com.foliopro.api.service.FolioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/v1") @Validated
@Tag(name = "Folio Pro", description = "Portfolio, watchlist, preferences, instrument, and dashboard endpoints")
public class ApiController {
 private final FolioService service; private final com.foliopro.api.service.SampleMarketDataProvider marketData;
 public ApiController(FolioService s,com.foliopro.api.service.SampleMarketDataProvider m){service=s;marketData=m;}
 @Operation(summary="List portfolios",description="Returns portfolios using zero-based pagination; page size is limited to 100.")
 @GetMapping("/portfolios") public PageResponse<PortfolioResponse> portfolios(@RequestParam(defaultValue="0") @Min(0) int page,@RequestParam(defaultValue="15") @Min(1) @Max(100) int size){return service.listPortfolios(page,size);}
 @Operation(summary="Create a portfolio",description="Creates a portfolio and its optional holdings.")
 @PostMapping("/portfolios") public PortfolioResponse createPortfolio(@Valid @RequestBody PortfolioRequest r){return service.savePortfolio(null,r);}
 @Operation(summary="Get a portfolio",description="Returns one portfolio and its holdings.")
 @GetMapping("/portfolios/{id}") public PortfolioResponse portfolio(@PathVariable @Positive long id){return service.getPortfolio(id);}
 @Operation(summary="Replace a portfolio",description="Replaces the portfolio fields and holdings.")
 @PutMapping("/portfolios/{id}") public PortfolioResponse updatePortfolio(@PathVariable @Positive long id,@Valid @RequestBody PortfolioRequest r){return service.savePortfolio(id,r);}
 @Operation(summary="Delete a portfolio")
 @DeleteMapping("/portfolios/{id}") public void deletePortfolio(@PathVariable @Positive long id){service.deletePortfolio(id);}
 @Operation(summary="List watchlists",description="Returns paginated watchlists, optionally filtered by US or India market.")
 @GetMapping("/watchlists") public PageResponse<WatchlistResponse> watchlists(@RequestParam(required=false) @Pattern(regexp="US|India") String market,@RequestParam(defaultValue="0") @Min(0) int page,@RequestParam(defaultValue="15") @Min(1) @Max(100) int size){return service.listWatchlists(market,page,size);}
 @Operation(summary="Create a watchlist",description="Creates an empty watchlist for the selected market.")
 @PostMapping("/watchlists") public WatchlistResponse createWatchlist(@Valid @RequestBody WatchlistRequest r){return service.createWatchlist(r);}
 @Operation(summary="Get a watchlist",description="Returns a watchlist with a page of its items.")
 @GetMapping("/watchlists/{id}") public WatchlistResponse watchlist(@PathVariable @Positive long id,@RequestParam(defaultValue="0") @Min(0) int page,@RequestParam(defaultValue="15") @Min(1) @Max(100) int size){return service.getWatchlist(id,page,size);}
 @Operation(summary="Rename a watchlist",description="Updates the watchlist name. Its market cannot be changed.")
 @PatchMapping("/watchlists/{id}") public WatchlistResponse renameWatchlist(@PathVariable @Positive long id,@Valid @RequestBody WatchlistRequest r){return service.renameWatchlist(id,r);}
 @Operation(summary="Delete a watchlist",description="Deletes the watchlist and its items.")
 @DeleteMapping("/watchlists/{id}") public void deleteWatchlist(@PathVariable @Positive long id){service.deleteWatchlist(id);}
 @Operation(summary="Add items to a watchlist",description="Adds up to 100 items atomically. Duplicate symbols in a list are rejected.")
 @PostMapping("/watchlists/{id}/items") public List<WatchlistItemResponse> addItems(@PathVariable @Positive long id,@Valid @RequestBody BulkItemsRequest r){return service.addItems(id,r);}
 @Operation(summary="Update a watchlist item",description="Updates an item identified by its symbol.")
 @PutMapping("/watchlists/{id}/items/{symbol}") public WatchlistItemResponse updateItem(@PathVariable @Positive long id,@PathVariable String symbol,@Valid @RequestBody WatchlistItemRequest r){return service.updateItem(id,symbol,r);}
 @Operation(summary="Remove an item from a watchlist")
 @DeleteMapping("/watchlists/{id}/items/{symbol}") public void removeItem(@PathVariable @Positive long id,@PathVariable String symbol){service.removeItem(id,symbol);}
 @Operation(summary="Get preferences",description="Returns preferences for the current development workspace.")
 @GetMapping("/preferences") public PreferencesResponse preferences(){return service.getPreferences();}
 @Operation(summary="Save preferences",description="Replaces favorite watchlists, favorite symbols, and visible watchlist columns.")
 @PutMapping("/preferences") public PreferencesResponse savePreferences(@Valid @RequestBody PreferencesRequest r){return service.savePreferences(r);}
 @Operation(summary="Search instruments",description="Searches the sample instrument catalog. Returned prices and instrument data are illustrative.")
 @GetMapping("/instruments") public List<InstrumentResponse> instruments(@RequestParam(required=false) @Size(max=80) String q,@RequestParam(required=false) @Pattern(regexp="US|India") String market){return marketData.search(q,market);}
 @Operation(summary="Get dashboard data",description="Returns dashboard summary and sample market data.")
 @GetMapping("/dashboard") public DashboardResponse dashboard(){return DashboardSamples.response();}
}
